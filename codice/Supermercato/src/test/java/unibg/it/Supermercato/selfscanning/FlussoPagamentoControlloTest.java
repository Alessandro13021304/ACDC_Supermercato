package unibg.it.Supermercato.selfscanning;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import unibg.it.Supermercato.gestionemagazzino.Prodotto;
import unibg.it.Supermercato.pagamentiesterni.IPagamento;
import unibg.it.Supermercato.sistemaaccesso.Cassiere;
import unibg.it.Supermercato.sistemaaccesso.Cliente;
import unibg.it.Supermercato.sistemaaccesso.IAutenticazione;
import unibg.it.Supermercato.sistemaaccesso.IRepositoryClienti;

class FlussoPagamentoControlloTest {

    private IAutenticazione autenticazione;
    private Cliente cliente;
    private Prodotto prodotto;
    private IPagamento pagamento;
    private IRepositoryClienti repositoryClienti;
    private CarrelloFisico carrelloFisico;
    private Totem totem;

    @BeforeEach
    void preparaDati() {
        autenticazione = mock(IAutenticazione.class);

        cliente = new Cliente("Mario", "Rossi", "mario@email.it", "password", LocalDate.of(2000, 1, 1));

        prodotto = new Prodotto("001", "Pasta", 20.0, "Pasta di semola", 0.0, 20, 100, 1);

        pagamento = mock(IPagamento.class);
        when(pagamento.paga(anyDouble())).thenReturn(true);

        repositoryClienti = mock(IRepositoryClienti.class);
        carrelloFisico = new CarrelloFisico();
        totem = new Totem(1, pagamento);
    }

    private void collegaCassiere() {
        Cassiere cassiere = new Cassiere("Luigi", "Bianchi", "luigi@email.it", "password", LocalDate.of(1990, 1, 1), repositoryClienti, new ControlloSpesa());

        totem.setCassiere(cassiere);
        totem.setCarrelloFisico(carrelloFisico);
    }

    private SessioneSpesa creaSessione(boolean serveControllo) {
        SessioneSpesa sessione = spy(new SessioneSpesa(cliente, autenticazione));

        doReturn(serveControllo).when(sessione).controlloNecessario();

        sessione.setTotem(totem);
        sessione.scansionaProdotto(prodotto);
        sessione.terminaSpesa();

        return sessione;
    }

    @Test
    void senzaCassierePagamentoNonVieneAddebitatoSeServeIlControllo() {
        SessioneSpesa sessione = creaSessione(true);

        assertFalse(sessione.effettuaPagamento(EModalitaPagamento.CARTA_DI_CREDITO));
        assertEquals(EStatoSessione.PAGAMENTO, sessione.getStato());

        verify(pagamento, never()).paga(anyDouble());
    }

    @Test
    void ilControlloNonPartePrimaCheIlCassiereLoEseguaConIlCarrelloFisicoPronto() {
        collegaCassiere();
        SessioneSpesa sessione = creaSessione(true);

        assertTrue(sessione.effettuaPagamento(EModalitaPagamento.CARTA_DI_CREDITO));
        assertEquals(EStatoSessione.CONTROLLO, sessione.getStato());

        verify(repositoryClienti, never()).registraSegnalazione(anyInt(), any(LocalDateTime.class));

        carrelloFisico.aggiungiElemento(new ElementoCarrello(prodotto, 1, 20.0));

        assertTrue(totem.effettuaControlloSpesa(sessione));
        assertEquals(EStatoSessione.COMPLETATA, sessione.getStato());
    }

    @Test
    void conDiscrepanzaIlClienteVieneSegnalatoSoloDopoIlControllo() {
        collegaCassiere();
        SessioneSpesa sessione = creaSessione(true);

        assertTrue(sessione.effettuaPagamento(EModalitaPagamento.CARTA_DI_CREDITO));
        assertEquals(EStatoSessione.CONTROLLO, sessione.getStato());

        verify(repositoryClienti, never()).registraSegnalazione(anyInt(), any(LocalDateTime.class));

        carrelloFisico.aggiungiElemento(new ElementoCarrello(prodotto, 2, 20.0));

        assertFalse(totem.effettuaControlloSpesa(sessione));
        assertEquals(EStatoSessione.SEGNALATA, sessione.getStato());

        verify(repositoryClienti).registraSegnalazione(eq(cliente.getId()), any(LocalDateTime.class));
    }

