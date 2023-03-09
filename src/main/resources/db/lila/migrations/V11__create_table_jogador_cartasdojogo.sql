create table jogador_cartasdojogo (
  id uuid not null,
  jogador_id uuid not null ,
  cartadojogo_id uuid not null,
  nova_categoria varchar(30),
     CONSTRAINT FK_cartadojogo_id FOREIGN KEY (cartadojogo_id)
     REFERENCES carta_do_jogo (id),
     CONSTRAINT FK_jogador_id FOREIGN KEY (jogador_id)
     REFERENCES jogador (id),
     primary key (id)
     );