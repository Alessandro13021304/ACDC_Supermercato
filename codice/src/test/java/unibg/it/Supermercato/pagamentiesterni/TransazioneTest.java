package unibg.it.Supermercato.pagamentiesterni;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class TransazioneTest {

    @Test
    void transazioneVieneCreataNonCompletata() {
        Transazione transazione = new Transazione(50.0);

        assertEquals(50.0, transazione.getImporto());
        assertFalse(transazione.isCompletata());
    }

    @Test
    void transazioneValidaVieneCompletata() {
        Transazione transazione = new Transazione(50.0);

        transazione.completa();

        assertTrue(transazione.isCompletata());
    }

    @Test
    void transazioneConImportoNonValidoNonVieneCompletata() {
        Transazione transazione = new Transazione(0.0);

        transazione.completa();

        assertFalse(transazione.isCompletata());
    }
}