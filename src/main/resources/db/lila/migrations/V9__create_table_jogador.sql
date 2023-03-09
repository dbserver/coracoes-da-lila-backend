create table jogador (
     id uuid not null,
     posicao int4 not null,
     bonus_coracao_gra int4 not null,
     bonus_coracao_peq int4 not null,
     coracao_gra int4 not null,
     coracao_peq int4 not null,
     nome varchar(30) not null,
     pontos int4 not null,
     status int4 not null,
     is_host boolean not null,
     pontos_objetivo int4,
     primary key (id)
     );