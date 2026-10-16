package unibg.it.Supermercato.selfscanning;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import unibg.it.Supermercato.gestionemagazzino.Prodotto;
import unibg.it.Supermercato.sistemaaccesso.Persona;
import unibg.it.Supermercato.sistemaaccesso.Cliente;
import unibg.it.Supermercato.sistemaaccesso.IAutenticazione;

class LettoreOtticoTest {

	@Test
	void scansionaProdottoAggiungeIlProdottoAlCarrello() {
	    IAutenticazione autenticazione = new IAutenticazione() {

	        @Override
	        public boolean registraUtente(Persona persona) {
	            return false;
	        }

	        @Override
	        public Persona autentica(String email, String password) {
	            return null;
	        }
	    };

	    ICarrello carrello = new Carrello();

	    LettoreOttico lettore = new LettoreOttico(autenticazione, carrello);

	    Prodotto prodotto = new Prodotto(
	            "123456",
	            "Latte",
	            2.00,
	            "Latte intero",
	            0.0,
	            10,
	            20,
                1
	    );

	    lettore.scansionaProdotto(prodotto);

	    assertNotNull(carrello.trovaElemento(prodotto));
	}
	
	@Test
	void autenticaClienteConCredenzialiCorrette() {
		IAutenticazione autenticazione = new IAutenticazione() {

		    @Override
		    public boolean registraUtente(Persona persona) {
		        return false;
		    }

		    @Override
		    public Persona autentica(String email, String password) {
		        if ("mario@email.it".equals(email) && "password123".equals(password)) {
		            return new Cliente(
		                    "Mario",
		                    "Rossi",
		                    email,
		                    password,
		                    java.time.LocalDate.of(2000, 5, 10)
		            );
		        }
		        return null;
		    }
		};

	    ICarrello carrello = new Carrello();

	    LettoreOttico lettore = new LettoreOttico(autenticazione, carrello);

	    Cliente risultato = lettore.autentica(
	            "mario@email.it",
	            "password123"
	    );

	    assertNotNull(risultato);
	    assertEquals("mario@email.it", risultato.getEmail());
	}

}
