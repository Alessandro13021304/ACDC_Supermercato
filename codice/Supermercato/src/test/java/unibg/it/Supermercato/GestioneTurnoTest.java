package unibg.it.Supermercato;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import unibg.it.Supermercato.gestionedipendenti.GestoreTurni;
import unibg.it.Supermercato.sistemaaccesso.*;

class GestioneTurnoTest {

    @Test
    void assegnaTurnoEDipendenteVisualizzaIlTurno() {

        IRepositoryDipendenti repositoryDipendenti =
                mock(IRepositoryDipendenti.class);

        GestoreTurni gestoreTurni =
                new GestoreTurni(repositoryDipendenti);

        Magazziniere dipendente = new Magazziniere(
                "Mario",
                "Rossi",
                "mario@email.it",
                "password",
                LocalDate.of(1990, 1, 1)
        );

        dipendente.setMatricola("M001");

        Turno turno = new Turno(
                LocalDate.of(2026, 10, 10),
                LocalTime.of(8, 0),
                LocalTime.of(14, 0)
        );

        gestoreTurni.assegnaTurno(dipendente, turno);

        verify(repositoryDipendenti)
                .salvaTurno("M001", turno);

        when(repositoryDipendenti.trovaTurniByMatricola("M001"))
                .thenReturn(List.of(turno));

        List<Turno> turni =
                gestoreTurni.visualizzaTurni(dipendente);

        assertNotNull(turni);
        assertEquals(1, turni.size());
        assertEquals(turno, turni.get(0));
        assertEquals(
                LocalDate.of(2026, 10, 10),
                turni.get(0).getData()
        );
        assertEquals(
                LocalTime.of(8, 0),
                turni.get(0).getOrarioInizio()
        );
        assertEquals(
                LocalTime.of(14, 0),
                turni.get(0).getOrarioFine()
        );
    }
    
    @Test
    void dipendenteVisualizzaPiuTurni() {

        IRepositoryDipendenti repositoryDipendenti =
                mock(IRepositoryDipendenti.class);

        GestoreTurni gestoreTurni =
                new GestoreTurni(repositoryDipendenti);

        Magazziniere dipendente = new Magazziniere(
                "Mario",
                "Rossi",
                "mario@email.it",
                "password",
                LocalDate.of(1990, 1, 1)
        );

        dipendente.setMatricola("M001");

        Turno turnoMattina = new Turno(
                LocalDate.of(2026, 10, 10),
                LocalTime.of(8, 0),
                LocalTime.of(14, 0)
        );

        Turno turnoPomeriggio = new Turno(
                LocalDate.of(2026, 10, 11),
                LocalTime.of(14, 0),
                LocalTime.of(20, 0)
        );

        gestoreTurni.assegnaTurno(dipendente, turnoMattina);
        gestoreTurni.assegnaTurno(dipendente, turnoPomeriggio);

        verify(repositoryDipendenti)
                .salvaTurno("M001", turnoMattina);

        verify(repositoryDipendenti)
                .salvaTurno("M001", turnoPomeriggio);

        when(repositoryDipendenti.trovaTurniByMatricola("M001"))
                .thenReturn(List.of(turnoMattina, turnoPomeriggio));

        List<Turno> turni =
                gestoreTurni.visualizzaTurni(dipendente);

        assertNotNull(turni);
        assertEquals(2, turni.size());

        assertEquals(turnoMattina, turni.get(0));
        assertEquals(turnoPomeriggio, turni.get(1));

        assertEquals(
                LocalDate.of(2026, 10, 10),
                turni.get(0).getData()
        );

        assertEquals(
                LocalDate.of(2026, 10, 11),
                turni.get(1).getData()
        );
    }
    
    @Test
    void dipendenteNullVisualizzaListaVuota() {

        IRepositoryDipendenti repositoryDipendenti =
                mock(IRepositoryDipendenti.class);

        GestoreTurni gestoreTurni =
                new GestoreTurni(repositoryDipendenti);

        List<Turno> turni =
                gestoreTurni.visualizzaTurni(null);

        assertNotNull(turni);
        assertTrue(turni.isEmpty());

        verifyNoInteractions(repositoryDipendenti);
    }
    
    @Test
    void nonAssegnaTurnoSovrapposto() {

        IRepositoryDipendenti repositoryDipendenti =
                mock(IRepositoryDipendenti.class);

        GestoreTurni gestoreTurni =
                new GestoreTurni(repositoryDipendenti);

        Magazziniere dipendente = new Magazziniere(
                "Mario",
                "Rossi",
                "mario@email.it",
                "password",
                LocalDate.of(1990, 1, 1)
        );

        dipendente.setMatricola("M001");

        Turno turnoEsistente = new Turno(
                LocalDate.of(2026, 10, 10),
                LocalTime.of(8, 0),
                LocalTime.of(14, 0)
        );

        Turno turnoSovrapposto = new Turno(
                LocalDate.of(2026, 10, 10),
                LocalTime.of(10, 0),
                LocalTime.of(16, 0)
        );

        when(repositoryDipendenti.trovaTurniByMatricola("M001"))
                .thenReturn(List.of(turnoEsistente));

        gestoreTurni.assegnaTurno(
                dipendente,
                turnoSovrapposto
        );

        verify(repositoryDipendenti, never())
                .salvaTurno("M001", turnoSovrapposto);
    }
    
    @Test
    void managerAssegnaUnTurnoAlDipendente() {

        IRepositoryDipendenti repository =
                mock(IRepositoryDipendenti.class);

        Manager manager = new Manager(
                "Luca",
                "Bianchi",
                "luca.manager@test.it",
                "password",
                LocalDate.of(1985, 3, 10),
                repository
        );

        manager.setGestoreTurni(new GestoreTurni(repository));

        Magazziniere dipendente = new Magazziniere(
                "Anna",
                "Verdi",
                "anna.turno@test.it",
                "password",
                LocalDate.of(1990, 7, 20)
        );

        dipendente.setMatricola("M004");

        when(repository.trovaTurniByMatricola("M004"))
                .thenReturn(new ArrayList<>());

        Turno turno = new Turno(
                LocalDate.of(2026, 10, 10),
                LocalTime.of(8, 0),
                LocalTime.of(16, 0)
        );

        manager.assegnaTurno(dipendente, turno);

        verify(repository).salvaTurno(
                "M004",
                turno
        );
    }
}