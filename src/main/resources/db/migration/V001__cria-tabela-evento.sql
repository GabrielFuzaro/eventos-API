create table evento(
    id int primary key auto_increment,
    nome varchar(30) not null,
    data date not null,
    local varchar(30) not null,
    capacidade_maxima int not null,
    status varchar(20) not null
);