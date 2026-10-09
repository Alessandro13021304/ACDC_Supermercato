package unibg.it.Supermercato.sistemaaccesso;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.BeforeEach;

import unibg.it.Supermercato.persistenzadati.DBConnection;
import unibg.it.Supermercato.persistenzadati.DBProdotti;
import unibg.it.Supermercato.gestionemagazzino.Fornitore;
import unibg.it.Supermercato.gestionemagazzino.OrdineFornitore;
import unibg.it.Supermercato.gestionemagazzino.Prodotto;
import unibg.it.Supermercato.gestionemagazzino.GestoreScorte;
import unibg.it.Supermercato.gestionemagazzino.TipoScorta;
import unibg.it.Supermercato.gestionemagazzino.GestoreProdotti;
import unibg.it.Supermercato.gestionemagazzino.GestoreOrdini;

class ResponsabileDiRepartoTest {
	
    @BeforeEach
    void inizializzaDatabaseDiTest() throws Exception {
        DBConnection.getInstance().resetTestDatabase("prodotti");
    }

    @Test
    void elaboraOrdineDeveCreareOrdineCon100PezziPerProdotto() {

        ResponsabileDiReparto responsabile = new ResponsabileDiReparto(
                "Mario",
                "Rossi",
                "mario@supermercato.it",
                "password",
                LocalDate.of(1980, 1, 1)
        );
        
        responsabile.setGestoreOrdini(new GestoreOrdini());

        Fornitore fornitore = new Fornitore("Fornitore Test");

        Prodotto prodotto1 = new Prodotto(
                "001", "Pasta", 2.00, "Pasta", 0.0, 2, 3, 1
        );

        Prodotto prodotto2 = new Prodotto(
                "002", "Latte", 1.50, "Latte", 0.0, 1, 2, 1
        );

        List<Prodotto> prodotti = List.of(prodotto1, prodotto2);

        OrdineFornitore ordine =
                responsabile.elaboraOrdine(fornitore, prodotti);

        assertNotNull(ordine);
        assertEquals(fornitore, ordine.getFornitore());
        assertEquals(2, ordine.getRighe().size());

        assertEquals(prodotto1, ordine.getRighe().get(0).getProdotto());
        assertEquals(100, ordine.getRighe().get(0).getQuantita());

        assertEquals(prodotto2, ordine.getRighe().get(1).getProdotto());
        assertEquals(100, ordine.getRighe().get(1).getQuantita());
    }
    
    @Test
    void testVisualizzaProdottoDaCatalogo() {
    	DBProdotti db = new DBProdotti(true);

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

        db.salvaProdotto(prodotto);

        ResponsabileDiReparto responsabile = new ResponsabileDiReparto(
                "Mario",
                "Rossi",
                "mario.rossi@email.it",
                "password",
                LocalDate.of(1990, 1, 1)
        );

        GestoreProdotti gestoreProdotti = new GestoreProdotti(db);
        responsabile.setGestoreProdotti(gestoreProdotti);

        Prodotto risultato = responsabile.visualizzaProdotto("123456789");

        assertNotNull(risultato);
        assertEquals("123456789", risultato.getCodiceABarre());
        assertEquals("Pasta", risultato.getNome());
        assertEquals(1.50, risultato.getPrezzo());
    }
    
    @Test
    void testAggiungiProdotto() {
        DBProdotti db = new DBProdotti(true);
        GestoreProdotti gestoreProdotti = new GestoreProdotti(db);

        ResponsabileDiReparto responsabile = new ResponsabileDiReparto(
                "Mario",
                "Rossi",
                "mario.rossi@email.it",
                "password",
                LocalDate.of(1990, 1, 1)
        );

        responsabile.setGestoreProdotti(gestoreProdotti);

        Prodotto prodotto = new Prodotto(
                "111111111",
                "Biscotti",
                2.50,
                "Biscotti al cioccolato",
                10.0,
                10,
                30,
                1
        );

        responsabile.aggiungiProdotto(prodotto);

        Prodotto risultato = db.trovaProdottoByCodiceABarre("111111111");

        assertNotNull(risultato);
        assertEquals("111111111", risultato.getCodiceABarre());
        assertEquals("Biscotti", risultato.getNome());
        assertEquals(2.50, risultato.getPrezzo());
    }
    
