package unibg.it.Supermercato;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

import unibg.it.Supermercato.persistenzadati.*;
import unibg.it.Supermercato.gestionemagazzino.GestoreOrdini;
import unibg.it.Supermercato.gestionemagazzino.IRepositoryOrdini;
import unibg.it.Supermercato.gestionemagazzino.IRepositoryProdotti;
import unibg.it.Supermercato.gestionemagazzino.Fornitore;
import unibg.it.Supermercato.gestionemagazzino.OrdineFornitore;
import unibg.it.Supermercato.gestionemagazzino.Prodotto;

class GestioneOrdineTest {

    @Test
    void creaOrdineEAggiungeUnProdotto() {

        Fornitore fornitore = new Fornitore(
                "Fornitore Alimentare",
                "02-1234567",
                "fornitore@email.it"
        );

        Prodotto prodotto = new Prodotto(
                "8001234567890",
                "Pasta",
                2.50,
                "Pasta di semola",
                0.0,
                20,
                50,
                1
        );

        OrdineFornitore ordine =
                new OrdineFornitore(fornitore);

        ordine.aggiungiProdotto(prodotto, 10);

        assertSame(fornitore, ordine.getFornitore());
        assertEquals("CREATO", ordine.getStato());
        assertEquals(1, ordine.getRighe().size());
        assertEquals(25.0, ordine.getTotale());
    }

    @Test
    void gestoreOrdiniCreaESalvaUnOrdine() {

        IRepositoryOrdini repositoryOrdini =
                mock(IRepositoryOrdini.class);

        GestoreOrdini gestoreOrdini =
                new GestoreOrdini(repositoryOrdini);

        Fornitore fornitore = new Fornitore(
                "Fornitore Alimentare",
                "02-1234567",
                "fornitore@email.it"
        );

        OrdineFornitore ordine =
                gestoreOrdini.creaOrdine(fornitore);

        assertNotNull(ordine);
        assertSame(fornitore, ordine.getFornitore());
        assertEquals("CREATO", ordine.getStato());

        verify(repositoryOrdini).salvaOrdine(ordine);
    }
    
    @Test
    void gestoreOrdiniAggiungeUnProdottoAllOrdine() {

        GestoreOrdini gestoreOrdini =
                new GestoreOrdini(mock(IRepositoryOrdini.class));

        Fornitore fornitore = new Fornitore(
                "Fornitore Alimentare",
                "02-1234567",
                "fornitore@email.it"
        );

        Prodotto prodotto = new Prodotto(
                "8001234567890",
                "Pasta",
                2.50,
                "Pasta di semola",
                0.0,
                20,
                50,
                1
        );

        OrdineFornitore ordine =
                gestoreOrdini.creaOrdine(fornitore);

        gestoreOrdini.aggiungiProdotto(
                ordine,
                prodotto,
                10
        );

        assertEquals(1, ordine.getRighe().size());
        assertEquals(25.0, ordine.getTotale());
    }
    
    @Test
    void gestoreOrdiniVisualizzaGliOrdini() {

        IRepositoryOrdini repositoryOrdini =
                mock(IRepositoryOrdini.class);

        GestoreOrdini gestoreOrdini =
                new GestoreOrdini(repositoryOrdini);

        Fornitore fornitore = new Fornitore(
                "Fornitore Alimentare",
                "02-1234567",
                "fornitore@email.it"
        );

        OrdineFornitore ordine =
                new OrdineFornitore(fornitore);

        when(repositoryOrdini.trovaTuttiOrdini())
                .thenReturn(java.util.List.of(ordine));

        var ordini = gestoreOrdini.visualizzaOrdini();

        assertNotNull(ordini);
        assertEquals(1, ordini.size());
        assertSame(ordine, ordini.get(0));

        verify(repositoryOrdini).trovaTuttiOrdini();
    }
    
    @Test
    void riceviOrdineAumentaLaQuantitaInMagazzino() {

        IRepositoryOrdini repositoryOrdini = mock(IRepositoryOrdini.class);
        IRepositoryProdotti repositoryProdotti = mock(IRepositoryProdotti.class);

        GestoreOrdini gestoreOrdini =
                new GestoreOrdini(repositoryOrdini);

        gestoreOrdini.setRepositoryProdotti(repositoryProdotti);

        Fornitore fornitore =
                new Fornitore("Fornitore Pasta", "0123456789",
                        "pasta@fornitore.it");

        Prodotto pasta =
                new Prodotto(
                        "123456789",
                        "Pasta",
                        2.50,
                        "Pasta di semola",
                        0.0,
                        10,
                        20,
                        1
                );

        Prodotto pastaDalDatabase =
                new Prodotto(
                        "123456789",
                        "Pasta",
                        2.50,
                        "Pasta di semola",
                        0.0,
                        10,
                        20,
                        1
                );

        OrdineFornitore ordine =
                new OrdineFornitore(fornitore);

        ordine.aggiungiProdotto(pasta, 100);

        when(repositoryProdotti.trovaProdottoByCodiceABarre("123456789"))
                .thenReturn(pastaDalDatabase);

        gestoreOrdini.riceviOrdine(ordine);

        assertEquals(120, pastaDalDatabase.getQuantitaMagazzino());

        verify(repositoryProdotti).trovaProdottoByCodiceABarre("123456789");
        verify(repositoryProdotti).modificaProdotto(pastaDalDatabase);
    }
}
