package unibg.it.Supermercato;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import unibg.it.Supermercato.persistenzadati.DBClienti;
import unibg.it.Supermercato.persistenzadati.DBConnection;
import unibg.it.Supermercato.persistenzadati.DBDipendenti;
import unibg.it.Supermercato.sistemaaccesso.AuthService;
import unibg.it.Supermercato.sistemaaccesso.Cliente;
import unibg.it.Supermercato.sistemaaccesso.IRepositoryClienti;
import unibg.it.Supermercato.sistemaaccesso.IRepositoryDipendenti;
import unibg.it.Supermercato.sistemaaccesso.Persona;

class AutenticazioneClienteTest {

    @BeforeEach
    void preparaDatabase() throws Exception {
        DBConnection.getInstance().resetTestDatabase("clienti");
        DBConnection.getInstance().resetTestDatabase("dipendenti");
    }

    @Test
    void autenticaClienteRegistrato() {

        IRepositoryClienti repositoryClienti = new DBClienti(true);
        IRepositoryDipendenti repositoryDipendenti = new DBDipendenti(true);

        AuthService authService = new AuthService(
                repositoryClienti,
                repositoryDipendenti
        );

        Cliente cliente = new Cliente(
                "Mario",
                "Rossi",
                "mario@email.it",
                "password123",
                LocalDate.of(2000, 5, 10)
        );

        repositoryClienti.salvaCliente(cliente);

        Persona risultato = authService.autentica(
                "mario@email.it",
                "password123"
        );

        assertNotNull(risultato);
        assertTrue(risultato instanceof Cliente);
        assertEquals("Mario", risultato.getNome());
        assertEquals("Rossi", risultato.getCognome());
        assertEquals("mario@email.it", risultato.getEmail());
    }
    
    @Test
    void rifiutaAutenticazioneConPasswordErrata() {

        IRepositoryClienti repositoryClienti = new DBClienti(true);
        IRepositoryDipendenti repositoryDipendenti = new DBDipendenti(true);

        AuthService authService = new AuthService(
                repositoryClienti,
                repositoryDipendenti
        );

        Cliente cliente = new Cliente(
                "Mario",
                "Rossi",
                "mario@email.it",
                "password123",
                LocalDate.of(2000, 5, 10)
        );

        repositoryClienti.salvaCliente(cliente);

        Persona risultato = authService.autentica(
                "mario@email.it",
                "passwordSbagliata"
        );

        assertNull(risultato);
    }
}