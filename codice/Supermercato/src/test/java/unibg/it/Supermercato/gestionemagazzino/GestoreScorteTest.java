package unibg.it.Supermercato.gestionemagazzino;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class GestoreScorteTest {
	
	@Test
    void testNotificaObserver() {

        GestoreScorte gestore = new GestoreScorte();

        final boolean[] notificato = {false};

        ScorteObserver observer = (prodotto, tipoScorta) -> notificato[0] = true;

        gestore.aggiungiObserver(observer);

        Prodotto prodotto = new Prodotto(
                "123",
                "Pasta",
                1.50,
                "Pasta",
                0.0,
                3,
                10,
                1
        );

        gestore.notificaObserver(prodotto, TipoScorta.SCAFFALE);

        assertTrue(notificato[0]);
    }

    @Test
    void testRimuoviObserver() {

        GestoreScorte gestore = new GestoreScorte();

        final boolean[] notificato = {false};

        ScorteObserver observer = (prodotto, tipoScorta) -> notificato[0] = true;

        gestore.aggiungiObserver(observer);
        gestore.rimuoviObserver(observer);

        Prodotto prodotto = new Prodotto(
                "123",
                "Pasta",
                1.50,
                "Pasta",
                0.0,
                3,
                10,
                1
        );

        gestore.notificaObserver(prodotto, TipoScorta.SCAFFALE);

        assertFalse(notificato[0]);
    }
    
    @Test
    void testNotificaPiuObserver() {

        GestoreScorte gestore = new GestoreScorte();

        final int[] notifiche = {0};

        ScorteObserver observer1 = (prodotto, tipoScorta) -> notifiche[0]++;
        ScorteObserver observer2 = (prodotto, tipoScorta) -> notifiche[0]++;

        gestore.aggiungiObserver(observer1);
        gestore.aggiungiObserver(observer2);

        Prodotto prodotto = new Prodotto(
                "456",
                "Latte",
                1.80,
                "Latte intero",
                0.0,
                2,
                8,
                1
        );

        gestore.notificaObserver(prodotto, TipoScorta.SCAFFALE);

        assertEquals(2, notifiche[0]);
    }
    
    @Test
    void testAggiornaScorteNotificaObserver() {

        GestoreScorte gestore = new GestoreScorte();

        final boolean[] notificato = {false};

        ScorteObserver observer = (prodotto, tipoScorta) -> notificato[0] = true;

        gestore.aggiungiObserver(observer);

        Prodotto prodotto = new Prodotto(
                "999",
                "Biscotti",
                2.00,
                "Biscotti",
                0.0,
                3,
                20,
                1
        );

        gestore.aggiornaScorte(prodotto, TipoScorta.SCAFFALE);

        assertTrue(notificato[0]);
    }
}
