-- PROCEDIMENTO PARA ALTERAR A TABELA tb_disponibilidade:

CREATE TABLE tb_disponibilidade_new (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    id_profissional INTEGER NOT NULL,
    id_posto INTEGER NOT NULL,
    dia_semana TEXT NOT NULL,
    hora_inicio TEXT NOT NULL,
    hora_fim TEXT NOT NULL,
    FOREIGN KEY (id_profissional) REFERENCES tb_profissional(id_pessoa),
    FOREIGN KEY (id_posto) REFERENCES tb_posto(id)
);

DROP TABLE tb_disponibilidade;

ALTER TABLE tb_disponibilidade_new RENAME TO tb_disponibilidade;

-- PROCEDIMENTO PARA ALTERAR A TABELA tb_agendamento:

ALTER TABLE tb_agendamento RENAME TO tb_agendamento_old;

CREATE TABLE tb_agendamento (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    id_paciente INTEGER NOT NULL,
    id_profissional INTEGER NOT NULL,
    id_posto INTEGER NOT NULL,
    data_hora_inicio TEXT NOT NULL,
    status TEXT DEFAULT 'AGENDADA',
    laudo_resultado TEXT,
    FOREIGN KEY (id_paciente) REFERENCES tb_paciente(id),
    FOREIGN KEY (id_profissional) REFERENCES tb_profissional(id_pessoa),
    FOREIGN KEY (id_posto) REFERENCES tb_posto(id)
);

DROP TABLE tb_agendamento_old;