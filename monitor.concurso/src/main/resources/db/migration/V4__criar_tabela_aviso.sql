CREATE SEQUENCE aviso_SEQ START WITH 1 INCREMENT BY 50;

CREATE TABLE aviso (
                       id         BIGINT PRIMARY KEY,
                       titulo     VARCHAR(255) NOT NULL,
                       mensagem   TEXT NOT NULL,
                       tipo       VARCHAR(20) NOT NULL,
                       ativo      BOOLEAN NOT NULL DEFAULT TRUE,
                       criado_em  TIMESTAMP NOT NULL,
                       expira_em  TIMESTAMP,
                       CONSTRAINT aviso_tipo_check CHECK (tipo IN ('INFO', 'ALERTA', 'MANUTENCAO'))
);