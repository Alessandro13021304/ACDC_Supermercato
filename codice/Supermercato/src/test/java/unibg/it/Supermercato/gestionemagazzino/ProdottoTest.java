package unibg.it.Supermercato.gestionemagazzino;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ProdottoTest {

	@Test
	void testCreazioneEGetter() {
        Prodotto prodotto = new Prodotto(
                "123456789",
                "Pasta",
                1.50,
                "Pasta di semola",
                10.0,
                8,
                20,
                1
        );

        assertEquals("123456789", prodotto.getCodiceABarre());
        assertEquals("Pasta", prodotto.getNome());
        assertEquals(1.50, prodotto.getPrezzo());
        assertEquals("Pasta di semola", prodotto.getDescrizione());
        assertEquals(10.0, prodotto.getSconto());
        assertEquals(8, prodotto.getQuantitaScaffale());
        assertEquals(20, prodotto.getQuantitaMagazzino());
    }
	
	@Test
    void testModificaQuantita() {
        Prodotto prodotto = new Prodotto(
                "123456789",
                "Pasta",
                1.50,
                "Pasta di semola",
                10.0,
                8,
                20,
                1
        );

        prodotto.setQuantitaScaffale(3);
        prodotto.setQuantitaMagazzino(15);

        assertEquals(3, prodotto.getQuantitaScaffale());
        assertEquals(15, prodotto.getQuantitaMagazzino());
    }
	
}
