package unibg.it.Supermercato.selfscanning;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class CalcolatoreScontoTest {

    @Test
    void applicaScontoDel20Percento() {

        CalcolatoreSconto calcolatore = new CalcolatoreSconto();

        double risultato = calcolatore.applicaSconto(10.00, 20.0);

        assertEquals(8.00, risultato, 0.001);
    }
    
    @Test
    void applicaNessunoSconto() {

        CalcolatoreSconto calcolatore = new CalcolatoreSconto();

        double risultato = calcolatore.applicaSconto(10.00, 0.0);

        assertEquals(10.00, risultato, 0.001);
    }
    
    @Test
    void applicaScontoDel100Percento() {

        CalcolatoreSconto calcolatore = new CalcolatoreSconto();

        double risultato = calcolatore.applicaSconto(10.00, 100.0);

        assertEquals(0.00, risultato, 0.001);
    }
}