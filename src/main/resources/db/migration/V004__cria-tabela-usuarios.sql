create table usuario(
    id int primary key auto_increment,
    username varchar(50) not null unique,
    senha varchar(255) not null,
    role varchar(20) not null default 'USER'
);