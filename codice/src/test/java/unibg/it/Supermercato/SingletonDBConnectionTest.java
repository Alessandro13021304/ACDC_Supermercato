package unibg.it.Supermercato;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import unibg.it.Supermercato.persistenzadati.DBConnection;

class SingletonDBConnectionTest {

	@Test
	void getInstanceRestituisceSempreLaStessaIstanza() {

	    DBConnection connessione1 = DBConnection.getInstance();

	    DBConnection connessione2 = DBConnection.getInstance();

	    assertNotNull(connessione1);
	    assertNotNull(connessione2);

	    assertSame(connessione1, connessione2);
	}
}
