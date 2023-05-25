create table sala_cartadojogo (
     sala_id uuid not null,
     cartadojogo_id uuid not null,
     CONSTRAINT FK_cartadojogo_id FOREIGN KEY (cartadojogo_id) REFERENCES carta_do_jogo (id),
     CONSTRAINT FK_sala_id FOREIGN KEY (sala_id) REFERENCES sala (id)
     );