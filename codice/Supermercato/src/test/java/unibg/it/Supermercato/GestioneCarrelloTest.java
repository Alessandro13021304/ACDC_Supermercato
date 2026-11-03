package unibg.it.Supermercato;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import unibg.it.Supermercato.gestionemagazzino.Prodotto;
import unibg.it.Supermercato.selfscanning.Carrello;
import unibg.it.Supermercato.selfscanning.ElementoCarrello;

class GestioneCarrelloTest {

    @Test
    void aggiungeProdottoAlCarrello() {

        Prodotto prodotto = new Prodotto("123456", "Pasta", 2.50, "Pasta di semola", 0.0, 10, 50, 1);

        Carrello carrello = new Carrello();

        carrello.aggiungiProdotto(prodotto);

        ElementoCarrello elemento = carrello.trovaElemento(prodotto);

        assertNotNull(elemento);
        assertEquals(1, elemento.getQuantita());
        assertEquals(2.50, elemento.calcolaTotaleRiga(), 0.001);
    }
    
    @Test
    void aumentaQuantitaAggiungendoLoStessoProdotto() {

    	Prodotto prodotto = new Prodotto("123456", "Pasta", 2.50, "Pasta di semola", 0.0, 10, 50, 1);

        Carrello carrello = new Carrello();

        carrello.aggiungiProdotto(prodotto);
        carrello.aggiungiProdotto(prodotto);

        ElementoCarrello elemento = carrello.trovaElemento(prodotto);

        assertNotNull(elemento);
        assertEquals(5.00, elemento.calcolaTotaleRiga(), 0.001);
    }
    
    @Test
    void rimuoveProdottoDalCarrello() {

    	Prodotto prodotto = new Prodotto("123456", "Pasta", 2.50, "Pasta di semola", 0.0, 10, 50, 1);

        Carrello carrello = new Carrello();

        carrello.aggiungiProdotto(prodotto);

        assertNotNull(carrello.trovaElemento(prodotto));

        ElementoCarrello elemento = carrello.trovaElemento(prodotto);

        carrello.rimuoviElemento(elemento);

        assertNull(carrello.trovaElemento(prodotto));
        assertEquals(0.0, carrello.calcolaTotale(), 0.001);
    }
    
    @Test
    void nonSuperaDisponibilitaScaffale() {

    	Prodotto prodotto = new Prodotto("123456", "Pasta", 2.50, "Pasta di semola", 0.0, 2, 50, 1);

        Carrello carrello = new Carrello();

        carrello.aggiungiProdotto(prodotto);
        carrello.aggiungiProdotto(prodotto);
        carrello.aggiungiProdotto(prodotto); 

        ElementoCarrello elemento = carrello.trovaElemento(prodotto);

        assertNotNull(elemento);
        assertEquals(5.00, elemento.calcolaTotaleRiga(), 0.001);
    }
    
    @Test
    void nonAggiungeProdottoNonDisponibile() {

    	Prodotto prodotto = new Prodotto("123456", "Pasta", 2.50, "Pasta di semola", 0.0, 0, 50, 1);

        Carrello carrello = new Carrello();

        carrello.aggiungiProdotto(prodotto);

        assertNull(carrello.trovaElemento(prodotto));
        assertEquals(0.0, carrello.calcolaTotale(), 0.001);
    }
    
    @Test
    void calcolaTotaleDelCarrello() {

    	Prodotto pasta = new Prodotto("123456", "Pasta", 2.50, "Pasta di semola", 0.0, 10, 50, 1);

        Prodotto latte = new Prodotto("654321", "Latte", 1.50, "Latte intero", 0.0, 10, 50, 1);

        Carrello carrello = new Carrello();

        carrello.aggiungiProdotto(pasta);
        carrello.aggiungiProdotto(pasta);
        carrello.aggiungiProdotto(latte);

        assertEquals(6.50, carrello.calcolaTotale(), 0.001);
    }
}
