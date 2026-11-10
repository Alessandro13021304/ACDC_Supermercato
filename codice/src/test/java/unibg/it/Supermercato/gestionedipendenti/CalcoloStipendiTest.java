package unibg.it.Supermercato.gestionedipendenti;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import unibg.it.Supermercato.sistemaaccesso.Dipendente;
import unibg.it.Supermercato.sistemaaccesso.Magazziniere;
import unibg.it.Supermercato.sistemaaccesso.Stipendio;

class CalcoloStipendiTest {

    @Test
    void calcolaStipendioConDipendenteValido() {

        Dipendente dipendente = new Magazziniere("Anna", "Verdi", "anna.verdi@test.it", "password", LocalDate.of(1990, 7, 20));

        CalcoloStipendi calcoloStipendi = new CalcoloStipendi();

        Stipendio risultato = calcoloStipendi.calcolaStipendio(dipendente);

        assertNotNull(risultato);
    }

    @Test
    void calcolaStipendioConDipendenteNullo() {

        CalcoloStipendi calcoloStipendi = new CalcoloStipendi();

        Stipendio risultato = calcoloStipendi.calcolaStipendio(null);

        assertNull(risultato);
    }

    @Test
    void calcolaStipendioMensileConDipendenteValido() {

        Dipendente dipendente = new Magazziniere("Anna", "Verdi", "anna.mensile@test.it", "password", LocalDate.of(1990, 7, 20));

        CalcoloStipendi calcoloStipendi = new CalcoloStipendi();

        Stipendio risultato = calcoloStipendi.calcolaStipendioMensile(dipendente, 2026, 10);

        assertNotNull(risultato);
    }

    @Test
    void calcolaStipendioMensileConDipendenteNullo() {

        CalcoloStipendi calcoloStipendi = new CalcoloStipendi();

        Stipendio risultato = calcoloStipendi.calcolaStipendioMensile(null, 2026, 10);

        assertNull(risultato);
    }
}