create table baralho_cartadojogo (
   cartadojogo_id uuid not null ,
    baralho_id uuid not null,
 CONSTRAINT FK_cartadojogo_id FOREIGN KEY (cartadojogo_id)
 REFERENCES carta_do_jogo (id),
 CONSTRAINT FK_baralho_id FOREIGN KEY (baralho_id)
 REFERENCES baralho (id)
 );