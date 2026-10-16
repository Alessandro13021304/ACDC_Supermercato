package unibg.it.Supermercato.gestionemagazzino;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class OrdineFornitoreTest {
	
	@Test
    void ordineVieneCreatoConFornitore() {
        Fornitore fornitore = new Fornitore("Fornitore Test");

        OrdineFornitore ordine = new OrdineFornitore(fornitore);

        assertEquals(fornitore, ordine.getFornitore());
        assertEquals("CREATO", ordine.getStato());
        assertNotNull(ordine.getDataOrdine());
        assertEquals(0.0, ordine.getTotale());
        assertTrue(ordine.getRighe().isEmpty());
    }

    @Test
    void aggiungiProdottoInserisceProdottoNellOrdine() {
        Fornitore fornitore = new Fornitore("Fornitore Test");

        Prodotto prodotto = new Prodotto(
                "123",
                "Pasta",
                2.50,
                "Pasta",
                0,
                10,
                20,
                1
        );

        OrdineFornitore ordine = new OrdineFornitore(fornitore);

        ordine.aggiungiProdotto(prodotto, 10);

        assertEquals(1, ordine.getRighe().size());
        assertEquals(prodotto, ordine.getRighe().get(0).getProdotto());
        assertEquals(10, ordine.getRighe().get(0).getQuantita());
    }

    @Test
    void aggiungiProdottoAggiornaIlTotale() {
        Fornitore fornitore = new Fornitore("Fornitore Test");

        Prodotto prodotto1 = new Prodotto(
                "123",
                "Pasta",
                2.50,
                "Pasta",
                0,
                10,
                20,
                1
        );

        Prodotto prodotto2 = new Prodotto(
                "456",
                "Latte",
                1.50,
                "Latte",
                0,
                10,
                20,
                1
        );

        OrdineFornitore ordine = new OrdineFornitore(fornitore);

        ordine.aggiungiProdotto(prodotto1, 1);
        ordine.aggiungiProdotto(prodotto2, 1);

        assertEquals(4.00, ordine.getTotale(), 0.001);
    }

    @Test
    void aggiungiProdottoNulloNonModificaOrdine() {
        Fornitore fornitore = new Fornitore("Fornitore Test");

        OrdineFornitore ordine = new OrdineFornitore(fornitore);

        ordine.aggiungiProdotto(null, 1);

        assertTrue(ordine.getRighe().isEmpty());
        assertEquals(0.0, ordine.getTotale());
    }
}
