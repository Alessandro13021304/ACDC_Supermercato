package unibg.it.Supermercato.persistenzadati;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import unibg.it.Supermercato.gestionemagazzino.*;

class DBOrdiniTest {

    @BeforeEach
    void inizializzaDatabaseDiTest() throws Exception {
        DBConnection.getInstance().resetTestDatabase("prodotti");
    }

    @Test
    void testSalvaOrdine() {

        DBProdotti repositoryProdotti = new DBProdotti(true);
        DBOrdini repositoryOrdini = new DBOrdini(true);

        Prodotto prodotto = new Prodotto("123456789", "Pasta", 1.50, "Pasta di semola", 10.0, 8, 20, 1);

        repositoryProdotti.salvaProdotto(prodotto);

        Fornitore fornitore = new Fornitore("Fornitore Pasta");

        OrdineFornitore ordine = new OrdineFornitore(fornitore);
        ordine.aggiungiProdotto(prodotto, 10);

        assertDoesNotThrow(() -> repositoryOrdini.salvaOrdine(ordine));
    }

    @Test
    void testTrovaTuttiOrdini() {

        DBProdotti repositoryProdotti = new DBProdotti(true);
        DBOrdini repositoryOrdini = new DBOrdini(true);

        Prodotto prodotto = new Prodotto("123456789", "Pasta", 1.50, "Pasta di semola", 10.0, 8, 20, 1);

        repositoryProdotti.salvaProdotto(prodotto);

        Fornitore fornitore = new Fornitore("Fornitore Pasta");

        OrdineFornitore ordine = new OrdineFornitore(fornitore);
        ordine.aggiungiProdotto(prodotto, 10);

        repositoryOrdini.salvaOrdine(ordine);

        List<OrdineFornitore> ordini = repositoryOrdini.trovaTuttiOrdini();

        assertNotNull(ordini);
        assertEquals(1, ordini.size());

        OrdineFornitore risultato = ordini.get(0);

        assertEquals("Fornitore Pasta", risultato.getFornitore().getNome());

        assertEquals("CREATO", risultato.getStato());
        assertEquals(15.0, risultato.getTotale());

        assertNotNull(risultato.getRighe());
        assertEquals(1, risultato.getRighe().size());

        RigaOrdine riga = risultato.getRighe().get(0);

        assertEquals("123456789", riga.getProdotto().getCodiceABarre());

        assertEquals(10, riga.getQuantita());
        assertEquals(1.50, riga.getProdotto().getPrezzo());
    }

    @Test
    void testTrovaTuttiOrdiniQuandoNonCiSonoOrdini() {

        DBOrdini repositoryOrdini = new DBOrdini(true);

        List<OrdineFornitore> ordini = repositoryOrdini.trovaTuttiOrdini();

        assertNotNull(ordini);
        assertTrue(ordini.isEmpty());
    }
}