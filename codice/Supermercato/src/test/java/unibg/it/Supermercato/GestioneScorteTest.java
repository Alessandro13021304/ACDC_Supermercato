package unibg.it.Supermercato;

import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import unibg.it.Supermercato.gestionemagazzino.*;
import unibg.it.Supermercato.sistemaaccesso.*;

class GestioneScorteTest {

    @Test
    void aggiornamentoScortaNotificaLObserver() {

        GestoreScorte gestoreScorte =
                new GestoreScorte();

        IScorteObserver observer =
                mock(IScorteObserver.class);

        Prodotto prodotto = new Prodotto(
                "8001234567890",
                "Pasta",
                2.50,
                "Pasta di semola",
                0.0,
                3,
                10,
                1
        );

        gestoreScorte.aggiungiObserver(observer);

        gestoreScorte.aggiornaScorte(
                prodotto,
                ETipoScorta.SCAFFALE
        );

        verify(observer).aggiorna(
                prodotto,
                ETipoScorta.SCAFFALE
        );
    }
    
    @Test
    void observerRimossoNonRicevePiuNotifiche() {

        GestoreScorte gestoreScorte =
                new GestoreScorte();

        IScorteObserver observer =
                mock(IScorteObserver.class);

        Prodotto prodotto = new Prodotto(
                "8001234567890",
                "Pasta",
                2.50,
                "Pasta di semola",
                0.0,
                3,
                10,
                1
        );

        gestoreScorte.aggiungiObserver(observer);

        gestoreScorte.rimuoviObserver(observer);

        gestoreScorte.aggiornaScorte(
                prodotto,
                ETipoScorta.SCAFFALE
        );

        verifyNoInteractions(observer);
    }
    
    @Test
    void magazziniereRifornisceLoScaffaleQuandoRiceveLaNotifica() {

        GestoreScorte gestoreScorte =
                new GestoreScorte();

        Magazziniere magazziniere =
                new Magazziniere(
                        "Mario",
                        "Rossi",
                        "mario@email.it",
                        "password",
                        LocalDate.of(1990, 1, 1)
                );

        Prodotto prodotto = new Prodotto(
                "8001234567890",
                "Pasta",
                2.50,
                "Pasta di semola",
                0.0,
                3,
                50,
                1
        );

        magazziniere.setGestoreScorte(gestoreScorte);

        gestoreScorte.aggiornaScorte(
                prodotto,
                ETipoScorta.SCAFFALE
        );

        assertEquals(23, prodotto.getQuantitaScaffale());
        assertEquals(30, prodotto.getQuantitaMagazzino());
    }
    
    @Test
    void responsabileDiRepartoGeneraUnOrdinePerScortaInsufficiente() {

        GestoreScorte gestoreScorte =
                new GestoreScorte();

        IRepositoryOrdini repositoryOrdini =
                mock(IRepositoryOrdini.class);

        IOrdini gestoreOrdini =
                new GestoreOrdini(repositoryOrdini);

        Fornitore fornitore =
                new Fornitore(
                        "Fornitore Alimentare",
                        "02-1234567",
                        "fornitore@email.it"
                );

        ResponsabileDiReparto responsabile =
                new ResponsabileDiReparto(
                        "Laura",
                        "Bianchi",
                        "laura@email.it",
                        "password",
                        LocalDate.of(1985, 3, 15)
                );

        responsabile.setNumeroReparto(1);
        responsabile.setFornitore(fornitore);
        responsabile.setGestoreOrdini(gestoreOrdini);
        responsabile.setGestoreScorte(gestoreScorte);

        Prodotto prodotto = new Prodotto(
                "8001234567890",
                "Pasta",
                2.50,
                "Pasta di semola",
                0.0,
                20,
                3,
                1
        );

        gestoreScorte.aggiornaScorte(
                prodotto,
                ETipoScorta.MAGAZZINO
        );

        assertEquals(
                1,
                responsabile.getOrdiniElaborati().size()
        );

        OrdineFornitore ordine =
                responsabile.getOrdiniElaborati().get(0);

        assertSame(
                fornitore,
                ordine.getFornitore()
        );

        assertEquals(
                1,
                ordine.getRighe().size()
        );

        verify(repositoryOrdini).salvaOrdine(ordine);
    }
}
