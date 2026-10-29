package unibg.it.Supermercato;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import unibg.it.Supermercato.gestionemagazzino.GestoreProdotti;
import unibg.it.Supermercato.gestionemagazzino.Prodotto;
import unibg.it.Supermercato.persistenzadati.DBConnection;
import unibg.it.Supermercato.persistenzadati.DBProdotti;

class GestioneProdottoTest {

    @BeforeEach
    void preparaDatabase() throws Exception {
        DBConnection.getInstance().resetTestDatabase("prodotti");
    }

    @Test
    void magazziniereAggiungeERicercaUnProdotto() {

        GestoreProdotti gestoreProdotti =
                new GestoreProdotti(new DBProdotti(true));

        Prodotto prodotto = new Prodotto(
                "8001234567890",
                "Pasta",
                2.50,
                "Pasta di semola",
                0.0,
                20,
                100,
                1
        );

        gestoreProdotti.aggiungiProdotto(prodotto);

        Prodotto prodottoSalvato =
                gestoreProdotti.cercaProdotto("8001234567890");

        assertNotNull(prodottoSalvato);
        assertEquals("8001234567890", prodottoSalvato.getCodiceABarre());
        assertEquals("Pasta", prodottoSalvato.getNome());
        assertEquals(2.50, prodottoSalvato.getPrezzo());
        assertEquals(20, prodottoSalvato.getQuantitaScaffale());
        assertEquals(100, prodottoSalvato.getQuantitaMagazzino());
    }
    
    @Test
    void magazziniereModificaUnProdottoEsistente() {

        GestoreProdotti gestoreProdotti =
                new GestoreProdotti(new DBProdotti(true));

        Prodotto prodotto = new Prodotto(
                "8001234567890",
                "Pasta",
                2.50,
                "Pasta di semola",
                0.0,
                20,
                100,
                1
        );

        gestoreProdotti.aggiungiProdotto(prodotto);

        prodotto.setQuantitaScaffale(15);
        prodotto.setQuantitaMagazzino(80);

        gestoreProdotti.modificaProdotto(prodotto);

        Prodotto prodottoModificato =
                gestoreProdotti.cercaProdotto("8001234567890");

        assertNotNull(prodottoModificato);
        assertEquals("Pasta", prodottoModificato.getNome());
        assertEquals(2.50, prodottoModificato.getPrezzo());
        assertEquals(15, prodottoModificato.getQuantitaScaffale());
        assertEquals(80, prodottoModificato.getQuantitaMagazzino());
    }
    
    @Test
    void magazziniereRimuoveUnProdottoEsistente() {

        GestoreProdotti gestoreProdotti =
                new GestoreProdotti(new DBProdotti(true));

        Prodotto prodotto = new Prodotto(
                "8001234567890",
                "Pasta",
                2.50,
                "Pasta di semola",
                0.0,
                20,
                100,
                1
        );

        gestoreProdotti.aggiungiProdotto(prodotto);

        assertNotNull(
                gestoreProdotti.cercaProdotto("8001234567890")
        );

        gestoreProdotti.rimuoviProdotto(prodotto);

        Prodotto prodottoRimosso =
                gestoreProdotti.cercaProdotto("8001234567890");

        assertNull(prodottoRimosso);
    }
}

