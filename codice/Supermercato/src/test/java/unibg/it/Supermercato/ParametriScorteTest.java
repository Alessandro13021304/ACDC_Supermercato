package unibg.it.Supermercato;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import unibg.it.Supermercato.gestionemagazzino.*;
import unibg.it.Supermercato.sistemaaccesso.Magazziniere;
import unibg.it.Supermercato.sistemaaccesso.ResponsabileDiReparto;

class ParametriScorteTest {

    private Prodotto prodotto(int scaffale, int magazzino) {
        return new Prodotto("123", "Pasta", 2.50, "Pasta", 0.0, scaffale, magazzino, 1);
    }

    @Test
    void iValoriDiBusinessSonoQuelliPrevisti() {
        assertEquals(5, ParametriScorte.SOGLIA_MINIMA);
        assertEquals(20, ParametriScorte.QUANTITA_RIFORNIMENTO);
        assertEquals(100, ParametriScorte.QUANTITA_ORDINE_STANDARD);
    }

    @Test
    void ilGestoreScorteNotificaSoloSottoLaSogliaMinima() {
        GestoreScorte gestore = new GestoreScorte();
        ScorteObserver observer = mock(ScorteObserver.class);
        gestore.aggiungiObserver(observer);

        Prodotto allaSoglia = prodotto(ParametriScorte.SOGLIA_MINIMA, 10);
        Prodotto sottoSoglia = prodotto(ParametriScorte.SOGLIA_MINIMA - 1, 10);

        gestore.aggiornaScorte(allaSoglia, ETipoScorta.SCAFFALE);
        verify(observer, never()).aggiorna(any(), any());

        gestore.aggiornaScorte(sottoSoglia, ETipoScorta.SCAFFALE);
        verify(observer).aggiorna(sottoSoglia, ETipoScorta.SCAFFALE);
    }

    @Test
    void laScortaMagazzinoUsaLaStessaSoglia() {
        ScortaMagazzino scorta = new ScortaMagazzino();

        Prodotto daRiordinare = prodotto(ParametriScorte.SOGLIA_MINIMA - 1, ParametriScorte.SOGLIA_MINIMA - 1);
        Prodotto conMagazzinoSufficiente = new Prodotto("456", "Latte", 1.50, "Latte", 0.0, ParametriScorte.SOGLIA_MINIMA - 1, ParametriScorte.SOGLIA_MINIMA, 1);

        scorta.aggiungiProdotto(daRiordinare);
        scorta.aggiungiProdotto(conMagazzinoSufficiente);

        assertEquals(1, scorta.getProdottiDaRiordinare().size());
        assertTrue(scorta.getProdottiDaRiordinare().contains(daRiordinare));
    }

    @Test
    void ilMagazziniereSegnalaSoloSottoLaSogliaMinima() {
        Magazziniere magazziniere = new Magazziniere("Mario", "Rossi", "mario@email.it", "password", LocalDate.of(1990, 1, 1));

        assertTrue(magazziniere.segnalaScorteInsufficientiMagazzino(prodotto(10, ParametriScorte.SOGLIA_MINIMA - 1)));
        assertFalse(magazziniere.segnalaScorteInsufficientiMagazzino(prodotto(10, ParametriScorte.SOGLIA_MINIMA)));
    }

    @Test
    void ilResponsabileSegnalaSoloSottoLaSogliaMinima() {
        ResponsabileDiReparto responsabile = new ResponsabileDiReparto("Laura", "Bianchi", "laura@email.it", "password", LocalDate.of(1985, 3, 15));

        assertTrue(responsabile.segnalaScorteInsufficientiScaffale(prodotto(ParametriScorte.SOGLIA_MINIMA - 1, 10)));
        assertFalse(responsabile.segnalaScorteInsufficientiScaffale(prodotto(ParametriScorte.SOGLIA_MINIMA, 10)));
    }
}

