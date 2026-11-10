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

class RegistrazioneClienteTest {

    @BeforeEach
    void preparaDatabase() throws Exception {
        DBConnection.getInstance().resetTestDatabase("clienti");
        DBConnection.getInstance().resetTestDatabase("dipendenti");
    }

    @Test
    void registraClienteEPersisteNelDatabase() {

        IRepositoryClienti repositoryClienti = new DBClienti(true);
        IRepositoryDipendenti repositoryDipendenti = new DBDipendenti(true);

        AuthService authService = new AuthService(repositoryClienti, repositoryDipendenti);

        Cliente cliente = new Cliente("Mario", "Rossi", "mario@email.it", "password123", LocalDate.of(2000, 5, 10));

        boolean registrato = authService.registraUtente(cliente);

        assertTrue(registrato);

        Persona clienteSalvato = authService.autentica("mario@email.it", "password123");

        assertNotNull(clienteSalvato);
        assertTrue(clienteSalvato instanceof Cliente);
        assertEquals("Mario", clienteSalvato.getNome());
        assertEquals("Rossi", clienteSalvato.getCognome());
        assertEquals("mario@email.it", clienteSalvato.getEmail());
    }
}

