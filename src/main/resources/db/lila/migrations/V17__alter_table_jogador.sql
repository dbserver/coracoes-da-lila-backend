delete from sala_cartadojogo;
delete from sala_cartaobjetivo;
delete from sala_jogadores;
delete from sala;

delete from jogador_cartadojogo;
delete from jogador_cartaobjetivo;
delete from jogador_cartasdojogo;
delete from jogador;


ALTER TABLE jogador 
ADD COLUMN sala_id uuid not null;

ALTER TABLE jogador 
ADD CONSTRAINT FK_sala_id FOREIGN KEY (sala_id) REFERENCES sala (id);