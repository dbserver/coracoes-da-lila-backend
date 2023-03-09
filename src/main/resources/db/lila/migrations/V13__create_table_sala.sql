create table sala (
     id uuid not null,
     hash varchar(255) not null,
     carta_inicio_id uuid,
     status int4 not null,
     baralho_id uuid,
     dado int4 not null,
     jogador_escolhido uuid,
     dth_fim timestamp,
     dth_inicio timestamp not null,
     primary key (id)
);