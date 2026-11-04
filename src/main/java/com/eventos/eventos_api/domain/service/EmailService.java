package com.eventos.eventos_api.domain.service;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.util.StringUtils;
import org.springframework.web.util.HtmlUtils;

import com.eventos.eventos_api.common.Privacidade;
import com.eventos.eventos_api.domain.event.NotificacaoEmailEvent;

@Service
public class EmailService { //Envia os emails informativos de inscrição, lista de espera e vaga liberada

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);
    private static final ZoneId FUSO_BRASILIA = ZoneId.of("America/Sao_Paulo");
    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm");

    private final ObjectProvider<JavaMailSender> mailSender;
    private final String host;
    private final String remetente;
    private final String nomeRemetente;

    public EmailService(
            ObjectProvider<JavaMailSender> mailSender,
            @Value("${spring.mail.host:}") String host,
            @Value("${email.remetente:}") String remetente,
            @Value("${spring.mail.username:}") String usuarioSmtp,
            @Value("${email.nome-remetente:Equipe de Eventos}") String nomeRemetente) {
        this.mailSender = mailSender;
        this.host = host;
        this.remetente = StringUtils.hasText(remetente) ? remetente : usuarioSmtp;
        this.nomeRemetente = nomeRemetente;
    }

    private boolean envioConfigurado() {
        return StringUtils.hasText(host) && StringUtils.hasText(remetente);
    }

    @EventListener(ApplicationReadyEvent.class) //Avisa na subida da API se os emails vão ou não ser enviados
    public void informarConfiguracao() {
        if (envioConfigurado()) {
            log.info("Envio de emails LIGADO: servidor {} com remetente {}", host, remetente);
        } else {
            log.warn("Envio de emails DESLIGADO: defina EMAIL_HOST, EMAIL_USUARIO e EMAIL_SENHA para enviar os emails de inscrição.");
        }
    }

    //Só depois do commit (se a inscrição falhar, ninguém recebe email) e em outra thread
    //(SMTP lento ou fora do ar não atrasa nem quebra a inscrição)
    @Async
    @TransactionalEventListener(fallbackExecution = true)
    public void aoNotificar(NotificacaoEmailEvent evento) {
        if (!envioConfigurado()) {
            log.warn("Email '{}' para {} não enviado: envio de emails desligado (EMAIL_HOST/EMAIL_USUARIO não configurados)",
                evento.getTipo(), Privacidade.mascararEmail(evento.getEmail()));
            return;
        }

        JavaMailSender sender = mailSender.getIfAvailable();
        if (sender == null) {
            return;
        }

        try {
            MimeMessage mensagem = sender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensagem, true, "UTF-8");
            helper.setFrom(remetente, nomeRemetente);
            helper.setTo(evento.getEmail());
            helper.setSubject(assunto(evento));
            helper.setText(textoSimples(evento), html(evento)); //Texto puro + HTML (o leitor de email escolhe)
            sender.send(mensagem);
            log.info("Email '{}' enviado para {}", evento.getTipo(), Privacidade.mascararEmail(evento.getEmail()));
        } catch (Exception e) {
            //Falha no envio não desfaz a inscrição: só registra no log (sem o email completo)
            log.warn("Falha ao enviar email '{}' para {}: {}", evento.getTipo(),
                Privacidade.mascararEmail(evento.getEmail()), e.getMessage());
        }
    }

    String assunto(NotificacaoEmailEvent e) {
        switch (e.getTipo()) {
            case ENTRADA_LISTA_ESPERA:
                return "Você está na lista de espera: " + e.getEventoNome();
            case VAGA_LIBERADA:
                return "Sua vaga foi confirmada: " + e.getEventoNome();
            default:
                return "Inscrição confirmada: " + e.getEventoNome();
        }
    }

    private String[] paragrafos(NotificacaoEmailEvent e) {
        switch (e.getTipo()) {
            case ENTRADA_LISTA_ESPERA:
                return new String[] {
                    "No momento, todas as vagas do evento \"" + e.getEventoNome() + "\" estão preenchidas, "
                        + "e você entrou na lista de espera na posição " + e.getPosicaoFila() + ".",
                    "Se alguma vaga for liberada, sua inscrição será confirmada automaticamente, por ordem de chegada, "
                        + "e você receberá um novo email avisando."
                };
            case VAGA_LIBERADA:
                return new String[] {
                    "Temos uma boa notícia: uma vaga foi liberada no evento \"" + e.getEventoNome() + "\" e, "
                        + "como você estava na lista de espera, sua inscrição foi confirmada.",
                    "Ficamos felizes em contar com a sua presença."
                };
            default:
                return new String[] {
                    "Sua inscrição no evento \"" + e.getEventoNome() + "\" foi confirmada.",
                    "Ficamos felizes em contar com a sua presença."
                };
        }
    }

    private String dataFormatada(NotificacaoEmailEvent e) {
        if (e.getEventoData() == null) {
            return "a definir";
        }
        return e.getEventoData().atZoneSameInstant(FUSO_BRASILIA).format(FORMATO_DATA) + " (horário de Brasília)";
    }

    String textoSimples(NotificacaoEmailEvent e) {
        StringBuilder sb = new StringBuilder();
        sb.append("Olá, ").append(e.getNome()).append("!\n\n");
        for (String p : paragrafos(e)) {
            sb.append(p).append("\n\n");
        }
        sb.append("Detalhes do evento\n");
        sb.append("Evento: ").append(e.getEventoNome()).append('\n');
        sb.append("Data: ").append(dataFormatada(e)).append('\n');
        sb.append("Local: ").append(e.getEventoLocal()).append("\n\n");
        sb.append("Este é um email automático, apenas informativo. Não é necessário respondê-lo.\n\n");
        sb.append("Atenciosamente,\n").append(nomeRemetente);
        return sb.toString();
    }

    String html(NotificacaoEmailEvent e) {
        //Tudo que vem do usuário (nome, evento, local) é escapado para não injetar HTML no email
        StringBuilder sb = new StringBuilder();
        sb.append("<div style=\"font-family:Arial,Helvetica,sans-serif;color:#1e293b;max-width:560px;margin:0 auto;line-height:1.5\">");
        sb.append("<p style=\"font-size:16px\">Olá, <strong>").append(esc(e.getNome())).append("</strong>!</p>");
        for (String p : paragrafos(e)) {
            sb.append("<p>").append(esc(p)).append("</p>");
        }
        sb.append("<table style=\"border-collapse:collapse;margin:16px 0;background:#f1f5f9;border-radius:8px\">");
        linha(sb, "Evento", e.getEventoNome());
        linha(sb, "Data", dataFormatada(e));
        linha(sb, "Local", e.getEventoLocal());
        sb.append("</table>");
        sb.append("<p style=\"color:#64748b;font-size:13px\">Este é um email automático, apenas informativo. Não é necessário respondê-lo.</p>");
        sb.append("<p>Atenciosamente,<br>").append(esc(nomeRemetente)).append("</p>");
        sb.append("</div>");
        return sb.toString();
    }

    private void linha(StringBuilder sb, String rotulo, String valor) {
        sb.append("<tr><td style=\"padding:6px 16px 6px 12px;color:#64748b\">").append(esc(rotulo))
          .append("</td><td style=\"padding:6px 12px 6px 0\"><strong>").append(esc(valor)).append("</strong></td></tr>");
    }

    private String esc(String valor) {
        return valor == null ? "" : HtmlUtils.htmlEscape(valor, "UTF-8");
    }
}
