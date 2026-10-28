package com.eventos.eventos_api.domain.service;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.eventos.eventos_api.domain.exception.EntidadeNaoEncontradaException;
import com.eventos.eventos_api.domain.exception.NegocioException;
import com.eventos.eventos_api.domain.model.Participante;
import com.eventos.eventos_api.domain.model.StatusEvento;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import com.eventos.eventos_api.domain.model.Evento;
import javax.transaction.Transactional;
import com.eventos.eventos_api.domain.repository.ParticipanteRepository;
import com.eventos.eventos_api.model.input.ParticipanteInput;
import com.eventos.eventos_api.model.output.ParticipanteOutput;

import lombok.AllArgsConstructor;

@AllArgsConstructor //Gera um constructor com todos os argumentos/variaveis
@Service //Declara a classe como um service
public class ParticipanteService {
    
    private ParticipanteRepository participanteRepository;
    private CrudEventoService crudEventoService;

    @Transactional
    public List<Participante> listarParticipantes(){  //Método para listar todos os participantes
        return participanteRepository.findAll();
    }

    public List<Participante> listarPorEvento(Long eventoId){
        return participanteRepository.findByEventoId(eventoId);
    }

    public Page<Participante> listarTodosPaginacao(Pageable pageable){  //Método oara listar todos os partiipantes só que com paginação
        return participanteRepository.findAll(pageable);
    }

    @Transactional
    public Participante listarParticipantePorId(Long participanteId){ //Método para buscar um partiicpante pelo id
        return participanteRepository.findById(participanteId)
        .orElseThrow(() -> new EntidadeNaoEncontradaException("Participante Não Encontrado!")); //Lança exception se o participante nao existir
    }

    @Transactional
    public Long contarParticipantesEvento(Long eventoId) {
        crudEventoService.buscarUnicoEvento(eventoId); // 404 se o evento não existir
        return participanteRepository.countByEventoId(eventoId);
    }

    @Transactional
    public Participante cadastrarParticipante(ParticipanteInput input){   //Registra um participante através do input de participante
        Evento evento = crudEventoService.buscarUnicoEvento(input.getEventoId().getId());  //Busca o evento que o participante será cadastrado
        Participante participante = new Participante(); //Cria um participante novo com tudo vazio

        if (evento.getStatus() == StatusEvento.LOTADO) {
            throw new NegocioException("Esse evento já está lotado!"); //Verifica se o evento já está lotado para nao receber mais participantes
        }

        if(participanteRepository.existsByEmailAndEventoId(input.getEmail(), evento.getId())){
            throw new NegocioException("Esse email já está inscrito nesse evento!");
        }

        participante.setEvento(evento); //Passa o evento para o participante
        participante.setNome(input.getNome()); //registra o nome do participante através do input
        participante.setEmail(input.getEmail()); //registra o email do particiipante atravéd do input

        Participante participanteSalvo = participanteRepository.save(participante); //Salva o participante no banco

        long totalInscritos = participanteRepository.countByEventoId(evento.getId()); //Verifica quantos participantes estão cadastrados no evento

        if (totalInscritos >= evento.getCapacidade_maxima()) {
            evento.setStatus(StatusEvento.LOTADO);             //Muda o evento para lotado caso o total de participantes se iguale ou ultrapasse a capacidade maxima do evento
        }
        if(totalInscritos < evento.getCapacidade_maxima()){
            evento.setStatus(StatusEvento.ABERTO); //Muda o evento para aberto caso o total de participantes seja menor que a capacidade maxima do evento
        }

        return participanteSalvo;
    }

    @Transactional
    public void excluirParticipante(Long participanteId){ //Método para excluir um praticipante
        Participante participante = participanteRepository.findById(participanteId) //Procura um participante pelo id
            .orElseThrow(() -> new EntidadeNaoEncontradaException("Participante Não Encontrado!")); //Lança exception caso o participante nao exista

        Evento evento = participante.getEvento(); //pega o evento do evento do participante

        participanteRepository.deleteById(participanteId); //exclui o participante pelo id

        if (evento.getStatus() == StatusEvento.LOTADO) {
            evento.setStatus(StatusEvento.ABERTO); //Muda o status para aberto caso após a exclusão caso o evento esteja lotado
        }
    }