    @Test
    void alTermineDelControlloIlCassiereRisultaNuovamenteAssente() {
        collegaCassiere();
        SessioneSpesa sessione = creaSessione(true);

        assertTrue(sessione.effettuaPagamento(EModalitaPagamento.CARTA_DI_CREDITO));
        assertTrue(totem.isCassiereArrivato());

        carrelloFisico.aggiungiElemento(new ElementoCarrello(prodotto, 1, 20.0));
        totem.effettuaControlloSpesa(sessione);

        assertFalse(totem.isCassiereArrivato());
    }

    @Test
    void dopoIlPagamentoConControlloNonSiPuoPagareUnaSecondaVolta() {
        collegaCassiere();
        SessioneSpesa sessione = creaSessione(true);

        assertTrue(sessione.effettuaPagamento(EModalitaPagamento.CARTA_DI_CREDITO));
        assertFalse(sessione.effettuaPagamento(EModalitaPagamento.CARTA_DI_CREDITO));

        verify(pagamento, times(1)).paga(anyDouble());
    }

    @Test
    void usaPremioFedeltaNonScalaSubitoIPunti() {
        when(repositoryClienti.trovaPuntiByCliente(cliente.getId())).thenReturn(200);

        SessioneSpesa sessione = creaSessione(false);
        sessione.setRepositoryClienti(repositoryClienti);

        assertTrue(sessione.usaPremioFedelta());
        assertEquals(18.0, sessione.getTotaleDaPagare(), 0.001);

        verify(repositoryClienti, never()).scalaPunti(anyInt(), anyInt());
    }

    @Test
    void conPagamentoRifiutatoIPuntiNonVengonoScalati() {
        doReturn(false).when(pagamento).paga(anyDouble());
        when(repositoryClienti.trovaPuntiByCliente(cliente.getId())).thenReturn(200);

        SessioneSpesa sessione = creaSessione(false);
        sessione.setRepositoryClienti(repositoryClienti);

        assertTrue(sessione.usaPremioFedelta());
        assertFalse(sessione.effettuaPagamento(EModalitaPagamento.CARTA_DI_CREDITO));
        assertEquals(EStatoSessione.PAGAMENTO, sessione.getStato());

        verify(repositoryClienti, never()).scalaPunti(anyInt(), anyInt());
    }

    @Test
    void alCompletamentoIPuntiVengonoScalatiPrimaDiAggiungereQuelliNuovi() {
        when(repositoryClienti.trovaPuntiByCliente(cliente.getId())).thenReturn(200);

        SessioneSpesa sessione = creaSessione(false);
        sessione.setRepositoryClienti(repositoryClienti);

        assertTrue(sessione.usaPremioFedelta());
        assertTrue(sessione.effettuaPagamento(EModalitaPagamento.CARTA_DI_CREDITO));
        assertEquals(EStatoSessione.COMPLETATA, sessione.getStato());

        InOrder ordine = inOrder(repositoryClienti);
        ordine.verify(repositoryClienti).scalaPunti(cliente.getId(), 200);
        ordine.verify(repositoryClienti).aggiungiPunti(cliente.getId(), 9);
    }

    @Test
    void seLaSessioneEVieneSegnalataIPuntiNonVengonoScalati() {
        collegaCassiere();
        when(repositoryClienti.trovaPuntiByCliente(cliente.getId())).thenReturn(200);

        SessioneSpesa sessione = creaSessione(true);
        sessione.setRepositoryClienti(repositoryClienti);

        assertTrue(sessione.usaPremioFedelta());
        assertTrue(sessione.effettuaPagamento(EModalitaPagamento.CARTA_DI_CREDITO));

        carrelloFisico.aggiungiElemento(new ElementoCarrello(prodotto, 2, 18.0));

        assertFalse(totem.effettuaControlloSpesa(sessione));
        assertEquals(EStatoSessione.SEGNALATA, sessione.getStato());

        verify(repositoryClienti, never()).scalaPunti(anyInt(), anyInt());
    }
}

