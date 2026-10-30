package unibg.it.Supermercato.selfscanning;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import unibg.it.Supermercato.sistemaaccesso.Cliente;
import unibg.it.Supermercato.sistemaaccesso.IAutenticazione;
import unibg.it.Supermercato.sistemaaccesso.Persona;

class GeneratoreRicevutaTest {
	
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

    @Test
    void generaRicevutaContieneLeInformazioniDelPagamento() {

    	Cliente cliente = new Cliente(
    	        "Mario",
    	        "Rossi",
    	        "mario@email.it",
    	        "password",
    	        LocalDate.of(2000, 1, 1)
    	);

    	SessioneSpesa sessione = new SessioneSpesa(cliente, autenticazione);

        Pagamento pagamento = new Pagamento(
                8.00,
                EModalitaPagamento.CARTA_DI_CREDITO
        );

        GeneratoreRicevuta generatore = new GeneratoreRicevuta();

        String ricevuta = generatore.genera(sessione, pagamento);

        assertNotNull(ricevuta);
        assertTrue(ricevuta.contains("RICEVUTA"));
        assertTrue(ricevuta.contains( String.format("%.2f", pagamento.getImporto())));
        assertTrue(ricevuta.contains("CARTA_DI_CREDITO"));
    }
    
    @Test
    void generaRicevutaConParametriNullRestituisceStringaVuota() {

        GeneratoreRicevuta generatore = new GeneratoreRicevuta();

        assertEquals("", generatore.genera(null, null));
    }
}
