package unibg.it.Supermercato.persistenzadati;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class DBConnection {
	
	private static final DBConnection instance = new DBConnection();

	private DBConnection() {
	}

	public static DBConnection getInstance() {
	    return instance;
	}

    private static final Properties properties = new Properties();

    static {
        try (InputStream input = DBConnection.class.getClassLoader().getResourceAsStream("database.properties")) {

            if (input == null) {
                throw new IOException("File database.properties non trovato.");
            }

            properties.load(input);

        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public Connection getConnection(String database) throws SQLException {

        String url = properties.getProperty("db." + database + ".url");

        if (url == null) {
            throw new IllegalArgumentException("Database non configurato: " + database);
        }

        Connection connection = DriverManager.getConnection(url,properties.getProperty("db.user"), properties.getProperty("db.password"));

        inizializzaDatabase(connection, database);

        return connection;
    }

    private static void inizializzaDatabase(Connection connection, String database) throws SQLException {

    	if ("dipendenti".equals(database)) {

    	    try (InputStream input = DBConnection.class.getClassLoader().getResourceAsStream("init-dipendenti.sql")) {

    	        if (input == null) {
    	            throw new SQLException("File init-dipendenti.sql non trovato.");
    	        }

    	        String sql = new String(input.readAllBytes());

    	        try (Statement statement = connection.createStatement()) {
    	            statement.execute(sql);
    	        }

    	    } catch (IOException e) {
    	        throw new SQLException("Errore nella lettura di init-dipendenti.sql.", e);
    	    }

    	} else if ("prodotti".equals(database)) {

    	    try (InputStream input = DBConnection.class.getClassLoader().getResourceAsStream("init-prodotti.sql")) {

    	        if (input == null) {
    	            throw new SQLException("File init-prodotti.sql non trovato.");
    	        }

    	        String sql = new String(input.readAllBytes());

    	        try (Statement statement = connection.createStatement()) {
    	            statement.execute(sql);
    	        }

    	    } catch (IOException e) {
    	        throw new SQLException("Errore nella lettura di init-prodotti.sql.", e);
    	    }
    	    
    	} else if ("clienti".equals(database)) {

    	    try (InputStream input = DBConnection.class.getClassLoader().getResourceAsStream("init-clienti.sql")) {

    	        if (input == null) {
    	            throw new SQLException("File init-clienti.sql non trovato.");
    	        }

    	        String sql = new String(input.readAllBytes());

    	        try (Statement statement = connection.createStatement()) {
    	            statement.execute(sql);
    	        }

    	    } catch (IOException e) {
    	        throw new SQLException("Errore nella lettura di init-clienti.sql.", e);
    	    }
    	}
    }
    
    public Connection getTestConnection(String database) throws SQLException {

        Properties testProperties = new Properties();

        try (InputStream input = DBConnection.class.getClassLoader().getResourceAsStream("database-test.properties")) {

            if (input == null) {
                throw new SQLException("File database-test.properties non trovato.");
            }

            testProperties.load(input);

        } catch (IOException e) {
            throw new SQLException("Errore nella lettura di database-test.properties.", e);
        }

        String url = testProperties.getProperty("db." + database + ".url");

        if (url == null) {
            throw new IllegalArgumentException("Database di test non configurato: " + database);
        }

        Connection connection = DriverManager.getConnection(url, testProperties.getProperty("db.user"), testProperties.getProperty("db.password"));

        inizializzaDatabase(connection, database);

        return connection;
    }
    
    public void resetTestDatabase(String database) throws SQLException {

        Properties testProperties = new Properties();

        try (InputStream input = DBConnection.class.getClassLoader().getResourceAsStream("database-test.properties")) {

            if (input == null) {
                throw new SQLException("File database-test.properties non trovato.");
            }

            testProperties.load(input);

        } catch (IOException e) {
            throw new SQLException("Errore nella lettura di database-test.properties.", e);
        }

        String url = testProperties.getProperty("db." + database + ".url");

        if (url == null) {
            throw new IllegalArgumentException("Database di test non configurato: " + database);
        }

        try (Connection connection = DriverManager.getConnection(url, testProperties.getProperty("db.user"), testProperties.getProperty("db.password")); Statement statement = connection.createStatement()) {

            if ("clienti".equals(database)) {

                statement.executeUpdate("DROP TABLE IF EXISTS segnalazioni");
                statement.executeUpdate("DROP TABLE IF EXISTS storico_spese");
                statement.executeUpdate("DROP TABLE IF EXISTS clienti");

            } else if ("dipendenti".equals(database)) {

                statement.executeUpdate("DROP TABLE IF EXISTS turni");
                statement.executeUpdate("DROP TABLE IF EXISTS dipendenti");

            } else if ("prodotti".equals(database)) {

                statement.executeUpdate("DROP TABLE IF EXISTS righe_ordine");
                statement.executeUpdate("DROP TABLE IF EXISTS ordini_fornitore");
                statement.executeUpdate("DROP TABLE IF EXISTS prodotti");

            } else {

                statement.executeUpdate("DROP TABLE IF EXISTS " + database);

            }

            inizializzaDatabase(connection, database);
        }
    }
}