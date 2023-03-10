create table carta_do_jogo (
     id uuid primary key  not null,
     bonus boolean not null,
     categoria varchar(80) not null,
     nova_categoria varchar(80),
     fonte varchar(80) not null,
     pontos int4 not null,
     texto varchar(255) not null,
     tipo varchar(50) not null,
     valor_cor_grande int4,
     valor_cor_pequeno int4
     );