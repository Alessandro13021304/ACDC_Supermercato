package unibg.it.Supermercato.gestionemagazzino;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GestoreOrdiniRicezioneTest {

    private IRepositoryOrdini repositoryOrdini;
    private IRepositoryProdotti repositoryProdotti;
    private GestoreOrdini gestoreOrdini;
    private Prodotto pastaOrdinata;
    private Prodotto pastaDalDatabase;
    private OrdineFornitore ordine;

    @BeforeEach
    void preparaDati() {
        repositoryOrdini = mock(IRepositoryOrdini.class);
        repositoryProdotti = mock(IRepositoryProdotti.class);

        gestoreOrdini = new GestoreOrdini(repositoryOrdini);
        gestoreOrdini.setRepositoryProdotti(repositoryProdotti);

        pastaOrdinata = new Prodotto(
                "123456789", "Pasta", 2.50, "Pasta di semola", 0.0, 10, 20, 1);

        pastaDalDatabase = new Prodotto(
                "123456789", "Pasta", 2.50, "Pasta di semola", 0.0, 10, 20, 1);

        ordine = new OrdineFornitore(new Fornitore("Fornitore Pasta"));
        ordine.aggiungiProdotto(pastaOrdinata, 100);

        when(repositoryProdotti.trovaProdottoByCodiceABarre("123456789"))
                .thenReturn(pastaDalDatabase);
    }

    @Test
    void riceviOrdineDueVolteAumentaIlMagazzinoUnaSolaVolta() {
        gestoreOrdini.riceviOrdine(ordine);
        gestoreOrdini.riceviOrdine(ordine);

        assertEquals(120, pastaDalDatabase.getQuantitaMagazzino());
        verify(repositoryProdotti, times(1)).modificaProdotto(pastaDalDatabase);
    }

    @Test
    void riceviOrdineSegnaLOrdineComeRicevutoESalvaLoStato() {
        ordine.setId(7);

        gestoreOrdini.riceviOrdine(ordine);

        assertTrue(ordine.isRicevuto());
        assertEquals("RICEVUTO", ordine.getStato());
        verify(repositoryOrdini).aggiornaStato(ordine);
    }

    @Test
    void unOrdineGiaRicevutoNonModificaIlMagazzino() {
        OrdineFornitore giaRicevuto = new OrdineFornitore(
                new Fornitore("Fornitore Pasta"), LocalDate.now(), "RICEVUTO", 250.0);
        giaRicevuto.aggiungiRiga(new RigaOrdine(pastaOrdinata, 100));

        gestoreOrdini.riceviOrdine(giaRicevuto);

        assertEquals(20, pastaDalDatabase.getQuantitaMagazzino());
        verify(repositoryProdotti, never()).modificaProdotto(any(Prodotto.class));
    }

    @Test
    void senzaRepositoryProdottiLaRicezioneFallisceENonCambiaLoStato() {
        GestoreOrdini senzaRepository = new GestoreOrdini(repositoryOrdini);

        assertThrows(IllegalStateException.class, () -> senzaRepository.riceviOrdine(ordine));
        assertFalse(ordine.isRicevuto());
    }
}

