package unibg.it.Supermercato;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import unibg.it.Supermercato.gestionemagazzino.Prodotto;
import unibg.it.Supermercato.pagamentiesterni.IPagamento;
import unibg.it.Supermercato.selfscanning.*;
import unibg.it.Supermercato.sistemaaccesso.*;

class ControlloSpesaClienteTest {

    @Test
    void clienteSegnalatoSuperaIlControlloDellaSpesa() {

        IAutenticazione autenticazione = mock(IAutenticazione.class);

        Cliente cliente = new Cliente("Mario", "Rossi", "mario@email.it", "password", LocalDate.of(2000, 1, 1));

        Prodotto pasta = new Prodotto("001", "Pasta", 10.00, "Pasta di semola", 0, 20, 100, 1);

        IRepositoryClienti repositoryClienti = mock(IRepositoryClienti.class);

        when(repositoryClienti.isClienteSegnalato(cliente.getId())).thenReturn(true);

        IPagamento pagamento = mock(IPagamento.class);

        when(pagamento.paga(10.00)).thenReturn(true);

        Cassiere cassiere = new Cassiere("Luigi", "Bianchi", "luigi@email.it", "password", LocalDate.of(1990, 1, 1), repositoryClienti, new ControlloSpesa());

        Totem totem = new Totem(1, pagamento);
        totem.setCassiere(cassiere);

        CarrelloFisico carrelloFisico = new CarrelloFisico();

        carrelloFisico.aggiungiElemento( new ElementoCarrello(pasta, 1, 10.00));

        totem.setCarrelloFisico(carrelloFisico);

        SessioneSpesa sessione = new SessioneSpesa(cliente, autenticazione);

        sessione.setRepositoryClienti(repositoryClienti);
        sessione.setTotem(totem);

        sessione.scansionaProdotto(pasta);
        sessione.terminaSpesa();

        boolean pagamentoEffettuato = sessione.effettuaPagamento(EModalitaPagamento.CARTA_DI_CREDITO);

        assertTrue(pagamentoEffettuato);

        assertEquals(EStatoSessione.CONTROLLO, sessione.getStato());

        assertTrue(totem.effettuaControlloSpesa(sessione));

        assertEquals(EStatoSessione.COMPLETATA, sessione.getStato());

        verify(pagamento).paga(10.00);

        verify(repositoryClienti, never()).registraSegnalazione(anyInt(), any(LocalDateTime.class));
    }

    @Test
    void controlloFallitoSegnalaIlCliente() {

        IAutenticazione autenticazione = mock(IAutenticazione.class);

        Cliente cliente = new Cliente("Mario", "Rossi", "mario@email.it", "password", LocalDate.of(2000, 1, 1));

        Prodotto pasta = new Prodotto("001", "Pasta", 10.00, "Pasta di semola", 0, 20, 100, 1);

        IRepositoryClienti repositoryClienti = mock(IRepositoryClienti.class);

        when(repositoryClienti.isClienteSegnalato(cliente.getId())).thenReturn(true);

        IPagamento pagamento = mock(IPagamento.class);

        when(pagamento.paga(10.00)).thenReturn(true);

        Cassiere cassiere = new Cassiere("Luigi", "Bianchi", "luigi@email.it", "password", LocalDate.of(1990, 1, 1), repositoryClienti, new ControlloSpesa());

        Totem totem = new Totem(1, pagamento);
        totem.setCassiere(cassiere);

        CarrelloFisico carrelloFisico = new CarrelloFisico();

        carrelloFisico.aggiungiElemento(new ElementoCarrello(pasta, 2, 10.00));

        totem.setCarrelloFisico(carrelloFisico);

        SessioneSpesa sessione = new SessioneSpesa(cliente, autenticazione);

        sessione.setRepositoryClienti(repositoryClienti);
        sessione.setTotem(totem);

        sessione.scansionaProdotto(pasta);
        sessione.terminaSpesa();

        boolean pagamentoEffettuato = sessione.effettuaPagamento(EModalitaPagamento.CARTA_DI_CREDITO);

        assertTrue(pagamentoEffettuato);

        assertEquals(EStatoSessione.CONTROLLO, sessione.getStato());

        assertFalse(totem.effettuaControlloSpesa(sessione));

        assertEquals(EStatoSessione.SEGNALATA, sessione.getStato());

        verify(repositoryClienti).registraSegnalazione(eq(cliente.getId()), any(LocalDateTime.class));
    }
}