    @Transactional
    public Page<Participante> buscarParticipantesDoEvento(Long eventoId, Pageable pageable){ //Lista os participantes do evento com paginação
        Page<Participante> participantesDoEvento = participanteRepository.findByEventoId(eventoId, pageable);

        return participantesDoEvento;
    }



    //EXCEL

    private static final String[] COLUNAS = {"Nome", "Email"};

    public byte[] gerarExcel(List<Participante> participantes){
        if(participantes == null || participantes.isEmpty()){
            throw new IllegalArgumentException("Nenhum participante para exportar");
        }

        try(Workbook workbook = new XSSFWorkbook();
        ByteArrayOutputStream saida = new ByteArrayOutputStream()){

            Sheet sheet = workbook.createSheet("Participantes");
            CreationHelper helper = workbook.getCreationHelper();
            XSSFColor azul = new XSSFColor(new java.awt.Color(4, 45, 98), null);


            Font fonteTitulo = workbook.createFont();
            fonteTitulo.setBold(true);
            fonteTitulo.setFontHeightInPoints((short) 16);
            fonteTitulo.setColor(IndexedColors.WHITE.getIndex());

            CellStyle estiloTitulo = workbook.createCellStyle();
            estiloTitulo.setFont(fonteTitulo);
            estiloTitulo.setFillForegroundColor(azul);
            estiloTitulo.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            estiloTitulo.setVerticalAlignment(VerticalAlignment.CENTER);
            estiloTitulo.setAlignment(HorizontalAlignment.CENTER);

            Row linhaTitulo = sheet.createRow(0);
            linhaTitulo.setHeightInPoints(25);
            for (int col = 0; col<COLUNAS.length; col++){
                linhaTitulo.createCell(col).setCellStyle(estiloTitulo);
            }
            linhaTitulo.getCell(0).setCellValue("Relatório de Produtos");
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, COLUNAS.length - 1));

            Font fonteCabecalho = workbook.createFont();
            fonteCabecalho.setBold(true);
            fonteCabecalho.setColor(IndexedColors.WHITE.getIndex());

            CellStyle estiloCabecalho = workbook.createCellStyle();
            estiloCabecalho.cloneStyleFrom(estiloTitulo);
            estiloCabecalho.setFont(fonteCabecalho);

            Row linhaCabecalho = sheet.createRow(1);
            for(int col = 0; col < COLUNAS.length; col++){
                Cell cabecalho = linhaCabecalho.createCell(col);
                cabecalho.setCellValue(COLUNAS[col].toUpperCase());
                cabecalho.setCellStyle(estiloCabecalho);
            }

            CellStyle estiloTexto = workbook.createCellStyle();
            estiloTexto.setBorderBottom(BorderStyle.THIN);
            estiloTexto.setBottomBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
            estiloTexto.setAlignment(HorizontalAlignment.CENTER);
            estiloTexto.setVerticalAlignment(VerticalAlignment.CENTER);

            int linha = 2;
            for(Participante participante : participantes) {
                Row row = sheet.createRow(linha++);

                Cell nome = row.createCell(0);
                nome.setCellValue(participante.getNome());
                nome.setCellStyle(estiloTexto);

                Cell email = row.createCell(1);
                email.setCellValue(participante.getEmail());
                email.setCellStyle(estiloTexto);
            }

            for(int col = 0; col< COLUNAS.length; col++){
                sheet.autoSizeColumn(col);
                sheet.setColumnWidth(col, Math.min(sheet.getColumnWidth(col) + 1000, 255*256));
            }

            workbook.write(saida);
            return saida.toByteArray();

        } catch (IOException e){
            throw new IllegalStateException("Erro ao gerar Excel", e);
        }
    }
    
}
