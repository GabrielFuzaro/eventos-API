create table participante(
    id int primary key auto_increment,
    nome varchar(100) not null,
    email varchar(100) not null,
    evento_id int not null,
    foreign key (evento_id) references evento(id)
);