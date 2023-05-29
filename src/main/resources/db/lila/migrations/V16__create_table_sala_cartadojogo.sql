create table sala_cartadojogo (
     id uuid not null,
     sala_id uuid not null,
     cartadojogo_id uuid not null,
     status varchar(10),
     CONSTRAINT FK_cartadojogo_id FOREIGN KEY (cartadojogo_id) REFERENCES carta_do_jogo (id),
     CONSTRAINT FK_sala_id FOREIGN KEY (sala_id) REFERENCES sala (id),
     primary key (id)
     );