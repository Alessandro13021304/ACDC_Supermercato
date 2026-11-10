package unibg.it.Supermercato.sistemaaccesso;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.BeforeEach;
import unibg.it.Supermercato.persistenzadati.DBConnection;
import unibg.it.Supermercato.persistenzadati.DBClienti;
import unibg.it.Supermercato.persistenzadati.DBDipendenti;

class AuthServiceTest {
	
	@BeforeEach
	void preparaDatabase() throws Exception {
	    DBConnection.getInstance().resetTestDatabase("clienti");
	    DBConnection.getInstance().resetTestDatabase("dipendenti");
	}

    @Test
    void registraUtenteConSuccesso() {
    	IRepositoryClienti repositoryClienti = new DBClienti(true);
    	IRepositoryDipendenti repositoryDipendenti = new DBDipendenti(true);

    	AuthService authService = new AuthService(repositoryClienti, repositoryDipendenti);

        Cliente cliente = new Cliente("Mario", "Rossi", "mario@email.it", "password", LocalDate.of(2000, 1, 1));

        boolean risultato = authService.registraUtente(cliente);

        assertTrue(risultato);
    }
    
    @Test
    void nonPermetteRegistrazioneConEmailDuplicata() {
    	IRepositoryClienti repositoryClienti = new DBClienti(true);
    	IRepositoryDipendenti repositoryDipendenti = new DBDipendenti(true);

    	AuthService authService = new AuthService(repositoryClienti, repositoryDipendenti);

        Cliente cliente1 = new Cliente("Mario", "Rossi", "mario@email.it", "password", LocalDate.of(2000, 1, 1));

        Cliente cliente2 = new Cliente("Luigi", "Verdi", "mario@email.it", "password456", LocalDate.of(1999, 3, 15));

        assertTrue(authService.registraUtente(cliente1));
        assertFalse(authService.registraUtente(cliente2));
    }
    
    @Test
    void autenticaUtenteConCredenzialiCorrette() {
    	IRepositoryClienti repositoryClienti = new DBClienti(true);
    	IRepositoryDipendenti repositoryDipendenti = new DBDipendenti(true);

    	AuthService authService = new AuthService(repositoryClienti, repositoryDipendenti);

        Cliente cliente = new Cliente("Mario", "Rossi", "mario@email.it", "password", LocalDate.of(2000, 1, 1));

        authService.registraUtente(cliente);

        Persona risultato = authService.autentica("mario@email.it", "password");

        assertNotNull(risultato);
        assertTrue(risultato instanceof Cliente);
        assertEquals(cliente.getEmail(), risultato.getEmail());
    }
    
    @Test
    void rifiutaAutenticazioneConPasswordErrata() {
    	IRepositoryClienti repositoryClienti = new DBClienti(true);
    	IRepositoryDipendenti repositoryDipendenti = new DBDipendenti(true);

    	AuthService authService = new AuthService(repositoryClienti, repositoryDipendenti);

        Cliente cliente = new Cliente("Mario", "Rossi", "mario@email.it", "password", LocalDate.of(2000, 1, 1));

        authService.registraUtente(cliente);

        Persona risultato = authService.autentica("mario@email.it", "passwordSbagliata");

        assertNull(risultato);
    }
}

