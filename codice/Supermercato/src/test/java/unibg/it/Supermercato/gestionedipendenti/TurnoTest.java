package unibg.it.Supermercato.gestionedipendenti;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.jupiter.api.Test;

import unibg.it.Supermercato.sistemaaccesso.Turno;

public class TurnoTest {

    @Test
    void registraInizioRegistraLaPresenza() {
        Turno turno = new Turno(LocalDate.now(), LocalTime.of(8, 0), LocalTime.of(14, 0));

        turno.registraInizio();

        assertTrue(turno.presenzaIniziata());
        assertFalse(turno.isCompletato());
    }

    @Test
    void registraFineCompletaIlTurno() {
        Turno turno = new Turno(LocalDate.now(), LocalTime.of(8, 0), LocalTime.of(14, 0));

        turno.registraInizio();
        turno.registraFine();

        assertTrue(turno.presenzaIniziata());
        assertTrue(turno.isCompletato());
    }

    @Test
    void registraFineSenzaInizioNonCompletaIlTurno() {
        Turno turno = new Turno(LocalDate.now(), LocalTime.of(8, 0), LocalTime.of(14, 0));

        turno.registraFine();

        assertFalse(turno.presenzaIniziata());
        assertFalse(turno.isCompletato());
    }

    @Test
    void turnoAppartieneAllaDataCorretta() {
        LocalDate oggi = LocalDate.now();

        Turno turno = new Turno(oggi, LocalTime.of(8, 0), LocalTime.of(14, 0));

        assertTrue(turno.appartieneAllaData(oggi));
        assertFalse(turno.appartieneAllaData(oggi.plusDays(1)));
    }
}