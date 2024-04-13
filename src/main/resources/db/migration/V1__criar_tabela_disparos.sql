CREATE TABLE DISPAROS (
    ID                              NUMERIC(19, 0)  NOT NULL,
    I_CLIENTES                      NUMERIC(19, 0),
    JA_FOI_DISPARADO                BOOLEAN DEFAULT false,
    AUD_CRIADO_POR                  VARCHAR(16),
    AUD_DH_CRIACAO                  TIMESTAMP(6),
    AUD_DH_ALTERACAO                TIMESTAMP(6),
    AUD_ALTERADO_POR                VARCHAR(16),
    AUD_VERSAO                      NUMERIC(8) NOT NULL DEFAULT 0
);
