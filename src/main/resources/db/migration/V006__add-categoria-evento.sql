alter table evento
add column categoria_id int null;

alter table evento
add constraint fk_evento_categoria
foreign key (categoria_id) references categoria(id);