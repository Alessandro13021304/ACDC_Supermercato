package unibg.it.Supermercato.persistenzadati;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import unibg.it.Supermercato.gestionemagazzino.Prodotto;
import unibg.it.Supermercato.selfscanning.AcquistoStorico;
import unibg.it.Supermercato.sistemaaccesso.Cliente;
import unibg.it.Supermercato.selfscanning.SessioneSpesa;
import unibg.it.Supermercato.selfscanning.Segnalazione;

public class DBClienti implements IRepositoryClienti {

    private boolean usaDatabaseTest;

    public DBClienti() {
        this.usaDatabaseTest = false;
    }

    public DBClienti(boolean usaDatabaseTest) {
        this.usaDatabaseTest = usaDatabaseTest;
    }

    private Connection getConnection() throws SQLException {
        if (usaDatabaseTest) {
            return DBConnection.getInstance().getTestConnection("clienti");
        }

        return DBConnection.getInstance().getConnection("clienti");
    }

    @Override
    public void salvaCliente(Cliente cliente) {

    	if (cliente == null) {
            return;
        }
    	
    	String sql = "INSERT INTO clienti (nome, cognome, email, password, data_nascita)"
                   + "VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql,java.sql.Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, cliente.getNome());
            statement.setString(2, cliente.getCognome());
            statement.setString(3, cliente.getEmail());
            statement.setString(4, cliente.getPassword());
            statement.setDate(5, java.sql.Date.valueOf(cliente.getDataNascita()));

            statement.executeUpdate();
            
            try (java.sql.ResultSet resultSet = statement.getGeneratedKeys()) {
                if (resultSet.next()) {
                    cliente.setId(resultSet.getInt(1));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Errore nel salvataggio del cliente.", e);
        }
    }

    @Override
    public Cliente verificaCredenziali(String email, String password) {

    	if (email == null || password == null) {
            return null;
        }
    	
    	String sql = "SELECT id, nome, cognome, email, password, data_nascita, punti_fedelta, segnalato"
    	           + "FROM clienti"
    	           + "WHERE email = ? AND password = ?";

        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, email);
            statement.setString(2, password);

            try (java.sql.ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {	

                    Cliente cliente = new Cliente(
                    		resultSet.getString("nome"),
                            resultSet.getString("cognome"),
                            resultSet.getString("email"),
                            resultSet.getString("password"),
                            resultSet.getDate("data_nascita").toLocalDate()
                    );

                    cliente.setId(resultSet.getInt("id"));
                    cliente.setPuntiFedelta(resultSet.getInt("punti_fedelta"));
                    if (resultSet.getBoolean("segnalato")) {
                        cliente.segnala();
                    }

                    return cliente;
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Errore nella verifica delle credenziali del cliente.", e);
        }
        
        return null;
    }

    @Override
    public Cliente trovaClienteById(int clienteId) {

    	String sql = "SELECT id, nome, cognome, email, password, data_nascita, punti_fedelta, segnalato"
    	           + "FROM clienti"
    	           + "WHERE id = ?";

        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, clienteId);

            try (java.sql.ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                	return creaClienteDaRisultato(resultSet);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Errore nel recupero del cliente.", e);
        }
        
        return null;
    }

