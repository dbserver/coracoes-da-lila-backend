package com.db.jogo.model;
import com.db.jogo.enums.StatusEnum;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class SalaTest {
    Sala sala = new Sala();

    @Test
    @DisplayName("Teste para ver se a variavel DataHoraFimDoJogo está sendo gravada")
    void getDataHoraFimDeJogo() {
        sala.setStatus(StatusEnum.FINALIZADO);
        assertEquals(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(sala.getDataHoraFimDoJogo()), new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(Timestamp.from(Instant.now())));
        assertNotNull(StatusEnum.FINALIZADO);
    }
}

