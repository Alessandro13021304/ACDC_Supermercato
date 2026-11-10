/**
 * @author Chiara Dentella
 */

package unibg.it.Supermercato.persistenzadati;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

import unibg.it.Supermercato.sistemaaccesso.AcquistoStorico;
import unibg.it.Supermercato.sistemaaccesso.Cliente;
import unibg.it.Supermercato.sistemaaccesso.IRepositoryClienti;

public class DBClienti implements IRepositoryClienti {

    private boolean usaDatabaseTest;

    // non usa il database di test
    public DBClienti() {
        this.usaDatabaseTest = false;
    }

    // usa il database di test
    public DBClienti(boolean usaDatabaseTest) {
        this.usaDatabaseTest = usaDatabaseTest;
    }

    /** questo metodo si connette a DBConnection
	 * @return l'istanza della connessione trovata
	 */
    private Connection getConnection() throws SQLException {
        if (usaDatabaseTest) {
            return DBConnection.getInstance().getTestConnection("clienti");
        }

        return DBConnection.getInstance().getConnection("clienti");
    }

    /** questo metodo salva un cliente in persistenza
     * @param il cliente da salvare
	 */
    @Override
    public void salvaCliente(Cliente cliente) {

        if (cliente == null) {
            return;
        }

        String sql = "INSERT INTO clienti (nome, cognome, email, password, data_nascita) "
                   + "VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, cliente.getNome());
            statement.setString(2, cliente.getCognome());
            statement.setString(3, cliente.getEmail());
            statement.setString(4, cliente.getPassword());
            statement.setDate(5, java.sql.Date.valueOf(cliente.getDataNascita()));

            statement.executeUpdate();

            try (ResultSet resultSet = statement.getGeneratedKeys()) {
                if (resultSet.next()) {
                    cliente.setId(resultSet.getInt(1));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Errore nel salvataggio del cliente.", e);
        }
    }

    /** questo metodo verifica le credenziali di un cliente
     * @param l'email e la password del cliente che si sta autenticando
     * @return il cliente autenticato
	 */
    @Override
    public Cliente verificaCredenziali(String email, String password) {

        if (email == null || password == null) {
            return null;
        }

        String sql = "SELECT id, nome, cognome, email, password, data_nascita, punti_fedelta, segnalato "
                   + "FROM clienti "
                   + "WHERE email = ? AND password = ?";

        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, email);
            statement.setString(2, password);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return creaClienteDaRisultato(resultSet);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Errore nella verifica delle credenziali del cliente.", e);
        }

