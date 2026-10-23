package unibg.it.Supermercato.gestionemagazzino;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.BeforeEach;

import unibg.it.Supermercato.persistenzadati.DBConnection;
import unibg.it.Supermercato.persistenzadati.DBOrdini;

public class GestoreOrdiniTest {
	
	@BeforeEach
	void inizializzaDatabaseDiTest() throws Exception {
	    DBConnection.getInstance().resetTestDatabase("prodotti");
	}
	
    @Test
    void testCreaOrdine() {
    	GestoreOrdini gestore = new GestoreOrdini(new DBOrdini(true));

        Fornitore fornitore = new Fornitore("Fornitore Test");

        OrdineFornitore ordine = gestore.creaOrdine(fornitore);

        assertNotNull(ordine);
        assertNotNull(ordine.getFornitore());
        assertEquals("Fornitore Test", ordine.getFornitore().getNome());
        assertEquals("CREATO", ordine.getStato());
    }
    
    @Test
    void testAggiungiProdotto() {
    	GestoreOrdini gestore = new GestoreOrdini(new DBOrdini(true));

        Fornitore fornitore = new Fornitore("Fornitore Test");
        Prodotto prodotto = new Prodotto(
                "123456789",
                "Pasta",
                1.50,
                "Pasta di semola",
                0.0,
                10,
                20,
                1
        );

        OrdineFornitore ordine = gestore.creaOrdine(fornitore);

        gestore.aggiungiProdotto(ordine, prodotto, 10);

        assertNotNull(ordine.getRighe());
        assertEquals(1, ordine.getRighe().size());
        assertEquals(10, ordine.getRighe().get(0).getQuantita());
        assertEquals("123456789",
                ordine.getRighe().get(0).getProdotto().getCodiceABarre());
        assertEquals(15.0, ordine.getTotale());
    }
    
    @Test
    void testVisualizzaOrdini() {
    	GestoreOrdini gestore = new GestoreOrdini(new DBOrdini(true));

        Fornitore fornitore = new Fornitore("Fornitore Test");

        OrdineFornitore ordine = gestore.creaOrdine(fornitore);
        gestore.salvaOrdine(ordine);

        var ordini = gestore.visualizzaOrdini();

        assertNotNull(ordini);
        assertFalse(ordini.isEmpty());
        assertEquals(1, ordini.size());
        assertEquals(
                "Fornitore Test",
                ordini.get(0).getFornitore().getNome()
        );
    }
}
