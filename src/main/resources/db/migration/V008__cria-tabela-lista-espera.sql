create table lista_espera(
    id int primary key auto_increment,
    nome varchar(100) not null,
    email varchar(100) not null,
    data_inscricao datetime not null,
    evento_id int not null,
    foreign key (evento_id) references evento(id)
);
