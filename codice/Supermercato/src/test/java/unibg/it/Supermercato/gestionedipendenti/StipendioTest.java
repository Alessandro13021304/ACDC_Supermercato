package unibg.it.Supermercato.gestionedipendenti;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.jupiter.api.Test;

import unibg.it.Supermercato.sistemaaccesso.Cassiere;
import unibg.it.Supermercato.persistenzadati.IRepositoryClienti;
import unibg.it.Supermercato.selfscanning.IControlloSpesa;
import unibg.it.Supermercato.persistenzadati.DBDipendenti;

public class StipendioTest {

    @Test
    void calcolaStipendioMensileDiSettembre() {

        IRepositoryClienti repository = null;
        
        IControlloSpesa controlloSpesa = null;

        Cassiere cassiere = new Cassiere(
                "Mario",
                "Rossi",
                "mario@email.it",
                "password",
                LocalDate.of(1990, 1, 1),
                repository,
                controlloSpesa
        );

        Stipendio stipendio = new Stipendio(cassiere);

        Turno turno1 = new Turno(
                LocalDate.of(2026, 9, 1),
                LocalTime.of(8, 0),
                LocalTime.of(14, 0)
        );
        turno1.impostaOraIngresso(LocalTime.of(8, 0));
        turno1.impostaOraUscita(LocalTime.of(14, 0));

        Turno turno2 = new Turno(
                LocalDate.of(2026, 9, 15),
                LocalTime.of(8, 0),
                LocalTime.of(16, 0)
        );
        turno2.impostaOraIngresso(LocalTime.of(8, 0));
        turno2.impostaOraUscita(LocalTime.of(16, 0));

        Turno turnoOttobre = new Turno(
                LocalDate.of(2026, 10, 3),
                LocalTime.of(8, 0),
                LocalTime.of(13, 0)
        );
        turnoOttobre.impostaOraIngresso(LocalTime.of(8, 0));
        turnoOttobre.impostaOraUscita(LocalTime.of(13, 0));

        stipendio.aggiungiPresenza(new Presenza(turno1));
        stipendio.aggiungiPresenza(new Presenza(turno2));
        stipendio.aggiungiPresenza(new Presenza(turnoOttobre));

        assertEquals(140.0, stipendio.calcolaStipendioMensile(2026, 9), 0.01);
    }

    @Test
    void calcolaStipendioMensileDiOttobre() {

        IRepositoryClienti repository = null;
        
        IControlloSpesa controlloSpesa = null;

        Cassiere cassiere = new Cassiere(
                "Mario",
                "Rossi",
                "mario@email.it",
                "password",
                LocalDate.of(1990, 1, 1),
                repository,
                controlloSpesa
        );

        Stipendio stipendio = new Stipendio(cassiere);

        Turno turnoSettembre = new Turno(
                LocalDate.of(2026, 9, 15),
                LocalTime.of(8, 0),
                LocalTime.of(14, 0)
        );
        turnoSettembre.impostaOraIngresso(LocalTime.of(8, 0));
        turnoSettembre.impostaOraUscita(LocalTime.of(14, 0));

        Turno turnoOttobre = new Turno(
                LocalDate.of(2026, 10, 3),
                LocalTime.of(8, 0),
                LocalTime.of(13, 0)
        );
        turnoOttobre.impostaOraIngresso(LocalTime.of(8, 0));
        turnoOttobre.impostaOraUscita(LocalTime.of(13, 0));

        stipendio.aggiungiPresenza(new Presenza(turnoSettembre));
        stipendio.aggiungiPresenza(new Presenza(turnoOttobre));

        assertEquals(50.0, stipendio.calcolaStipendioMensile(2026, 10), 0.01);
    }

    @Test
    void nessunaPresenzaNelMeseRestituisceZero() {

        IRepositoryClienti repository = null;
        
        IControlloSpesa controlloSpesa = null;

        Cassiere cassiere = new Cassiere(
                "Mario",
                "Rossi",
                "mario@email.it",
                "password",
                LocalDate.of(1990, 1, 1),
                repository,
                controlloSpesa
        );

        Stipendio stipendio = new Stipendio(cassiere);

        assertEquals(0.0, stipendio.calcolaStipendioMensile(2026, 9), 0.01);
    }
    
    @Test
    void aggiungeSoloLePresenzeDeiTurniCompletati() {

        IRepositoryClienti repository = null;
        
        IControlloSpesa controlloSpesa = null;

        Cassiere cassiere = new Cassiere(
                "Mario",
                "Rossi",
                "mario@email.it",
                "password",
                LocalDate.of(1990, 1, 1),
                repository,
                controlloSpesa
        );
        
        cassiere.setMatricola("C023");
        
        Stipendio stipendio = new Stipendio(cassiere);

        Turno turnoCompletato = new Turno(
                LocalDate.of(2026, 9, 10),
                LocalTime.of(8, 0),
                LocalTime.of(14, 0)
        );
        
        DBDipendenti repositoryd = new DBDipendenti(true);
        cassiere.setRepositoryDipendenti(repositoryd);
        
        repositoryd.salvaDipendente(cassiere);
        repositoryd.salvaTurno(cassiere.getMatricola(), turnoCompletato);
        
        repositoryd.aggiornaPresenza(
                cassiere.getMatricola(),
                turnoCompletato.getData(),
                turnoCompletato.getOrarioInizio(),
                LocalTime.of(8, 0),
                LocalTime.of(14, 0),
                true,
                true
        );

        Turno turnoNonCompletato = new Turno(
                LocalDate.of(2026, 9, 15),
                LocalTime.of(8, 0),
                LocalTime.of(13, 0)
        );
        
        repositoryd.salvaTurno(cassiere.getMatricola(), turnoNonCompletato);

        stipendio.aggiungiPresenzeDaTurni(cassiere);

        assertEquals(60.0, stipendio.calcolaStipendioMensile(2026, 9), 0.01);
    }
}

