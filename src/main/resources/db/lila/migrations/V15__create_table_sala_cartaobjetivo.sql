create table sala_cartaobjetivo (
     sala_id uuid not null,
     cartaobjetivo_id uuid not null,
     CONSTRAINT FK_cartaobjetivo_id FOREIGN KEY (cartaobjetivo_id)
     REFERENCES carta_objetivo (id),
     CONSTRAINT FK_sala_id FOREIGN KEY (sala_id)
     REFERENCES sala (id)
     );