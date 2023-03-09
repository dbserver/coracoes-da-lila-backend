create table jogador_cartaobjetivo (
  jogador_id uuid not null ,
  cartaobjetivo_id uuid not null ,
    CONSTRAINT FK_cartaobjetivo_id FOREIGN KEY (cartaobjetivo_id)
     REFERENCES carta_objetivo (id),
     CONSTRAINT FK_jogador_id FOREIGN KEY (jogador_id)
     REFERENCES jogador (id)
     );