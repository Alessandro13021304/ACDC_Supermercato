package unibg.it.Supermercato.gestionedipendenti;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.jupiter.api.Test;

import unibg.it.Supermercato.sistemaaccesso.Turno;

class TurnoOrarioTest {

    private static final LocalDate DATA = LocalDate.of(2026, 11, 10);

    private Turno turno(LocalTime inizio, LocalTime fine) {
        return new Turno(DATA, inizio, fine);
    }

    @Test
    void orarioEValidoSeLInizioPrecedeLaFine() {
        assertTrue(turno(LocalTime.of(8, 0), LocalTime.of(16, 0)).isOrarioValido());
    }

    @Test
    void orarioNonEValidoSeLInizioNonPrecedeLaFine() {
        assertFalse(turno(LocalTime.of(16, 0), LocalTime.of(8, 0)).isOrarioValido());
        assertFalse(turno(LocalTime.of(8, 0), LocalTime.of(8, 0)).isOrarioValido());
    }

    @Test
    void orarioNonEValidoConValoriNulli() {
        assertFalse(turno(null, LocalTime.of(16, 0)).isOrarioValido());
        assertFalse(turno(LocalTime.of(8, 0), null).isOrarioValido());
    }

    @Test
    void registraInizioConOraEsplicitaImpostaLIngresso() {
        Turno turno = turno(LocalTime.of(8, 0), LocalTime.of(16, 0));

        turno.registraInizio(LocalTime.of(8, 5));

        assertTrue(turno.presenzaIniziata());
        assertEquals(LocalTime.of(8, 5), turno.getOraIngresso());
        assertFalse(turno.isCompletato());
    }

    @Test
    void registraFineConOraEsplicitaCompletaIlTurno() {
        Turno turno = turno(LocalTime.of(8, 0), LocalTime.of(16, 0));

        turno.registraInizio(LocalTime.of(8, 5));
        turno.registraFine(LocalTime.of(15, 55));

        assertTrue(turno.isCompletato());
        assertEquals(LocalTime.of(15, 55), turno.getOraUscita());
    }

    @Test
    void registraFineSenzaInizioNonCompletaIlTurno() {
        Turno turno = turno(LocalTime.of(8, 0), LocalTime.of(16, 0));

        turno.registraFine(LocalTime.of(15, 55));

        assertFalse(turno.isCompletato());
        assertNull(turno.getOraUscita());
    }

    @Test
    void unaOraNullaNonModificaIlTurno() {
        Turno turno = turno(LocalTime.of(8, 0), LocalTime.of(16, 0));

        turno.registraInizio(null);

        assertFalse(turno.presenzaIniziata());

        turno.registraInizio(LocalTime.of(8, 5));
        turno.registraFine(null);

        assertFalse(turno.isCompletato());
    }
}

