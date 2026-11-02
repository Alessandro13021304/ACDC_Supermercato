package unibg.it.Supermercato;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

import unibg.it.Supermercato.gestionemagazzino.*;
import unibg.it.Supermercato.pagamentiesterni.IPagamento;
import unibg.it.Supermercato.persistenzadati.*;
import unibg.it.Supermercato.sistemaaccesso.*;
import unibg.it.Supermercato.selfscanning.*;

class GestioneSpesaClienteTest {

    @Test
    void clienteGestisceSpesaEFissaTotaleDaPagare() {

        IAutenticazione autenticazione = new IAutenticazione() {

            @Override
            public boolean registraUtente(Persona persona) {
                return false;
            }

            @Override
            public Persona autentica(String email, String password) {
                return null;
            }
        };

        Cliente cliente = new Cliente(
                "Mario",
                "Rossi",
                "mario@email.it",
                "password",
                LocalDate.of(2000, 1, 1)
        );

        Prodotto pasta = new Prodotto(
                "001",
                "Pasta",
                2.50,
                "Pasta di semola",
                0,
                20,
                100,
                1
        );

        Prodotto latte = new Prodotto(
                "002",
                "Latte",
                1.50,
                "Latte intero",
                0,
                15,
                50,
                1
        );

        SessioneSpesa sessione =
                new SessioneSpesa(cliente, autenticazione);

        sessione.scansionaProdotto(pasta);
        sessione.scansionaProdotto(latte);

        sessione.terminaSpesa();

        assertEquals(
                EStatoSessione.PAGAMENTO,
                sessione.getStato()
        );

        assertEquals(
                4.00,
                sessione.getTotaleDaPagare(),
                0.001
        );
    }

    @Test
    void clienteCompletaLaSpesaConPagamento() {

        IAutenticazione autenticazione = new IAutenticazione() {

            @Override
            public boolean registraUtente(Persona persona) {
                return false;
            }

            @Override
            public Persona autentica(String email, String password) {
                return null;
            }
        };

        Cliente cliente = new Cliente(
                "Mario",
                "Rossi",
                "mario@email.it",
                "password",
                LocalDate.of(2000, 1, 1)
        );

        Prodotto pasta = new Prodotto(
                "001",
                "Pasta",
                2.50,
                "Pasta di semola",
                0,
                20,
                100,
                1
        );

        IPagamento pagamento = mock(IPagamento.class);

        when(pagamento.paga(2.50)).thenReturn(true);

        Totem totem = new Totem(1, pagamento);

        SessioneSpesa sessione =
                spy(new SessioneSpesa(cliente, autenticazione));

        doReturn(false).when(sessione).controlloNecessario();

        sessione.setTotem(totem);

        sessione.scansionaProdotto(pasta);
        sessione.terminaSpesa();

        boolean risultato =
                sessione.effettuaPagamento(
                        EModalitaPagamento.CARTA_DI_CREDITO
                );

        assertTrue(risultato);
        assertEquals(
                EStatoSessione.COMPLETATA,
                sessione.getStato()
        );

        verify(pagamento).paga(2.50);
    }

    @Test
    void clienteSegnalatoRichiedeControlloDellaSpesa() {

        IAutenticazione autenticazione = new IAutenticazione() {

            @Override
            public boolean registraUtente(Persona persona) {
                return false;
            }

            @Override
            public Persona autentica(String email, String password) {
                return null;
            }
        };

        Cliente cliente = new Cliente(
                "Mario",
                "Rossi",
                "mario@email.it",
                "password",
                LocalDate.of(2000, 1, 1)
        );

        Prodotto pasta = new Prodotto(
                "001",
                "Pasta",
                2.50,
                "Pasta di semola",
                0,
                20,
                100,
                1
        );

        IRepositoryClienti repositoryClienti =
                mock(IRepositoryClienti.class);

        when(repositoryClienti.isClienteSegnalato(cliente.getId()))
                .thenReturn(true);

        IPagamento pagamento = mock(IPagamento.class);

        when(pagamento.paga(2.50))
                .thenReturn(true);

        Totem totem = new Totem(1, pagamento);

        SessioneSpesa sessione =
                new SessioneSpesa(cliente, autenticazione);

        sessione.setRepositoryClienti(repositoryClienti);
        sessione.setTotem(totem);

        sessione.scansionaProdotto(pasta);
        sessione.terminaSpesa();

        boolean risultato =
                sessione.effettuaPagamento(
                        EModalitaPagamento.CARTA_DI_CREDITO
                );

        assertFalse(risultato);
        assertEquals(
                EStatoSessione.PAGAMENTO,
                sessione.getStato()
        );

        verify(repositoryClienti)
                .isClienteSegnalato(cliente.getId());

        // Senza cassiere il pagamento non deve essere addebitato
        verify(pagamento, never()).paga(anyDouble());
    }

