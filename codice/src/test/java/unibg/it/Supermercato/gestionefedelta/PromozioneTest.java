package unibg.it.Supermercato.gestionefedelta;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class PromozioneTest {
	
	@Test
    void calcolaScontoDeveRestituireZeroSottoLaSogliaMinima() {
        Promozione promozione = new Promozione();

        assertEquals(0.0, promozione.calcolaSconto(0));
        assertEquals(0.0, promozione.calcolaSconto(99));
    }

    @Test
    void calcolaScontoDeveCalcolareLaPercentualeInBaseAiPunti() {
        Promozione promozione = new Promozione();

        assertEquals(5.0, promozione.calcolaSconto(100));
        assertEquals(10.0, promozione.calcolaSconto(200));
        assertEquals(20.0, promozione.calcolaSconto(400));
        assertEquals(25.0, promozione.calcolaSconto(500));
    }

    @Test
    void calcolaScontoNonDeveSuperareIlMassimoDelTrentaPercento() {
        Promozione promozione = new Promozione();

        assertEquals(30.0, promozione.calcolaSconto(600));
        assertEquals(30.0, promozione.calcolaSconto(1000));
    }

    @Test
    void applicaScontoDeveCalcolareIlTotaleScontato() {
        Promozione promozione = new Promozione();

        assertEquals(190.0, promozione.applicaSconto(200.0, 5.0), 0.001);
        assertEquals(180.0, promozione.applicaSconto(200.0, 10.0), 0.001);
        assertEquals(140.0, promozione.applicaSconto(200.0, 30.0), 0.001);
    }

    @Test
    void applicaScontoDeveGestireImportiOPercentualiNonValidi() {
        Promozione promozione = new Promozione();

        assertEquals(200.0, promozione.applicaSconto(200.0, 0.0), 0.001);
        assertEquals(200.0, promozione.applicaSconto(200.0, -5.0), 0.001);
        assertEquals(0.0, promozione.applicaSconto(0.0, 10.0), 0.001);
    }

    @Test
    void calcolaPuntiDaScalareDeveRestituireZeroSottoLaSoglia() {
        Promozione promozione = new Promozione();

        assertEquals(0, promozione.calcolaPuntiDaScalare(0));
        assertEquals(0, promozione.calcolaPuntiDaScalare(99));
    }

    @Test
    void calcolaPuntiDaScalareDeveRestituireTuttiIPuntiDisponibili() {
        Promozione promozione = new Promozione();

        assertEquals(100, promozione.calcolaPuntiDaScalare(100));
        assertEquals(250, promozione.calcolaPuntiDaScalare(250));
        assertEquals(600, promozione.calcolaPuntiDaScalare(600));
    }
}
