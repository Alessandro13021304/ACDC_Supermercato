package unibg.it.Supermercato.persistenzadati;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.sql.Connection;

import org.junit.jupiter.api.Test;

public class DBConnectionTest {

    @Test
    void testConnessioneDipendenti() throws Exception {
    	Connection connection = DBConnection.getInstance().getConnection("dipendenti");

        assertNotNull(connection);

        connection.close();
    }
}
