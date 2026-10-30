package unibg.it.Supermercato.gestionedipendenti;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.jupiter.api.Test;

import unibg.it.Supermercato.sistemaaccesso.Presenza;
import unibg.it.Supermercato.sistemaaccesso.Turno;

public class PresenzaTest {

    @Test
    void calcolaSeiOreLavorate() {
        Turno turno = new Turno(
                LocalDate.now(),
                LocalTime.of(8, 0),
                LocalTime.of(14, 0)
        );

        turno.impostaOraIngresso(LocalTime.of(8, 0));
        turno.impostaOraUscita(LocalTime.of(14, 0));

        Presenza presenza = new Presenza(turno);

        assertEquals(6.0, presenza.calcolaOreLavorate(), 0.01);
    }

    @Test
    void restituisceZeroSeTurnoNonCompletato() {
        Turno turno = new Turno(
                LocalDate.now(),
                LocalTime.of(8, 0),
                LocalTime.of(14, 0)
        );

        turno.impostaOraIngresso(LocalTime.of(8, 0));

        Presenza presenza = new Presenza(turno);

        assertEquals(0.0, presenza.calcolaOreLavorate(), 0.01);
    }

    @Test
    void restituisceZeroConTurnoNullo() {
        Presenza presenza = new Presenza(null);

        assertEquals(0.0, presenza.calcolaOreLavorate(), 0.01);
    }
}