    @Override
    public java.util.List<Cliente> trovaTuttiIClienti() {

    	java.util.List<Cliente> clienti = new java.util.ArrayList<>();
    	
    	String sql = "SELECT id, nome, cognome, email, password, data_nascita, punti_fedelta, segnalato"
    	           + "FROM clienti";

        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql); java.sql.ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
            	clienti.add(creaClienteDaRisultato(resultSet));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Errore nel recupero dei clienti.", e);
        }
        
        return clienti;
    }
    
    @Override
    public int trovaPuntiByCliente(int clienteId) {
        String sql = "SELECT punti_fedelta"
        		   + "FROM clienti"
        		   + "WHERE id = ?";

        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, clienteId);

            try (java.sql.ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("punti_fedelta");
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Errore nel recupero dei punti del cliente.", e);
        }
        
        return 0;
    }
    
    @Override
    public void aggiungiPunti(int clienteId, int punti) {

        String sql = "UPDATE clienti"
        		   + "SET punti_fedelta = punti_fedelta + ?"
        		   + "WHERE id = ?";

        try (Connection connection = getConnection(); java.sql.PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, punti);
            statement.setInt(2, clienteId);

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Errore nell'aggiunta dei punti del cliente.", e);
        }
    }

    @Override
    public void scalaPunti(int clienteId, int punti) {

        String sql = "UPDATE clienti" 
                   + "SET punti_fedelta = punti_fedelta - ?" 
                   + "WHERE id = ? AND punti_fedelta >= ?";

        try (Connection connection = getConnection(); java.sql.PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, punti);
            statement.setInt(2, clienteId);
            statement.setInt(3, punti);

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Errore nella scalatura dei punti del cliente.", e);
        }
    }

    @Override
    public void salvaStorico(int clienteId, SessioneSpesa sessione) {

        if (sessione == null) {
            return;
        }

        String sql = "INSERT INTO storico_spese (cliente_id, data_spesa, totale)" 
                   + "VALUES (?, ?, ?)";

        try (Connection connection = getConnection(); java.sql.PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, clienteId);
            statement.setTimestamp(2, java.sql.Timestamp.valueOf(sessione.getDataOra()));
            statement.setDouble(3, sessione.getTotaleDaPagare());

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Errore nel salvataggio dello storico della spesa.", e);
        }
    }
    
    @Override
    public List<AcquistoStorico> trovaStoricoByCliente(int clienteId) {

        String sql = "SELECT data_spesa, totale"
                   + "FROM storico_spese" 
                   + "WHERE cliente_id = ?" 
                   + "ORDER BY data_spesa";

        List<AcquistoStorico> storico = new java.util.ArrayList<>();

        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, clienteId);

            try (java.sql.ResultSet resultSet = statement.executeQuery()) {

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

    @Override
    public void registraSegnalazione(Segnalazione segnalazione) {

        if (segnalazione == null || segnalazione.getSessione() == null || segnalazione.getSessione().getCliente() == null) {
            return;
        }

        int clienteId = segnalazione.getSessione().getCliente().getId();

        String sql = "INSERT INTO segnalazioni (cliente_id, data_segnalazione)"
                   + "VALUES (?, ?)";

        try (Connection connection = getConnection(); java.sql.PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, clienteId);
            statement.setTimestamp(2, java.sql.Timestamp.valueOf(segnalazione.getSessione().getDataOra()));

            statement.executeUpdate();
            
            String sqlAggiornaCliente = "UPDATE clienti"
            		                  + "SET segnalato = TRUE"
            		                  + "WHERE id = ?";

            try (PreparedStatement updateStatement = connection.prepareStatement(sqlAggiornaCliente)) {

                updateStatement.setInt(1, clienteId);
                updateStatement.executeUpdate();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Errore nel salvataggio della segnalazione.", e);
        }
    }
    
    @Override
    public boolean isClienteSegnalato(int clienteId) {

        String sql = "SELECT segnalato"
        		   + "FROM clienti"
        		   + "WHERE id = ?";

        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, clienteId);

            try (java.sql.ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return resultSet.getBoolean("segnalato");
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Errore nella verifica della segnalazione del cliente.", e);
        }
        
        return false;
    }
    
    private Cliente creaClienteDaRisultato(ResultSet resultSet) {

    	Cliente cliente = new Cliente(
                resultSet.getString("nome"),
                resultSet.getString("cognome"),
                resultSet.getString("email"),
                resultSet.getString("password"),
                resultSet.getDate("data_nascita").toLocalDate()
        );

        cliente.setId(resultSet.getInt("id"));
        cliente.setPuntiFedelta(resultSet.getInt("punti_fedelta"));
        if (resultSet.getBoolean("segnalato")) {
            cliente.segnala();
        }
        
        return cliente;
    }
}

