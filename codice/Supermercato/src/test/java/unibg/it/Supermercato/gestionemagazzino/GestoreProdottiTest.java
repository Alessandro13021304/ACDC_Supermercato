package unibg.it.Supermercato.gestionemagazzino;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import unibg.it.Supermercato.persistenzadati.DBConnection;
import unibg.it.Supermercato.persistenzadati.DBProdotti;

public class GestoreProdottiTest {
	
	private GestoreProdotti gestoreProdotti;

    @BeforeEach
    void inizializzaDatabaseDiTest() throws Exception {
        DBConnection.getInstance().resetTestDatabase("prodotti");
        gestoreProdotti = new GestoreProdotti(new DBProdotti(true));
    }

    @Test
    void testAggiungiProdotto() {

        Prodotto prodotto = new Prodotto(
                "123456789",
                "Pasta",
                1.50,
                "Pasta di semola",
                10.0,
                8,
                20,
                1
        );

        gestoreProdotti.aggiungiProdotto(prodotto);

        Prodotto risultato = gestoreProdotti.cercaProdotto("123456789");

        assertNotNull(risultato);
        assertEquals("Pasta", risultato.getNome());
    }
    
    @Test
    void testRimuoviProdotto() {

        Prodotto prodotto = new Prodotto(
                "123456789",
                "Pasta",
                1.50,
                "Pasta di semola",
                10.0,
                8,
                20,
                1
        );

        gestoreProdotti.aggiungiProdotto(prodotto);

        gestoreProdotti.rimuoviProdotto(prodotto);

        Prodotto risultato = gestoreProdotti.cercaProdotto("123456789");

        assertNull(risultato);
    }
    
    @Test
    void testModificaProdotto() {

        Prodotto prodotto = new Prodotto(
                "123456789",
                "Pasta",
                1.50,
                "Pasta di semola",
                10.0,
                8,
                20,
                1
        );

        gestoreProdotti.aggiungiProdotto(prodotto);

        Prodotto prodottoModificato = new Prodotto(
                "123456789",
                "Pasta Integrale",
                2.00,
                "Pasta integrale di semola",
                20.0,
                12,
                25,
                1
        );

        gestoreProdotti.modificaProdotto(prodottoModificato);

        Prodotto risultato = gestoreProdotti.cercaProdotto("123456789");

        assertNotNull(risultato);
        assertEquals("Pasta Integrale", risultato.getNome());
        assertEquals(2.00, risultato.getPrezzo());
        assertEquals("Pasta integrale di semola", risultato.getDescrizione());
        assertEquals(20.0, risultato.getSconto());
        assertEquals(12, risultato.getQuantitaScaffale());
        assertEquals(25, risultato.getQuantitaMagazzino());
    }
    
    @Test
    void testCercaProdotto() {

        Prodotto prodotto = new Prodotto(
                "123456789",
                "Pasta",
                1.50,
                "Pasta di semola",
                10.0,
                8,
                20,
                1
        );

        gestoreProdotti.aggiungiProdotto(prodotto);

        Prodotto risultato = gestoreProdotti.cercaProdotto("123456789");

        assertNotNull(risultato);
        assertEquals("123456789", risultato.getCodiceABarre());
        assertEquals("Pasta", risultato.getNome());
        assertEquals(1.50, risultato.getPrezzo());
        assertEquals("Pasta di semola", risultato.getDescrizione());
        assertEquals(10.0, risultato.getSconto());
        assertEquals(8, risultato.getQuantitaScaffale());
        assertEquals(20, risultato.getQuantitaMagazzino());
    }
    
    @Test
    void testVisualizzaProdotti() {

        Prodotto prodotto1 = new Prodotto(
                "123456789",
                "Pasta",
                1.50,
                "Pasta di semola",
                10.0,
                8,
                20,
                1
        );

        Prodotto prodotto2 = new Prodotto(
                "987654321",
                "Latte",
                1.20,
                "Latte intero",
                0.0,
                15,
                30,
                1
        );

        gestoreProdotti.aggiungiProdotto(prodotto1);
        gestoreProdotti.aggiungiProdotto(prodotto2);

        List<Prodotto> prodotti = gestoreProdotti.visualizzaProdotti();

        assertNotNull(prodotti);
        assertEquals(2, prodotti.size());

        assertTrue(prodotti.stream().anyMatch(p -> "123456789".equals(p.getCodiceABarre())));

        assertTrue(prodotti.stream().anyMatch(p -> "987654321".equals(p.getCodiceABarre())));
    }

}
