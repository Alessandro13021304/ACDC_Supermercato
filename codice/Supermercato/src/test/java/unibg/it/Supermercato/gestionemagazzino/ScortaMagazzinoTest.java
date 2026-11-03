package unibg.it.Supermercato.gestionemagazzino;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class ScortaMagazzinoTest {

	@Test
	void prodottoConMagazzinoSufficienteNonDeveEssereRiordinato() {
        Prodotto prodotto = new Prodotto("001", "Pasta", 2.50, "Pasta", 0.0, 2, 10, 1);

        ScortaMagazzino scorta = new ScortaMagazzino();
        scorta.aggiungiProdotto(prodotto);

        assertTrue(scorta.getProdottiDaRiordinare().isEmpty());
    }

    @Test
    void prodottoConScaffaleEMagazzinoSottoSogliaDeveEssereRiordinato() {
        Prodotto prodotto = new Prodotto("002", "Latte", 1.50, "Latte", 0.0, 2, 3, 1);

        ScortaMagazzino scorta = new ScortaMagazzino();
        scorta.aggiungiProdotto(prodotto);

        assertEquals(1, scorta.getProdottiDaRiordinare().size());
        assertTrue(scorta.getProdottiDaRiordinare().contains(prodotto));
    }

    @Test
    void prodottoConScaffaleSufficienteNonDeveEssereRiordinato() {
        Prodotto prodotto = new Prodotto("003", "Biscotti", 3.00, "Biscotti", 0.0, 5, 3, 1);

        ScortaMagazzino scorta = new ScortaMagazzino();
        scorta.aggiungiProdotto(prodotto);

        assertTrue(scorta.getProdottiDaRiordinare().isEmpty());
    }

    @Test
    void prodottoNullNonDeveEssereAggiunto() {
        ScortaMagazzino scorta = new ScortaMagazzino();

        scorta.aggiungiProdotto(null);

        assertTrue(scorta.getProdottiDaRiordinare().isEmpty());
    }

}
