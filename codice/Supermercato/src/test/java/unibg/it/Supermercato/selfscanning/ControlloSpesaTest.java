package unibg.it.Supermercato.selfscanning;

import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import unibg.it.Supermercato.gestionemagazzino.Prodotto;
import unibg.it.Supermercato.sistemaaccesso.Cliente;
import unibg.it.Supermercato.sistemaaccesso.IAutenticazione;
import unibg.it.Supermercato.sistemaaccesso.Persona;

public class ControlloSpesaTest {
	
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
    void carrelliCorrispondenti() {

        Prodotto prodotto = new Prodotto(
                "123",
                "Pasta",
                2.50,
                "Pasta",
                0,
                10,
                20,
                1
        );

        Cliente cliente = new Cliente(
                "Mario",
                "Rossi",
                "mario@email.it",
                "password",
                LocalDate.of(2000, 1, 1)
        );

        SessioneSpesa sessione = new SessioneSpesa(cliente, autenticazione);

        sessione.scansionaProdotto(prodotto);
        sessione.scansionaProdotto(prodotto);

        CarrelloFisico carrelloFisico = new CarrelloFisico();

        ElementoCarrello elementoFisico = new ElementoCarrello(prodotto, 2, 2.50);

        carrelloFisico.aggiungiElemento(elementoFisico);

        ControlloSpesa controllo = new ControlloSpesa();

        assertTrue(controllo.verificaCorrispondenza(sessione, carrelloFisico));
    }
    
    @Test
    void quantitaDiverse() {

        Prodotto prodotto = new Prodotto(
                "123",
                "Pasta",
                2.50,
                "Pasta",
                0,
                10,
                20,
                1
        );

        Cliente cliente = new Cliente(
                "Mario",
                "Rossi",
                "mario@email.it",
                "password",
                LocalDate.of(2000, 1, 1)
        );

        SessioneSpesa sessione = new SessioneSpesa(cliente, autenticazione);

        sessione.scansionaProdotto(prodotto);
        sessione.scansionaProdotto(prodotto);

        CarrelloFisico carrelloFisico = new CarrelloFisico();

        ElementoCarrello elementoFisico = new ElementoCarrello(prodotto, 1, 2.50);

        carrelloFisico.aggiungiElemento(elementoFisico);

        ControlloSpesa controllo = new ControlloSpesa();

        assertFalse(controllo.verificaCorrispondenza(sessione, carrelloFisico));
    }
    
    @Test
    void prodottiDiversi() {

        Prodotto prodottoVirtuale = new Prodotto(
                "123",
                "Pasta",
                2.50,
                "Pasta",
                0,
                10,
                20,
                1
        );

        Prodotto prodottoFisico = new Prodotto(
                "456",
                "Latte",
                1.50,
                "Latte",
                0,
                10,
                20,
                1
        );

        Cliente cliente = new Cliente(
                "Mario",
                "Rossi",
                "mario@email.it",
                "password",
                LocalDate.of(2000, 1, 1)
        );

        SessioneSpesa sessione = new SessioneSpesa(cliente, autenticazione);

        sessione.scansionaProdotto(prodottoVirtuale);

        CarrelloFisico carrelloFisico = new CarrelloFisico();

        ElementoCarrello elementoFisico = new ElementoCarrello(prodottoFisico, 1, 1.50);

        carrelloFisico.aggiungiElemento(elementoFisico);

        ControlloSpesa controllo = new ControlloSpesa();

        assertFalse(controllo.verificaCorrispondenza(sessione, carrelloFisico));
    }
    
    @Test
    void prodottoMancante() {

        Prodotto pasta = new Prodotto(
                "123",
                "Pasta",
                2.50,
                "Pasta",
                0,
                10,
                20,
                1
        );

        Prodotto latte = new Prodotto(
                "456",
                "Latte",
                1.50,
                "Latte",
                0,
                10,
                20,
                1
        );

        Cliente cliente = new Cliente(
                "Mario",
                "Rossi",
                "mario@email.it",
                "password",
                LocalDate.of(2000, 1, 1)
        );

        SessioneSpesa sessione = new SessioneSpesa(cliente, autenticazione);

        sessione.scansionaProdotto(pasta);
        sessione.scansionaProdotto(latte);

        CarrelloFisico carrelloFisico = new CarrelloFisico();

        ElementoCarrello elementoFisico = new ElementoCarrello(pasta, 1, 2.50);

        carrelloFisico.aggiungiElemento(elementoFisico);

        ControlloSpesa controllo = new ControlloSpesa();

        assertFalse(controllo.verificaCorrispondenza(sessione, carrelloFisico));
    }
    
    @Test
    void prodottoAggiuntivo() {

        Prodotto pasta = new Prodotto(
                "123",
                "Pasta",
                2.50,
                "Pasta",
                0,
                10,
                20,
                1
        );

        Prodotto latte = new Prodotto(
                "456",
                "Latte",
                1.50,
                "Latte",
                0,
                10,
                20,
                1
        );

        Cliente cliente = new Cliente(
                "Mario",
                "Rossi",
                "mario@email.it",
                "password",
                LocalDate.of(2000, 1, 1)
        );

        SessioneSpesa sessione = new SessioneSpesa(cliente, autenticazione);

        sessione.scansionaProdotto(pasta);

        CarrelloFisico carrelloFisico = new CarrelloFisico();

        ElementoCarrello pastaFisica = new ElementoCarrello(pasta, 1, 2.50);

        ElementoCarrello latteFisico = new ElementoCarrello(latte, 1, 1.50);

        carrelloFisico.aggiungiElemento(pastaFisica);
        carrelloFisico.aggiungiElemento(latteFisico);

        ControlloSpesa controllo = new ControlloSpesa();

        assertFalse(controllo.verificaCorrispondenza(sessione, carrelloFisico));
    }
    
    @Test
    void sessioneNull() {

        CarrelloFisico carrelloFisico = new CarrelloFisico();

        ControlloSpesa controllo = new ControlloSpesa();

        assertFalse(controllo.verificaCorrispondenza(null, carrelloFisico));
    }
    
    @Test
    void carrelloFisicoNull() {

    	Cliente cliente = new Cliente(
                "Mario",
                "Rossi",
                "mario@email.it",
                "password",
                LocalDate.of(2000, 1, 1)
        );

        SessioneSpesa sessione = new SessioneSpesa(cliente, autenticazione);

        ControlloSpesa controllo = new ControlloSpesa();

        assertFalse(controllo.verificaCorrispondenza(sessione, null));
    }
}
