create table baralho_cartainicio (
    baralho_id uuid not null ,
    cartainicio_id uuid not null,
     CONSTRAINT FK_cartainicio_id FOREIGN KEY (cartainicio_id)
     REFERENCES carta_inicio (id),
     CONSTRAINT FK_baralho_id FOREIGN KEY (baralho_id)
     REFERENCES baralho (id)
     );