CREATE SEQUENCE IF NOT EXISTS recuperacao_senha_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE IF NOT EXISTS recuperacao_senha (
                                                 id BIGINT DEFAULT nextval('recuperacao_senha_seq') PRIMARY KEY,
    token VARCHAR(255) NOT NULL UNIQUE,
    usuario_id BIGINT NOT NULL,
    criadoem TIMESTAMP NOT NULL,
    expiracao TIMESTAMP NOT NULL,
    usado BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_recuperacao_usuario FOREIGN KEY (usuario_id) REFERENCES usuario(id) ON DELETE CASCADE
    );