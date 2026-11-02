package unibg.it.Supermercato.persistenzadati;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import unibg.it.Supermercato.gestionemagazzino.*;

class DBOrdiniStatoTest {

    private DBProdotti repositoryProdotti;
    private DBOrdini repositoryOrdini;
    private Prodotto prodotto;

    @BeforeEach
    void inizializzaDatabaseDiTest() throws Exception {
        DBConnection.getInstance().resetTestDatabase("prodotti");

        repositoryProdotti = new DBProdotti(true);
        repositoryOrdini = new DBOrdini(true);

        prodotto = new Prodotto(
                "123456789", "Pasta", 1.50, "Pasta di semola", 10.0, 8, 20, 1);

        repositoryProdotti.salvaProdotto(prodotto);
    }

    private OrdineFornitore creaESalvaOrdine() {
        OrdineFornitore ordine = new OrdineFornitore(new Fornitore("Fornitore Pasta"));
        ordine.aggiungiProdotto(prodotto, 10);
        repositoryOrdini.salvaOrdine(ordine);
        return ordine;
    }

    @Test
    void salvaOrdineAssegnaUnIdEIlRecuperoLoRestituisce() {
        OrdineFornitore ordine = creaESalvaOrdine();

        assertTrue(ordine.getId() > 0);

        List<OrdineFornitore> ordini = repositoryOrdini.trovaTuttiOrdini();

        assertEquals(1, ordini.size());
        assertEquals(ordine.getId(), ordini.get(0).getId());
    }

    @Test
    void laRicezioneAggiornaLoStatoNelDatabaseEUnaSecondaRicezioneNonDuplicaLeScorte() {
        creaESalvaOrdine();

        GestoreOrdini gestoreOrdini = new GestoreOrdini(repositoryOrdini);
        gestoreOrdini.setRepositoryProdotti(repositoryProdotti);

        OrdineFornitore daDatabase = repositoryOrdini.trovaTuttiOrdini().get(0);

        gestoreOrdini.riceviOrdine(daDatabase);

        assertEquals(30, repositoryProdotti.trovaProdottoByCodiceABarre("123456789").getQuantitaMagazzino());

        OrdineFornitore ricaricato = repositoryOrdini.trovaTuttiOrdini().get(0);

        assertEquals("RICEVUTO", ricaricato.getStato());

        gestoreOrdini.riceviOrdine(ricaricato);

        assertEquals(30, repositoryProdotti.trovaProdottoByCodiceABarre("123456789").getQuantitaMagazzino());
    }
}

