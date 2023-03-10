create table baralho_cartaobjetivo (
    baralho_id uuid not null,
    cartaobjetivo_id uuid not null,
     CONSTRAINT FK_cartaobjetivo_id FOREIGN KEY (cartaobjetivo_id)
     REFERENCES carta_objetivo (id),
     CONSTRAINT FK_baralho_id FOREIGN KEY (baralho_id)
     REFERENCES baralho (id)
     );