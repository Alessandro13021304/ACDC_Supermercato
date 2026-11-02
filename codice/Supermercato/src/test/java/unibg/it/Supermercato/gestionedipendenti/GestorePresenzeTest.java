package unibg.it.Supermercato.gestionedipendenti;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import unibg.it.Supermercato.persistenzadati.DBClienti;
import unibg.it.Supermercato.persistenzadati.DBConnection;
import unibg.it.Supermercato.persistenzadati.DBDipendenti;
import unibg.it.Supermercato.sistemaaccesso.Cassiere;
import unibg.it.Supermercato.sistemaaccesso.Dipendente;
import unibg.it.Supermercato.sistemaaccesso.IControlloSpesa;
import unibg.it.Supermercato.sistemaaccesso.Turno;
import unibg.it.Supermercato.sistemaaccesso.AuthService;

class GestorePresenzeTest {

    @BeforeEach
    void inizializzaDatabaseDiTest() throws Exception {
        DBConnection.getInstance().resetTestDatabase("dipendenti");
    }

    @Test
    void testRegistraIngresso() {

        DBClienti repositoryClienti = new DBClienti();

        IControlloSpesa controlloSpesa = null;

        Dipendente cassiere = new Cassiere(
                "Mario",
                "Rossi",
                "mario.presenza@test.it",
                "password",
                LocalDate.of(1995, 5, 10),
                repositoryClienti,
                controlloSpesa
        );

        cassiere.setMatricola("C020");

        DBDipendenti repository = new DBDipendenti(true);
        repository.salvaDipendente(cassiere);

        AuthService autenticazione = new AuthService(repositoryClienti, repository);

        // Orari fissi: il turno deve iniziare e finire nello stesso giorno, a qualsiasi ora di esecuzione
        LocalTime oraInizio = LocalTime.of(8, 0);
        LocalTime oraFine = LocalTime.of(16, 0);

        Turno turno = new Turno(
                LocalDate.now(),
                oraInizio,
                oraFine
        );

        GestoreTurni gestoreTurni = new GestoreTurni(repository);
        gestoreTurni.assegnaTurno(cassiere, turno);

        GestorePresenze gestorePresenze = new GestorePresenze(repository, autenticazione);

        gestorePresenze.registraIngresso(cassiere);

        List<Turno> turni = repository.trovaTurniByMatricola("C020");

        assertNotNull(turni);
        assertEquals(1, turni.size());

        Turno turnoSalvato = turni.get(0);

        assertNotNull(turnoSalvato.getOraIngresso());
        assertTrue(turnoSalvato.presenzaIniziata());
        assertFalse(turnoSalvato.isCompletato());
    }

    @Test
    void testRegistraUscita() {

        DBClienti repositoryClienti = new DBClienti();

        IControlloSpesa controlloSpesa = null;

        Dipendente cassiere = new Cassiere(
                "Mario",
                "Rossi",
                "mario.uscita@test.it",
                "password",
                LocalDate.of(1995, 5, 10),
                repositoryClienti,
                controlloSpesa
        );

        cassiere.setMatricola("C021");

        DBDipendenti repository = new DBDipendenti(true);
        repository.salvaDipendente(cassiere);

        AuthService autenticazione = new AuthService(repositoryClienti, repository);

        LocalTime oraInizio = LocalTime.of(8, 0);
        LocalTime oraFine = LocalTime.of(16, 0);

        Turno turno = new Turno(
                LocalDate.now(),
                oraInizio,
                oraFine
        );

        GestoreTurni gestoreTurni = new GestoreTurni(repository);
        gestoreTurni.assegnaTurno(cassiere, turno);

        GestorePresenze gestorePresenze = new GestorePresenze(repository, autenticazione);

        gestorePresenze.registraIngresso(cassiere);

        gestorePresenze.registraUscita(cassiere);

        List<Turno> turni = repository.trovaTurniByMatricola("C021");

        assertNotNull(turni);
        assertEquals(1, turni.size());

        Turno turnoSalvato = turni.get(0);

        assertNotNull(turnoSalvato.getOraIngresso());
        assertNotNull(turnoSalvato.getOraUscita());
        assertTrue(turnoSalvato.presenzaIniziata());
        assertTrue(turnoSalvato.isCompletato());
    }

    @Test
    void testCalcolaOreLavorate() {

        DBClienti repositoryClienti = new DBClienti();

        IControlloSpesa controlloSpesa = null;

        Dipendente cassiere = new Cassiere(
                "Mario",
                "Rossi",
                "mario.ore@test.it",
                "password",
                LocalDate.of(1995, 5, 10),
                repositoryClienti,
                controlloSpesa
        );

        cassiere.setMatricola("C022");

        Turno turno = new Turno(
                LocalDate.now(),
                LocalTime.of(8, 0),
                LocalTime.of(16, 0)
        );

        turno.impostaOraIngresso(LocalTime.of(8, 0));
        turno.impostaOraUscita(LocalTime.of(16, 0));

        DBDipendenti repository = new DBDipendenti(true);

        cassiere.setRepositoryDipendenti(repository);

        repository.salvaDipendente(cassiere);
        repository.salvaTurno(cassiere.getMatricola(), turno);

        repository.aggiornaPresenza(
                cassiere.getMatricola(),
                turno.getData(),
                turno.getOrarioInizio(),
                LocalTime.of(8, 0),
                LocalTime.of(16, 0),
                true,
                true
        );

        AuthService autenticazione = new AuthService(repositoryClienti, repository);

        GestorePresenze gestorePresenze = new GestorePresenze(repository, autenticazione);

        double oreLavorate = gestorePresenze.calcolaOreLavorate(cassiere);

        assertEquals(8.0, oreLavorate, 0.01);
    }
}


