package unibg.it.Supermercato.selfscanning;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class CarrelloTest {

	@Test
	void test() {
		fail("Not yet implemented");
	}

}
package unibg.it.Supermercato.selfscanning;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import unibg.it.Supermercato.gestionemagazzino.Prodotto;

class CarrelloTest {

    @Test
    void calcolaTotaleConUnElemento() {
        Prodotto prodotto = new Prodotto(
                "123456",
                "Pasta",
                2.50,
                "Pasta di semola",
                0.0,
                20,
                100,
                1
        );

        ElementoCarrello elemento = new ElementoCarrello(
                prodotto,
                3,
                2.50
        );

        Carrello carrello = new Carrello();
        carrello.aggiungiElemento(elemento);

        assertEquals(7.50, carrello.calcolaTotale(), 0.001);
    }

    @Test
    void calcolaTotaleConPiuElementi() {
        Prodotto pasta = new Prodotto(
                "123456",
                "Pasta",
                2.50,
                "Pasta di semola",
                0.0,
                20,
                100,
                1
        );

        Prodotto latte = new Prodotto(
                "654321",
                "Latte",
                1.50,
                "Latte intero",
                0.0,
                15,
                50,
                1
        );

        ElementoCarrello elementoPasta = new ElementoCarrello(
                pasta,
                3,
                2.50
        );

        ElementoCarrello elementoLatte = new ElementoCarrello(
                latte,
                2,
                1.50
        );

        Carrello carrello = new Carrello();

        carrello.aggiungiElemento(elementoPasta);
        carrello.aggiungiElemento(elementoLatte);

        assertEquals(10.50, carrello.calcolaTotale(), 0.001);
    }

    @Test
    void rimuoveElementoCorrettamente() {
        Prodotto prodotto = new Prodotto(
                "123456",
                "Pasta",
                2.50,
                "Pasta di semola",
                0.0,
                20,
                100,
                1
        );

        ElementoCarrello elemento = new ElementoCarrello(
                prodotto,
                3,
                2.50
        );

        Carrello carrello = new Carrello();

        carrello.aggiungiElemento(elemento);
        carrello.rimuoviElemento(elemento);

        assertEquals(0.0, carrello.calcolaTotale(), 0.001);
    }
}