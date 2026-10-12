package unibg.it.Supermercato.persistenzadati;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.LocalDateTime;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import unibg.it.Supermercato.selfscanning.*;
import unibg.it.Supermercato.sistemaaccesso.*;


class DBClientiTest {
	
    private final IAutenticazione autenticazione = new IAutenticazione() {

        @Override
        public boolean registraUtente(Persona persona) {
            return false;
        }

        @Override
        public Persona autentica(String email, String password) {
            return null;
        }
    };

    @BeforeEach
    void inizializzaDatabaseDiTest() throws Exception {
        DBConnection.getInstance().resetTestDatabase("clienti");
    }

    @Test
    void testSalvaCliente() throws Exception {

        Cliente cliente = new Cliente(
                "Mario",
                "Rossi",
                "mario.rossi@test.it",
                "password",
                LocalDate.of(1995, 5, 10)
        );

        DBClienti repository = new DBClienti(true);

        assertDoesNotThrow(() -> repository.salvaCliente(cliente));

        String sql = "SELECT nome, cognome, email, password, data_nascita "
                   + "FROM clienti WHERE email = ?";

        try (java.sql.Connection connection = DBConnection.getInstance().getTestConnection("clienti"); java.sql.PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, "mario.rossi@test.it");

            try (java.sql.ResultSet resultSet = statement.executeQuery()) {

                assertTrue(resultSet.next());

                assertEquals("Mario", resultSet.getString("nome"));
                assertEquals("Rossi", resultSet.getString("cognome"));
                assertEquals("mario.rossi@test.it", resultSet.getString("email"));
                assertEquals("password", resultSet.getString("password"));
                assertEquals(LocalDate.of(1995, 5, 10), resultSet.getDate("data_nascita").toLocalDate());
            }
        }
    }
    
    @Test
    void testVerificaCredenziali() {

        DBClienti repository = new DBClienti(true);

        Cliente cliente = new Cliente(
                "Mario",
                "Rossi",
                "mario.rossi@test.it",
                "password",
                LocalDate.of(1995, 5, 10)
        );

        repository.salvaCliente(cliente);

        Cliente risultato = repository.verificaCredenziali("mario.rossi@test.it", "password");

        assertNotNull(risultato);

        assertEquals("Mario", risultato.getNome());
        assertEquals("Rossi", risultato.getCognome());
        assertEquals("mario.rossi@test.it", risultato.getEmail());
        assertEquals("password", risultato.getPassword());
        assertEquals(LocalDate.of(1995, 5, 10), risultato.getDataNascita());

        assertTrue(risultato.getId() > 0);
    }
    
    @Test
    void testVerificaCredenzialiErrate() {

        DBClienti repository = new DBClienti(true);

        Cliente cliente = new Cliente(
                "Mario",
                "Rossi",
                "mario.rossi@test.it",
                "password",
                LocalDate.of(1995, 5, 10)
        );

        repository.salvaCliente(cliente);

        Cliente risultato = repository.verificaCredenziali("mario.rossi@test.it", "passwordErrata");

        assertNull(risultato);
    }
    
    @Test
    void testTrovaClienteById() {

        DBClienti repository = new DBClienti(true);

        Cliente cliente = new Cliente(
                "Mario",
                "Rossi",
                "mario.rossi@test.it",
                "password",
                LocalDate.of(1995, 5, 10)
        );

        repository.salvaCliente(cliente);

        Cliente risultato = repository.trovaClienteById(cliente.getId());

        assertNotNull(risultato);

        assertEquals(cliente.getId(), risultato.getId());
        assertEquals("Mario", risultato.getNome());
        assertEquals("Rossi", risultato.getCognome());
        assertEquals("mario.rossi@test.it", risultato.getEmail());
        assertEquals("password", risultato.getPassword());
        assertEquals(LocalDate.of(1995, 5, 10), risultato.getDataNascita());
    }
    
    @Test
    void testTrovaTuttiIClienti() {

        DBClienti repository = new DBClienti(true);

        Cliente cliente1 = new Cliente(
                "Mario",
                "Rossi",
                "mario.rossi@test.it",
                "password1",
                LocalDate.of(1995, 5, 10)
        );

        Cliente cliente2 = new Cliente(
                "Anna",
                "Bianchi",
                "anna.bianchi@test.it",
                "password2",
                LocalDate.of(1998, 8, 20)
        );

        Cliente cliente3 = new Cliente(
                "Luca",
                "Verdi",
                "luca.verdi@test.it",
                "password3",
                LocalDate.of(1992, 3, 15)
        );

        repository.salvaCliente(cliente1);
        repository.salvaCliente(cliente2);
        repository.salvaCliente(cliente3);

        List<Cliente> clienti = repository.trovaTuttiIClienti();

        assertNotNull(clienti);
        assertEquals(3, clienti.size());

        assertEquals("Mario", clienti.get(0).getNome());
        assertEquals("Anna", clienti.get(1).getNome());
        assertEquals("Luca", clienti.get(2).getNome());

        assertEquals("mario.rossi@test.it", clienti.get(0).getEmail());
        assertEquals("anna.bianchi@test.it", clienti.get(1).getEmail());
        assertEquals("luca.verdi@test.it", clienti.get(2).getEmail());

        assertTrue(clienti.get(0).getId() > 0);
        assertTrue(clienti.get(1).getId() > 0);
        assertTrue(clienti.get(2).getId() > 0);
    }
    
    @Test
    void aggiungiPuntiDeveAccumulareIPunti() throws Exception {

        DBConnection.getInstance().resetTestDatabase("clienti");

        DBClienti dbClienti = new DBClienti(true);

        try (Connection connection = DBConnection.getInstance().getTestConnection("clienti");
             PreparedStatement statement = connection.prepareStatement("INSERT INTO clienti (id, nome, cognome, email, password, data_nascita, punti_fedelta, segnalato)"
             + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)")) {

            statement.setInt(1, 1);
            statement.setString(2, "Mario");
            statement.setString(3, "Rossi");
            statement.setString(4, "mario@test.it");
            statement.setString(5, "password");
            statement.setDate(6, java.sql.Date.valueOf(LocalDate.of(1995, 5, 10)));
            statement.setInt(7, 0);
            statement.setBoolean(8, false);

            statement.executeUpdate();
        }

        dbClienti.aggiungiPunti(1, 20);

        try (Connection connection = DBConnection.getInstance().getTestConnection("clienti");
             PreparedStatement statement = connection.prepareStatement("SELECT punti_fedelta FROM clienti WHERE id = ?")) {

            statement.setInt(1, 1);

            try (ResultSet resultSet = statement.executeQuery()) {

                assertTrue(resultSet.next());
                assertEquals(20, resultSet.getInt("punti_fedelta"));
            }
        }

        dbClienti.aggiungiPunti(1, 15);

        try (Connection connection = DBConnection.getInstance().getTestConnection("clienti");
             PreparedStatement statement = connection.prepareStatement("SELECT punti_fedelta FROM clienti WHERE id = ?")) {

            statement.setInt(1, 1);

            try (ResultSet resultSet = statement.executeQuery()) {

                assertTrue(resultSet.next());
                assertEquals(35, resultSet.getInt("punti_fedelta"));
            }
        }
    }
    
    @Test
    void scalaPuntiDeveDiminuireIlSaldo() throws Exception {

        DBConnection.getInstance().resetTestDatabase("clienti");

        DBClienti dbClienti = new DBClienti(true);

        try (Connection connection = DBConnection.getInstance().getTestConnection("clienti");
             PreparedStatement statement = connection.prepareStatement("INSERT INTO clienti (id, nome, cognome, email, password, data_nascita, punti_fedelta, segnalato)"
             + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)")) {

            statement.setInt(1, 1);
            statement.setString(2, "Mario");
            statement.setString(3, "Rossi");
            statement.setString(4, "mario@test.it");
            statement.setString(5, "password");
            statement.setDate(6, java.sql.Date.valueOf(LocalDate.of(1995, 5, 10)));
            statement.setInt(7, 250);
            statement.setBoolean(8, false);

            statement.executeUpdate();
        }

        dbClienti.scalaPunti(1, 100);

        try (Connection connection = DBConnection.getInstance().getTestConnection("clienti");
             PreparedStatement statement = connection.prepareStatement("SELECT punti_fedelta FROM clienti WHERE id = ?")) {

            statement.setInt(1, 1);

            try (ResultSet resultSet = statement.executeQuery()) {

                assertTrue(resultSet.next());
                assertEquals(150, resultSet.getInt("punti_fedelta"));
            }
        }
    }
    
    @Test
    void scalaPuntiNonDeveScalareSeIPuntiSonoInsufficienti() throws Exception {

        DBConnection.getInstance().resetTestDatabase("clienti");

        DBClienti dbClienti = new DBClienti(true);

        try (Connection connection = DBConnection.getInstance().getTestConnection("clienti");
             PreparedStatement statement = connection.prepareStatement("INSERT INTO clienti (id, nome, cognome, email, password, data_nascita, punti_fedelta, segnalato)"
             + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)")) {

            statement.setInt(1, 1);
            statement.setString(2, "Mario");
            statement.setString(3, "Rossi");
            statement.setString(4, "mario@test.it");
            statement.setString(5, "password");
            statement.setDate(6, java.sql.Date.valueOf(LocalDate.of(1995, 5, 10)));
            statement.setInt(7, 100);
            statement.setBoolean(8, false);

            statement.executeUpdate();
        }

        dbClienti.scalaPunti(1, 150);

        try (Connection connection = DBConnection.getInstance().getTestConnection("clienti");
             PreparedStatement statement = connection.prepareStatement("SELECT punti_fedelta FROM clienti WHERE id = ?")) {

            statement.setInt(1, 1);

            try (ResultSet resultSet = statement.executeQuery()) {

                assertTrue(resultSet.next());
                assertEquals(100, resultSet.getInt("punti_fedelta"));
            }
        }
    }
    
    @Test
    void salvaStoricoDeveInserireLaSpesaNelDatabase() throws Exception {

        DBConnection.getInstance().resetTestDatabase("clienti");

        DBClienti dbClienti = new DBClienti(true);

        Cliente cliente = new Cliente(
                "Mario",
                "Rossi",
                "mario.rossi@email.it",
                "password",
                LocalDate.of(2000, 1, 1)
        );

        dbClienti.salvaCliente(cliente);

        SessioneSpesa sessione = new SessioneSpesa(cliente, autenticazione);

        dbClienti.salvaStorico(cliente.getId(), sessione);

        try (Connection connection = DBConnection.getInstance().getTestConnection("clienti");
             PreparedStatement statement = connection.prepareStatement("SELECT cliente_id, totale FROM storico_spese WHERE cliente_id = ?")) {

            statement.setInt(1, cliente.getId());

            try (ResultSet resultSet = statement.executeQuery()) {

                assertTrue(resultSet.next());
                assertEquals(cliente.getId(), resultSet.getInt("cliente_id"));
                assertEquals(0.0, resultSet.getDouble("totale"));
            }
        }
    }
    
    @Test
    void trovaStoricoByClienteDeveRecuperareDataETotale() throws Exception {

        DBConnection.getInstance().resetTestDatabase("clienti");

        DBClienti dbClienti = new DBClienti(true);

        Cliente cliente = new Cliente(
                "Mario",
                "Rossi",
                "mario.rossi@email.it",
                "password",
                LocalDate.of(2000, 1, 1)
        );

        dbClienti.salvaCliente(cliente);

        String sql = "INSERT INTO storico_spese"
                   + "(cliente_id, data_spesa, totale)"
                   + "VALUES (?, ?, ?)";

        LocalDateTime dataSpesa = LocalDateTime.of(2026, 10, 5, 10, 30);

        try (Connection connection = DBConnection.getInstance().getTestConnection("clienti"); PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, cliente.getId());
            statement.setTimestamp(2, java.sql.Timestamp.valueOf(dataSpesa));
            statement.setDouble(3, 25.50);

            statement.executeUpdate();
        }

        List<AcquistoStorico> storico = dbClienti.trovaStoricoByCliente(cliente.getId());

        assertNotNull(storico);
        assertEquals(1, storico.size());

        assertEquals(dataSpesa, storico.get(0).getData());
        assertEquals(25.50, storico.get(0).getTotale());
    }
    
    @Test
    void registraSegnalazioneDeveInserireLaSegnalazioneNelDatabase() throws Exception {

        DBConnection.getInstance().resetTestDatabase("clienti");

        DBClienti dbClienti = new DBClienti(true);

        Cliente cliente = new Cliente(
                "Mario",
                "Rossi",
                "mario.rossi@email.it",
                "password",
                LocalDate.of(2000, 1, 1)
        );

        dbClienti.salvaCliente(cliente);

        SessioneSpesa sessione = new SessioneSpesa(cliente, autenticazione);
        Segnalazione segnalazione = new Segnalazione(sessione);

        dbClienti.registraSegnalazione(segnalazione);

        try (Connection connection = DBConnection.getInstance().getTestConnection("clienti");
             PreparedStatement statement = connection.prepareStatement("SELECT cliente_id, data_segnalazione FROM segnalazioni WHERE cliente_id = ?")) {

            statement.setInt(1, cliente.getId());

            try (ResultSet resultSet = statement.executeQuery()) {

                assertTrue(resultSet.next());
                assertEquals(cliente.getId(), resultSet.getInt("cliente_id"));
                assertNotNull(resultSet.getTimestamp("data_segnalazione"));
            }
        }
    }
}
