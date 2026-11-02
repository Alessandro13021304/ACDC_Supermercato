package unibg.it.Supermercato.gestionemagazzino;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

class ProdottoUguaglianzaTest {

    private Prodotto crea(String codice, String nome) {
        return new Prodotto(codice, nome, 2.50, "Descrizione", 0.0, 10, 20, 1);
    }

    @Test
    void prodottiConLoStessoCodiceABarreSonoUguali() {
        Prodotto a = crea("123", "Pasta");
        Prodotto b = crea("123", "Pasta integrale");

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void prodottiConCodiciDiversiNonSonoUguali() {
        assertNotEquals(crea("123", "Pasta"), crea("456", "Pasta"));
    }

    @Test
    void ilConfrontoConNullOConUnAltroTipoRestituisceFalse() {
        Prodotto prodotto = crea("123", "Pasta");

        assertFalse(prodotto.equals(null));
        assertFalse(prodotto.equals("123"));
    }

    @Test
    void unaMappaTrovaIlProdottoUsandoUnaCopiaConLoStessoCodice() {
        Map<Prodotto, Integer> quantita = new HashMap<>();
        quantita.put(crea("123", "Pasta"), 3);

        assertEquals(3, quantita.get(crea("123", "Pasta")));
    }
}