        return null;
    }

    /** questo metodo trova un cliente tramite il suo id
     * @param l'id del cliente da trovare
     * @return il cliente trovato
	 */
    @Override
    public Cliente trovaClienteById(int clienteId) {

        String sql = "SELECT id, nome, cognome, email, password, data_nascita, punti_fedelta, segnalato "
                   + "FROM clienti "
                   + "WHERE id = ?";

        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, clienteId);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return creaClienteDaRisultato(resultSet);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Errore nel recupero del cliente.", e);
        }

        return null;
    }
    
    /** questo metodo permette di visualizzare tutti i clienti
     * @return la lista dei clienti 
	 */
    @Override
    public List<Cliente> trovaTuttiIClienti() {

        List<Cliente> clienti = new ArrayList<>();

        String sql = "SELECT id, nome, cognome, email, password, data_nascita, punti_fedelta, segnalato "
                   + "FROM clienti";

        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql); ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                clienti.add(creaClienteDaRisultato(resultSet));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Errore nel recupero dei clienti.", e);
        }

        return clienti;
    }

    /** questo metodo trova i punti fedeltà di un cliente
     * @param l'id del cliente di cui trovare i punti
     * @return i punti del cliente
	 */
    @Override
    public int trovaPuntiByCliente(int clienteId) {
        String sql = "SELECT punti_fedelta "
                   + "FROM clienti "
                   + "WHERE id = ?";

        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, clienteId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("punti_fedelta");
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Errore nel recupero dei punti del cliente.", e);
        }

        return 0;
    }

    /** questo metodo aggiunge punti fedeltà ad un cliente
     * @param l'id del cliente a cui aggiungere punti e i punti da aggiungere
	 */
    @Override
    public void aggiungiPunti(int clienteId, int punti) {

        String sql = "UPDATE clienti "
                   + "SET punti_fedelta = punti_fedelta + ? "
                   + "WHERE id = ?";

        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, punti);
            statement.setInt(2, clienteId);

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Errore nell'aggiunta dei punti del cliente.", e);
        }
    }

    /** questo metodo scala punti fedeltà ad un cliente
     * @param l'id del cliente a cui scalare punti e i punti da scalare
	 */
    @Override
    public void scalaPunti(int clienteId, int punti) {

        String sql = "UPDATE clienti "
                   + "SET punti_fedelta = punti_fedelta - ? "
                   + "WHERE id = ? AND punti_fedelta >= ?";

        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, punti);
            statement.setInt(2, clienteId);
            statement.setInt(3, punti);

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Errore nella scalatura dei punti del cliente.", e);
        }
    }

    /** questo metodo salva lo storico di una spesa in persistenza
     * @param l'id del cliente che ha fatto la spesa, la data e l'importo pagato
	 */
    @Override
    public void salvaStorico(int clienteId, LocalDateTime data, double totale) {

        if (data == null) {
            return;
        }

        String sql = "INSERT INTO storico_spese (cliente_id, data_spesa, totale) "
                   + "VALUES (?, ?, ?)";

        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, clienteId);
            statement.setTimestamp(2, java.sql.Timestamp.valueOf(data));
            statement.setDouble(3, totale);

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Errore nel salvataggio dello storico della spesa.", e);
        }
    }

    /** questo metodo fa vedere le spese fatte da un cliente
     * @param l'id del cliente di cui si vogliono vedere le spese
     * @return la lista delle spese fatte
	 */
    @Override
    public List<AcquistoStorico> trovaStoricoByCliente(int clienteId) {

        String sql = "SELECT data_spesa, totale "
                   + "FROM storico_spese "
                   + "WHERE cliente_id = ? "
                   + "ORDER BY data_spesa";

        List<AcquistoStorico> storico = new ArrayList<>();

        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, clienteId);

            try (ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {

                    LocalDateTime data = resultSet.getTimestamp("data_spesa").toLocalDateTime();
                    double totale = resultSet.getDouble("totale");

                    storico.add(new AcquistoStorico(data, totale));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Errore nel recupero dello storico del cliente.", e);
        }

        return storico;
    }

    /** questo metodo salva una segnalazione in persistenza
     * @param l'id del cliente segnalato e la data in cui è stato segnalato
	 */
    @Override
    public void registraSegnalazione(int clienteId, LocalDateTime data) {

        if (data == null) {
            return;
        }

        String sql = "INSERT INTO segnalazioni (cliente_id, data_segnalazione) "
                   + "VALUES (?, ?)";

        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, clienteId);
            statement.setTimestamp(2, java.sql.Timestamp.valueOf(data));

            statement.executeUpdate();

            String sqlAggiornaCliente = "UPDATE clienti "
                                      + "SET segnalato = TRUE "
                                      + "WHERE id = ?";

            try (PreparedStatement updateStatement = connection.prepareStatement(sqlAggiornaCliente)) {

                updateStatement.setInt(1, clienteId);
                updateStatement.executeUpdate();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Errore nel salvataggio della segnalazione.", e);
        }
    }

    /** questo metodo fa vedere le segnalazioni di un cliente
     * @param l'id del cliente segnalato
     * @return la lista delle date in cui è stato segnalato
	 */
    @Override
    public List<LocalDateTime> trovaSegnalazioniByCliente(int clienteId) {

        String sql = "SELECT data_segnalazione "
                   + "FROM segnalazioni "
                   + "WHERE cliente_id = ? "
                   + "ORDER BY data_segnalazione";

        List<LocalDateTime> segnalazioni = new ArrayList<>();

        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, clienteId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    segnalazioni.add(resultSet.getTimestamp("data_segnalazione").toLocalDateTime());
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Errore nel recupero delle segnalazioni del cliente.", e);
        }

        return segnalazioni;
    }

    /** questo metodo dice se un cliente è stato segnalato o no
     * @param l'id del cliente di cui si vuole sapere se è stato segnalato
     * @return true se è stato segnalato, altrimenti false
	 */
    @Override
    public boolean isClienteSegnalato(int clienteId) {

        String sql = "SELECT segnalato "
                   + "FROM clienti "
                   + "WHERE id = ?";

        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, clienteId);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return resultSet.getBoolean("segnalato");
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Errore nella verifica della segnalazione del cliente.", e);
        }

        return false;
    }

    /** questo metodo crea un cliente
     * @param i valori del cliente da creare
     * @return il cliente creato
	 */
    private Cliente creaClienteDaRisultato(ResultSet resultSet) throws SQLException {

        Cliente cliente = new Cliente(
                resultSet.getString("nome"),
                resultSet.getString("cognome"),
                resultSet.getString("email"),
                resultSet.getString("password"),
                resultSet.getDate("data_nascita").toLocalDate()
        );

        cliente.setId(resultSet.getInt("id"));
        cliente.setPuntiFedelta(resultSet.getInt("punti_fedelta"));
        cliente.setRepositoryClienti(this);
        if (resultSet.getBoolean("segnalato")) {
            cliente.segnala();
        }

        return cliente;
    }
}