    @Test
    void testRimuoviProdotto() {
        DBProdotti db = new DBProdotti(true);
        GestoreProdotti gestoreProdotti = new GestoreProdotti(db);

        ResponsabileDiReparto responsabile = new ResponsabileDiReparto(
                "Mario",
                "Rossi",
                "mario.rossi@email.it",
                "password",
                LocalDate.of(1990, 1, 1)
        );

        responsabile.setGestoreProdotti(gestoreProdotti);

        Prodotto prodotto = new Prodotto(
                "222222222",
                "Latte",
                1.50,
                "Latte intero",
                0.0,
                10,
                20,
                1
        );

        db.salvaProdotto(prodotto);

        assertNotNull(db.trovaProdottoByCodiceABarre("222222222"));

        responsabile.rimuoviProdotto(prodotto);

        assertNull(db.trovaProdottoByCodiceABarre("222222222"));
    }
    
    @Test
    void testModificaProdotto() {
        DBProdotti db = new DBProdotti(true);
        GestoreProdotti gestoreProdotti = new GestoreProdotti(db);

        ResponsabileDiReparto responsabile = new ResponsabileDiReparto(
                "Mario",
                "Rossi",
                "mario.rossi@email.it",
                "password",
                LocalDate.of(1990, 1, 1)
        );

        responsabile.setGestoreProdotti(gestoreProdotti);

        Prodotto prodotto = new Prodotto(
                "333333333",
                "Pasta",
                1.50,
                "Pasta di semola",
                0.0,
                10,
                20,
                1
        );

        db.salvaProdotto(prodotto);

        Prodotto prodottoModificato = new Prodotto(
                "333333333",
                "Pasta Integrale",
                2.00,
                "Pasta integrale di semola",
                10.0,
                15,
                25,
                1
        );

        responsabile.modificaProdotto(prodottoModificato);

        Prodotto risultato = db.trovaProdottoByCodiceABarre("333333333");

        assertNotNull(risultato);
        assertEquals("333333333", risultato.getCodiceABarre());
        assertEquals("Pasta Integrale", risultato.getNome());
        assertEquals(2.00, risultato.getPrezzo());
        assertEquals("Pasta integrale di semola", risultato.getDescrizione());
        assertEquals(10.0, risultato.getSconto());
        assertEquals(15, risultato.getQuantitaScaffale());
        assertEquals(25, risultato.getQuantitaMagazzino());
    }
    
    @Test
    void testSegnalaScorteInsufficientiScaffale() {
        ResponsabileDiReparto responsabile = new ResponsabileDiReparto(
                "Mario",
                "Rossi",
                "mario.rossi@email.it",
                "password",
                LocalDate.of(1990, 1, 1)
        );

        Prodotto prodotto = new Prodotto(
                "123456789",
                "Pasta",
                1.50,
                "Pasta di semola",
                10.0,
                3,   
                20,
                1
        );

        assertTrue(responsabile.segnalaScorteInsufficientiScaffale(prodotto));

        prodotto.setQuantitaScaffale(5);

        assertFalse(responsabile.segnalaScorteInsufficientiScaffale(prodotto));

        assertFalse(responsabile.segnalaScorteInsufficientiScaffale(null));
    }
    
    @Test
    void aggiornaConMagazzinoInsufficienteElaboraOrdine() {

        ResponsabileDiReparto responsabile = new ResponsabileDiReparto(
                "Mario",
                "Rossi",
                "mario@supermercato.it",
                "password",
                LocalDate.of(1980, 1, 1)
        );

        Fornitore fornitore = new Fornitore("Fornitore Test");
        responsabile.setFornitore(fornitore);

        GestoreScorte gestore = new GestoreScorte();
        responsabile.setGestoreScorte(gestore);

        Prodotto prodotto = new Prodotto(
                "001",
                "Pasta",
                2.00,
                "Pasta",
                0.0,
                3,
                0,
                1
        );

        responsabile.aggiorna(prodotto, TipoScorta.MAGAZZINO);

        assertEquals(0, prodotto.getQuantitaMagazzino());
    }
}
