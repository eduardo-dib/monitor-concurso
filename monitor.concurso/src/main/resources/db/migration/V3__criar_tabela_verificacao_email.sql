ALTER TABLE usuario ADD COLUMN email_verificado BOOLEAN NOT NULL DEFAULT FALSE;


UPDATE usuario SET email_verificado = TRUE;


CREATE SEQUENCE verificacao_email_SEQ START WITH 1 INCREMENT BY 50;

CREATE TABLE verificacao_email (
                                   id BIGINT PRIMARY KEY,
                                   codigo VARCHAR(255) NOT NULL,
                                   usuario_id BIGINT NOT NULL REFERENCES usuario(id),
                                   criado_em TIMESTAMP NOT NULL,
                                   expiracao TIMESTAMP NOT NULL,
                                   usado BOOLEAN NOT NULL DEFAULT FALSE
);