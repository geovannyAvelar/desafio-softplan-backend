--liquibase formatted sql

--changeset Geovanny de Avelar Carneiro:tabelas
--comment Criação das tabelas
CREATE TABLE foto (
    id bigserial PRIMARY KEY,
    bytes BYTEA NOT NULL
);

CREATE TABLE pessoa (
	id bigserial PRIMARY KEY,
	nome varchar(150) NULL,
	email varchar(150) NULL,
	cpf varchar(20) NULL UNIQUE,
	foto_id int8,
	data_nascimento timestamp NOT NULL,
	CONSTRAINT foto_pessoa_fk FOREIGN KEY(foto_id) REFERENCES foto(id)
);