    @Test
    void clienteGeneraLaRicevutaDopoLaSpesa() {

        IAutenticazione autenticazione = new IAutenticazione() {

            @Override
            public boolean registraUtente(Persona persona) {
                return false;
            }

            @Override
            public Persona autentica(String email, String password) {
                return null;
            }
        };

        Cliente cliente = new Cliente(
                "Mario",
                "Rossi",
                "mario@email.it",
                "password",
                LocalDate.of(2000, 1, 1)
        );

        Prodotto pasta = new Prodotto(
                "001",
                "Pasta",
                2.50,
                "Pasta di semola",
                0,
                20,
                100,
                1
        );

        IPagamento pagamento = mock(IPagamento.class);

        when(pagamento.paga(2.50))
                .thenReturn(true);

        Totem totem = new Totem(1, pagamento);

        SessioneSpesa sessione =
                spy(new SessioneSpesa(cliente, autenticazione));

        doReturn(false).when(sessione).controlloNecessario();

        sessione.setTotem(totem);

        sessione.scansionaProdotto(pasta);
        sessione.terminaSpesa();

        boolean pagamentoEffettuato =
                sessione.effettuaPagamento(
                        EModalitaPagamento.CARTA_DI_CREDITO
                );

        assertTrue(pagamentoEffettuato);
        assertEquals(
                EStatoSessione.COMPLETATA,
                sessione.getStato()
        );

        String ricevuta = sessione.generaRicevuta();

        assertNotNull(ricevuta);
        assertFalse(ricevuta.isEmpty());
    }

    @Test
    void clienteUsaPremioFedeltaEApplicaLoSconto() {

        IAutenticazione autenticazione = new IAutenticazione() {

            @Override
            public boolean registraUtente(Persona persona) {
                return false;
            }

            @Override
            public Persona autentica(String email, String password) {
                return null;
            }
        };

        Cliente cliente = new Cliente(
                "Mario",
                "Rossi",
                "mario@email.it",
                "password",
                LocalDate.of(2000, 1, 1)
        );

        Prodotto prodotto = new Prodotto(
                "001",
                "Pasta",
                20.00,
                "Pasta di semola",
                0,
                20,
                100,
                1
        );

        IRepositoryClienti repositoryClienti =
                mock(IRepositoryClienti.class);

        when(repositoryClienti.trovaPuntiByCliente(cliente.getId()))
                .thenReturn(200);

        SessioneSpesa sessione =
                new SessioneSpesa(cliente, autenticazione);

        sessione.setRepositoryClienti(repositoryClienti);

        sessione.scansionaProdotto(prodotto);
        sessione.terminaSpesa();

        assertEquals(
                20.00,
                sessione.getTotaleDaPagare(),
                0.001
        );

        boolean premioApplicato =
                sessione.usaPremioFedelta();

        assertTrue(premioApplicato);

        assertEquals(
                18.00,
                sessione.getTotaleDaPagare(),
                0.001
        );

        // I punti vengono scalati solo al completamento dell'acquisto
        verify(repositoryClienti, never())
                .scalaPunti(anyInt(), anyInt());
    }

    @Test
    void spesaCompletataAggiornaLaGestioneDelleScorte() {

        IAutenticazione autenticazione =
                mock(IAutenticazione.class);

        IScorte gestoreScorte =
                mock(IScorte.class);

        IRepositoryProdotti repositoryProdotti =
                mock(IRepositoryProdotti.class);

        IPagamento pagamento =
                mock(IPagamento.class);

        when(pagamento.paga(2.50)).thenReturn(true);

        Cliente cliente = new Cliente(
                "Mario",
                "Rossi",
                "mario@email.it",
                "password",
                LocalDate.of(2000, 5, 10)
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

        Totem totem = new Totem(1, pagamento);

        SessioneSpesa sessione =
                spy(new SessioneSpesa(cliente, autenticazione));

        doReturn(false)
                .when(sessione)
                .controlloNecessario();

        sessione.setGestoreScorte(gestoreScorte);
        sessione.setRepositoryProdotti(repositoryProdotti);
        sessione.setTotem(totem);

        sessione.scansionaProdotto(prodotto);
        sessione.terminaSpesa();

        boolean risultato =
                sessione.effettuaPagamento(
                        EModalitaPagamento.CARTA_DI_CREDITO
                );

        assertTrue(risultato);
        assertEquals(
                EStatoSessione.COMPLETATA,
                sessione.getStato()
        );

        assertEquals(
                2,
                prodotto.getQuantitaScaffale()
        );

        verify(gestoreScorte).aggiornaScorte(
                prodotto,
                TipoScorta.SCAFFALE
        );
    }
}
