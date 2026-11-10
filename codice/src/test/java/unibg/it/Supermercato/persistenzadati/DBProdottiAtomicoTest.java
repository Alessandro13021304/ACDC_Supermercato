package unibg.it.Supermercato.persistenzadati;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import unibg.it.Supermercato.gestionemagazzino.Prodotto;

class DBProdottiAtomicoTest {

    private DBProdotti repository;

    @BeforeEach
    void preparaDatabase() throws Exception {
        DBConnection.getInstance().resetTestDatabase("prodotti");

        repository = new DBProdotti(true);
        repository.salvaProdotto(new Prodotto("123", "Pasta", 1.50, "Pasta", 0.0, 10, 20, 1));
    }

    private Prodotto leggi() {
        return repository.trovaProdottoByCodiceABarre("123");
    }

    @Test
    void decrementaRiduceLaQuantitaAScaffale() {
        assertTrue(repository.decrementaQuantitaScaffale("123", 3));

        assertEquals(7, leggi().getQuantitaScaffale());
        assertEquals(20, leggi().getQuantitaMagazzino());
    }

    @Test
    void decrementaNonPortaLaQuantitaSottoZero() {
        assertFalse(repository.decrementaQuantitaScaffale("123", 11));

        assertEquals(10, leggi().getQuantitaScaffale());
    }

    @Test
    void decrementaRifiutaCodiciInesistentiEQuantitaNonPositive() {
        assertFalse(repository.decrementaQuantitaScaffale("999", 1));
        assertFalse(repository.decrementaQuantitaScaffale(null, 1));
        assertFalse(repository.decrementaQuantitaScaffale("123", 0));
        assertFalse(repository.decrementaQuantitaScaffale("123", -2));

        assertEquals(10, leggi().getQuantitaScaffale());
    }

    @Test
    void duePiuAggiornamentiIndipendentiSiSommanoSenzaSovrascriversi() {
        DBProdotti altroRepository = new DBProdotti(true);

        assertTrue(repository.decrementaQuantitaScaffale("123", 3));
        assertTrue(altroRepository.decrementaQuantitaScaffale("123", 4));

        assertEquals(3, leggi().getQuantitaScaffale());
    }

    @Test
    void spostaLaMerceDalMagazzinoAlloScaffale() {
        assertTrue(repository.spostaDalMagazzinoAlloScaffale("123", 5));

        assertEquals(15, leggi().getQuantitaScaffale());
        assertEquals(15, leggi().getQuantitaMagazzino());
    }

    @Test
    void nonSpostaPiuMerceDiQuellaPresenteInMagazzino() {
        assertFalse(repository.spostaDalMagazzinoAlloScaffale("123", 21));

        assertEquals(10, leggi().getQuantitaScaffale());
        assertEquals(20, leggi().getQuantitaMagazzino());
    }

    @Test
    void spostaRifiutaCodiciInesistentiEQuantitaNonPositive() {
        assertFalse(repository.spostaDalMagazzinoAlloScaffale("999", 1));
        assertFalse(repository.spostaDalMagazzinoAlloScaffale(null, 1));
        assertFalse(repository.spostaDalMagazzinoAlloScaffale("123", 0));

        assertEquals(10, leggi().getQuantitaScaffale());
        assertEquals(20, leggi().getQuantitaMagazzino());
    }
}
