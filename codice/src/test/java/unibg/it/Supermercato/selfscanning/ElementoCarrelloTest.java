package unibg.it.Supermercato.selfscanning;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import unibg.it.Supermercato.gestionemagazzino.Prodotto;

class ElementoCarrelloTest {

    @Test
    void calcolaTotaleRigaCorrettamente() {
        Prodotto prodotto = new Prodotto("123456", "Pasta", 2.50, "Pasta di semola", 0.0, 20, 100, 1);

        ElementoCarrello elemento = new ElementoCarrello(prodotto, 3, 2.50);

        double risultato = elemento.calcolaTotaleRiga();

        assertEquals(7.50, risultato, 0.001);
    }
}
