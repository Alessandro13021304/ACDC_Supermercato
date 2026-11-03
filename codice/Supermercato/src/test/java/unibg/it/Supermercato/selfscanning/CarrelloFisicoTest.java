package unibg.it.Supermercato.selfscanning;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import unibg.it.Supermercato.gestionemagazzino.Prodotto;
import unibg.it.Supermercato.sistemaaccesso.Cliente;
import unibg.it.Supermercato.sistemaaccesso.IAutenticazione;

class CarrelloFisicoTest {

    private Prodotto crea(String codice) {
        return new Prodotto(codice, "Pasta", 2.50, "Pasta", 0.0, 10, 20, 1);
    }

    @Test
    void riconosceUnaCopiaDelloStessoProdottoConLaStessaQuantita() {
        CarrelloFisico carrelloFisico = new CarrelloFisico();
        carrelloFisico.aggiungiElemento(new ElementoCarrello(crea("123"), 2, 2.50));

        assertTrue(carrelloFisico.contieneElemento(crea("123"), 2));
    }

    @Test
    void nonRiconosceLoStessoProdottoConUnaQuantitaDiversa() {
        CarrelloFisico carrelloFisico = new CarrelloFisico();
        carrelloFisico.aggiungiElemento(new ElementoCarrello(crea("123"), 2, 2.50));

        assertFalse(carrelloFisico.contieneElemento(crea("123"), 1));
    }

    @Test
    void nonRiconosceUnProdottoConUnAltroCodiceABarre() {
        CarrelloFisico carrelloFisico = new CarrelloFisico();
        carrelloFisico.aggiungiElemento(new ElementoCarrello(crea("123"), 2, 2.50));

        assertFalse(carrelloFisico.contieneElemento(crea("456"), 2));
        assertFalse(carrelloFisico.contieneElemento(null, 2));
    }

    @Test
    void ilControlloDellaSpesaUsaIlCodiceABarreENonIlRiferimento() {
        Cliente cliente = new Cliente("Mario", "Rossi", "mario@email.it", "password", LocalDate.of(2000, 1, 1));

        SessioneSpesa sessione = new SessioneSpesa(cliente, mock(IAutenticazione.class));
        sessione.scansionaProdotto(crea("123"));

        CarrelloFisico carrelloFisico = new CarrelloFisico();
        carrelloFisico.aggiungiElemento(new ElementoCarrello(crea("123"), 1, 2.50));

        assertTrue(new ControlloSpesa().verificaCorrispondenza(sessione, carrelloFisico));
    }
}

