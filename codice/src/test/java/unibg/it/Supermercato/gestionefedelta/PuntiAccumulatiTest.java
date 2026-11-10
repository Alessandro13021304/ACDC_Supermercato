package unibg.it.Supermercato.gestionefedelta;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class PuntiAccumulatiTest {
	
	@Test
    void calcolaPuntiDeveRestituireZeroPerSpeseInferioriADueEuro() {
        PuntiAccumulati punti = new PuntiAccumulati();

        assertEquals(0, punti.calcolaPunti(0));
        assertEquals(0, punti.calcolaPunti(1.99));
    }

    @Test
    void calcolaPuntiDeveRestituireUnPuntoOgniDueEuro() {
        PuntiAccumulati punti = new PuntiAccumulati();

        assertEquals(1, punti.calcolaPunti(2));
        assertEquals(5, punti.calcolaPunti(10));
        assertEquals(50, punti.calcolaPunti(100));
        assertEquals(100, punti.calcolaPunti(200));
    }

    @Test
    void calcolaPuntiDeveIgnorareLaParteFrazionaria() {
        PuntiAccumulati punti = new PuntiAccumulati();

        assertEquals(4, punti.calcolaPunti(9.99));
        assertEquals(18, punti.calcolaPunti(37.80));
    }

    @Test
    void calcolaPuntiDeveRestituireZeroPerImportiNonValidi() {
        PuntiAccumulati punti = new PuntiAccumulati();

        assertEquals(0, punti.calcolaPunti(-10));
    }
}