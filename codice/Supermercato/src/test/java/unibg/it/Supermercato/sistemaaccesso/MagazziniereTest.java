package unibg.it.Supermercato.sistemaaccesso;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.BeforeEach;

import unibg.it.Supermercato.persistenzadati.DBConnection;
import unibg.it.Supermercato.persistenzadati.DBProdotti;
import unibg.it.Supermercato.gestionemagazzino.Prodotto;
import unibg.it.Supermercato.gestionemagazzino.GestoreScorte;
import unibg.it.Supermercato.gestionemagazzino.IScorteObserver;
import unibg.it.Supermercato.gestionemagazzino.TipoScorta;
import unibg.it.Supermercato.gestionemagazzino.GestoreProdotti;

public class MagazziniereTest {
	
    @BeforeEach
    void inizializzaDatabaseDiTest() throws Exception {
        DBConnection.getInstance().resetTestDatabase("prodotti");
    }

    @Test
    void rifornisciScaffaleSpostaProdottiDalMagazzino() {
        Magazziniere magazziniere = new Magazziniere(
                "Mario",
                "Rossi",
                "mario@email.it",
                "password",
                LocalDate.of(1990, 1, 1)
        );

        Prodotto prodotto = new Prodotto(
                "123",
                "Pasta",
                2.50,
                "Pasta",
                0,
                10,
                50,
                1
        );

        magazziniere.rifornisciScaffale(prodotto, 20);

        assertEquals(30, prodotto.getQuantitaScaffale());
        assertEquals(30, prodotto.getQuantitaMagazzino());
    }

    @Test
    void rifornisciScaffaleNonPermetteDiSuperareLaScortaMagazzino() {
        Magazziniere magazziniere = new Magazziniere(
                "Mario",
                "Rossi",
                "mario@email.it",
                "password",
                LocalDate.of(1990, 1, 1)
        );

        Prodotto prodotto = new Prodotto(
                "123",
                "Pasta",
                2.50,
                "Pasta",
                0,
                10,
                5,
                1
        );

        magazziniere.rifornisciScaffale(prodotto, 10);

        assertEquals(10, prodotto.getQuantitaScaffale());
        assertEquals(5, prodotto.getQuantitaMagazzino());
    }

    @Test
    void rifornisciScaffaleIgnoraQuantitaNonValida() {
        Magazziniere magazziniere = new Magazziniere(
                "Mario",
                "Rossi",
                "mario@email.it",
                "password",
                LocalDate.of(1990, 1, 1)
        );

        Prodotto prodotto = new Prodotto(
                "123",
                "Pasta",
                2.50,
                "Pasta",
                0,
                10,
                50,
                1
        );

        magazziniere.rifornisciScaffale(prodotto, 0);

        assertEquals(10, prodotto.getQuantitaScaffale());
        assertEquals(50, prodotto.getQuantitaMagazzino());
    }

    @Test
    void rifornisciScaffaleIgnoraProdottoNullo() {
        Magazziniere magazziniere = new Magazziniere(
                "Mario",
                "Rossi",
                "mario@email.it",
                "password",
                LocalDate.of(1990, 1, 1)
        );

        assertDoesNotThrow(() -> magazziniere.rifornisciScaffale(null, 10));
    }
    
    @Test
    void segnalaScorteInsufficientiQuandoSottoSoglia() {
        Magazziniere magazziniere = new Magazziniere(
                "Mario",
                "Rossi",
                "mario@email.it",
                "password",
                LocalDate.of(1990, 1, 1)
        );

        Prodotto prodotto = new Prodotto(
                "123",
                "Pasta",
                2.50,
                "Pasta",
                0,
                3,
                2,
                1
        );

        assertTrue(magazziniere.segnalaScorteInsufficientiMagazzino(prodotto));
    }

    @Test
    void nonSegnalaScorteQuandoSopraSoglia() {
        Magazziniere magazziniere = new Magazziniere(
                "Mario",
                "Rossi",
                "mario@email.it",
                "password",
                LocalDate.of(1990, 1, 1)
        );

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

        assertFalse(magazziniere.segnalaScorteInsufficientiMagazzino(prodotto));
    }

    @Test
    void nonSegnalaScorteQuandoEsattamenteAllaSoglia() {
        Magazziniere magazziniere = new Magazziniere(
                "Mario",
                "Rossi",
                "mario@email.it",
                "password",
                LocalDate.of(1990, 1, 1)
        );

        Prodotto prodotto = new Prodotto(
                "123",
                "Pasta",
                2.50,
                "Pasta",
                0,
                5,
                20,
                1
        );

        assertFalse(magazziniere.segnalaScorteInsufficientiMagazzino(prodotto));
    }

    @Test
    void nonSegnalaScorteConProdottoNullo() {
        Magazziniere magazziniere = new Magazziniere(
                "Mario",
                "Rossi",
                "mario@email.it",
                "password",
                LocalDate.of(1990, 1, 1)
        );

        assertFalse(magazziniere.segnalaScorteInsufficientiMagazzino(null));
    }
    
    @Test
    void testVisualizzaProdottoDaCatalogo() {

        DBProdotti db = new DBProdotti(true);

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

        db.salvaProdotto(prodotto);

        GestoreProdotti gestoreProdotti = new GestoreProdotti(db);

        Magazziniere magazziniere = new Magazziniere(
                "Mario",
                "Rossi",
                "mario.rossi@email.it",
                "password",
                LocalDate.of(1990, 1, 1)
        );

        magazziniere.setGestoreProdotti(gestoreProdotti);

        Prodotto risultato =
                magazziniere.visualizzaProdotto("123456789");

        assertNotNull(risultato);
        assertEquals("123456789", risultato.getCodiceABarre());
        assertEquals("Pasta", risultato.getNome());
    }
    
    @Test
    void aggiornaRifornisceScaffaleConTuttaLaDisponibilitaQuandoInferioreA20() {

        Magazziniere magazziniere = new Magazziniere(
                "Mario",
                "Rossi",
                "mario@email.it",
                "password",
                LocalDate.of(1990, 1, 1)
        );

        GestoreScorte gestore = new GestoreScorte();

        final TipoScorta[] tipoNotificato = {null};

        IScorteObserver observer = (prodotto, tipoScorta) ->
                tipoNotificato[0] = tipoScorta;

        gestore.aggiungiObserver(observer);
        magazziniere.setGestoreScorte(gestore);

        Prodotto prodotto = new Prodotto(
                "123",
                "Pasta",
                2.50,
                "Pasta",
                0,
                3,
                12,
                1
        );

        magazziniere.aggiorna(prodotto, TipoScorta.SCAFFALE);

        assertEquals(15, prodotto.getQuantitaScaffale());
        assertEquals(0, prodotto.getQuantitaMagazzino());
        assertEquals(TipoScorta.MAGAZZINO, tipoNotificato[0]);
    }
}
