create table carta_objetivo (
    id uuid not null,
    categoria varchar(100),
    texto_regra varchar(255) not null,
    texto_tematico varchar(255) not null,
    pontos int4 not null,
    tipo_contagem int4 not null,
    tipo varchar(20),
    primary key (id)
    );