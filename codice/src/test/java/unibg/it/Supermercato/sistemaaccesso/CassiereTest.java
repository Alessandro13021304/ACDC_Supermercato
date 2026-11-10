package unibg.it.Supermercato.sistemaaccesso;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

import unibg.it.Supermercato.gestionemagazzino.Prodotto;
import unibg.it.Supermercato.selfscanning.CarrelloFisico;
import unibg.it.Supermercato.selfscanning.ElementoCarrello;
import unibg.it.Supermercato.selfscanning.SessioneSpesa;
import unibg.it.Supermercato.selfscanning.EStatoSessione;

public class CassiereTest {

    private final IAutenticazione autenticazione = mock(IAutenticazione.class);

    @Test
    void controlloCarrelliCorrispondenti() {

        Prodotto prodotto = new Prodotto("123", "Pasta", 2.50, "Pasta", 0, 10, 20, 1);

        Cliente cliente = new Cliente("Mario", "Rossi", "mario@email.it", "password", LocalDate.of(2000, 1, 1));

        SessioneSpesa sessione = new SessioneSpesa(cliente, autenticazione);

        sessione.scansionaProdotto(prodotto);
        
        sessione.terminaSpesa();      
        sessione.avviaControllo();

        CarrelloFisico carrelloFisico = new CarrelloFisico();

        ElementoCarrello elementoFisico = new ElementoCarrello(prodotto, 1, 2.50);

        carrelloFisico.aggiungiElemento(elementoFisico);
        
        IControlloSpesa controlloSpesa = mock(IControlloSpesa.class);
        
        when(controlloSpesa.verificaCorrispondenza(sessione, carrelloFisico)).thenReturn(true);

        Cassiere cassiere = new Cassiere("Luigi", "Verdi", "luigi@email.it", "password", LocalDate.of(1990, 1, 1), mock(IRepositoryClienti.class), controlloSpesa);

        assertTrue(cassiere.effettuaControllo(sessione, carrelloFisico));
    }
    
    @Test
    void controlloCarrelliNonCorrispondenti() {

        Prodotto prodotto = new Prodotto("123", "Pasta", 2.50, "Pasta", 0, 10, 20, 1);

        Cliente cliente = new Cliente("Mario", "Rossi", "mario@email.it", "password", LocalDate.of(2000, 1, 1));

        SessioneSpesa sessione = new SessioneSpesa(cliente, autenticazione);

        sessione.scansionaProdotto(prodotto);
        sessione.scansionaProdotto(prodotto);
        
        sessione.terminaSpesa();
        sessione.avviaControllo();

        CarrelloFisico carrelloFisico = new CarrelloFisico();

        ElementoCarrello elementoFisico = new ElementoCarrello(prodotto, 1, 2.50);

        carrelloFisico.aggiungiElemento(elementoFisico);

        Cassiere cassiere = new Cassiere("Luigi", "Verdi", "luigi@email.it", "password", LocalDate.of(1990, 1, 1), mock(IRepositoryClienti.class), mock(IControlloSpesa.class));

        assertFalse(cassiere.effettuaControllo(sessione, carrelloFisico));
    }
    
    @Test
    void controlloNonCorrispondenteSegnalaSessione() {

        Prodotto prodotto = new Prodotto("123", "Pasta", 2.50, "Pasta", 0, 10, 20, 1);

        Cliente cliente = new Cliente("Mario", "Rossi", "mario@email.it", "password", LocalDate.of(2000, 1, 1));

        SessioneSpesa sessione = new SessioneSpesa(cliente, autenticazione);

        sessione.scansionaProdotto(prodotto);
        sessione.scansionaProdotto(prodotto);
        
        sessione.terminaSpesa();
        sessione.avviaControllo();

        CarrelloFisico carrelloFisico = new CarrelloFisico();

        ElementoCarrello elementoFisico = new ElementoCarrello(prodotto, 1, 2.50);

        carrelloFisico.aggiungiElemento(elementoFisico);
        
        Cassiere cassiere = new Cassiere("Luigi", "Verdi", "luigi@email.it", "password", LocalDate.of(1990, 1, 1), mock(IRepositoryClienti.class), mock(IControlloSpesa.class));

        boolean risultato = cassiere.effettuaControllo(sessione, carrelloFisico);

        assertFalse(risultato);
        assertEquals(EStatoSessione.SEGNALATA, sessione.getStato());
    }
    
    @Test
    void controlloFallitoSegnalaCliente() {
    	
        Cliente cliente = new Cliente("Mario", "Rossi", "mario@email.it", "password", LocalDate.of(2000, 1, 1));

        SessioneSpesa sessione = new SessioneSpesa(cliente, autenticazione);

        Prodotto prodotto = new Prodotto("123", "Pasta", 10.0, "Pasta", 0, 10, 20, 1);

        sessione.scansionaProdotto(prodotto);
        
        sessione.terminaSpesa();
        sessione.avviaControllo();

        CarrelloFisico carrelloFisico = new CarrelloFisico();

        carrelloFisico.aggiungiElemento(new ElementoCarrello(prodotto, 2, 10.0));

        IRepositoryClienti repositoryClienti = mock(IRepositoryClienti.class);

        Cassiere cassiere = new Cassiere("Luigi", "Verdi", "luigi@email.it", "password", LocalDate.of(1990, 1, 1), repositoryClienti, mock(IControlloSpesa.class));

        cassiere.effettuaControllo(sessione, carrelloFisico);

        assertTrue(cliente.isSegnalato());
        
        verify(repositoryClienti).registraSegnalazione(eq(cliente.getId()), any(LocalDateTime.class));
    }
}

