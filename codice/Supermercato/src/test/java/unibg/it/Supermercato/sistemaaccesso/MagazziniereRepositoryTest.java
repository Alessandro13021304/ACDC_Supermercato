package unibg.it.Supermercato.sistemaaccesso;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import unibg.it.Supermercato.gestionemagazzino.IRepositoryProdotti;
import unibg.it.Supermercato.gestionemagazzino.Prodotto;
import unibg.it.Supermercato.gestionemagazzino.TipoScorta;

class MagazziniereRepositoryTest {

    private IRepositoryProdotti repositoryProdotti;
    private Magazziniere magazziniere;

    @BeforeEach
    void preparaDati() {
        repositoryProdotti = mock(IRepositoryProdotti.class);

        magazziniere = new Magazziniere("Mario", "Rossi", "mario@email.it", "password", LocalDate.of(1990, 1, 1));
        magazziniere.setRepositoryProdotti(repositoryProdotti);
    }

    private Prodotto prodotto(int scaffale, int magazzino) {
        return new Prodotto("123", "Pasta", 2.50, "Pasta", 0, scaffale, magazzino, 1);
    }

    @Test
    void ilRifornimentoViaggiaSulRepositoryOltreCheInMemoria() {
        Prodotto prodotto = prodotto(10, 50);

        magazziniere.rifornisciScaffale(prodotto, 20);

        assertEquals(30, prodotto.getQuantitaScaffale());
        assertEquals(30, prodotto.getQuantitaMagazzino());
        verify(repositoryProdotti).spostaDalMagazzinoAlloScaffale("123", 20);
    }

    @Test
    void unRifornimentoImpossibileNonToccaIlRepository() {
        Prodotto prodotto = prodotto(10, 5);

        magazziniere.rifornisciScaffale(prodotto, 10);

        assertEquals(10, prodotto.getQuantitaScaffale());
        assertEquals(5, prodotto.getQuantitaMagazzino());
        verify(repositoryProdotti, never()).spostaDalMagazzinoAlloScaffale(anyString(), anyInt());
    }

    @Test
    void quandoLoScaffaleSiSvuotaIlRifornimentoAutomaticoVieneSalvato() {
        Prodotto prodotto = prodotto(3, 12);

        magazziniere.aggiorna(prodotto, TipoScorta.SCAFFALE);

        assertEquals(15, prodotto.getQuantitaScaffale());
        assertEquals(0, prodotto.getQuantitaMagazzino());
        verify(repositoryProdotti).spostaDalMagazzinoAlloScaffale("123", 12);
    }
}
