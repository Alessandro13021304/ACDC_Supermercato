package unibg.it.Supermercato.selfscanning;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import unibg.it.Supermercato.gestionemagazzino.*;
import unibg.it.Supermercato.pagamentiesterni.IPagamento;
import unibg.it.Supermercato.sistemaaccesso.Cliente;
import unibg.it.Supermercato.sistemaaccesso.IAutenticazione;

class SessioneSpesaScorteAtomicheTest {

    private IRepositoryProdotti repositoryProdotti;
    private IScorte gestoreScorte;
    private SessioneSpesa sessione;

    @BeforeEach
    void preparaDati() {
        repositoryProdotti = mock(IRepositoryProdotti.class);
        gestoreScorte = mock(IScorte.class);

        IPagamento pagamento = mock(IPagamento.class);
        when(pagamento.paga(anyDouble())).thenReturn(true);

        Cliente cliente = new Cliente("Mario", "Rossi", "mario@email.it", "password", LocalDate.of(2000, 1, 1));

        sessione = spy(new SessioneSpesa(cliente, mock(IAutenticazione.class)));
        doReturn(false).when(sessione).controlloNecessario();

        sessione.setRepositoryProdotti(repositoryProdotti);
        sessione.setGestoreScorte(gestoreScorte);
        sessione.setTotem(new Totem(1, pagamento));
    }

    @Test
    void loScaffaleVieneAggiornatoConUnaScritturaAtomicaENonConUnaSovrascrittura() {
        Prodotto pasta = new Prodotto("001", "Pasta", 2.50, "Pasta", 0.0, 10, 50, 1);

        sessione.scansionaProdotto(pasta);
        sessione.scansionaProdotto(pasta);
        sessione.terminaSpesa();

        assertTrue(sessione.effettuaPagamento(EModalitaPagamento.CARTA_DI_CREDITO));

        assertEquals(8, pasta.getQuantitaScaffale());
        verify(repositoryProdotti).decrementaQuantitaScaffale("001", 2);
        verify(repositoryProdotti, never()).modificaProdotto(any(Prodotto.class));
        verify(gestoreScorte).aggiornaScorte(pasta, TipoScorta.SCAFFALE);
    }

    @Test
    void ogniProdottoDelCarrelloAggiornaLaPropriaQuantita() {
        Prodotto pasta = new Prodotto("001", "Pasta", 2.50, "Pasta", 0.0, 10, 50, 1);
        Prodotto latte = new Prodotto("002", "Latte", 1.50, "Latte", 0.0, 15, 30, 1);

        sessione.scansionaProdotto(pasta);
        sessione.scansionaProdotto(latte);
        sessione.scansionaProdotto(latte);
        sessione.terminaSpesa();

        assertTrue(sessione.effettuaPagamento(EModalitaPagamento.CARTA_DI_CREDITO));

        verify(repositoryProdotti).decrementaQuantitaScaffale("001", 1);
        verify(repositoryProdotti).decrementaQuantitaScaffale("002", 2);
        assertEquals(9, pasta.getQuantitaScaffale());
        assertEquals(13, latte.getQuantitaScaffale());
    }
}

