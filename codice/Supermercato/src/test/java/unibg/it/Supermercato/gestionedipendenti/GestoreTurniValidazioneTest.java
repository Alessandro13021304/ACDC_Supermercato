package unibg.it.Supermercato.gestionedipendenti;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import unibg.it.Supermercato.sistemaaccesso.IRepositoryDipendenti;
import unibg.it.Supermercato.sistemaaccesso.Magazziniere;
import unibg.it.Supermercato.sistemaaccesso.Manager;
import unibg.it.Supermercato.sistemaaccesso.Turno;

class GestoreTurniValidazioneTest {

    private IRepositoryDipendenti repository;
    private GestoreTurni gestoreTurni;
    private Magazziniere dipendente;

    private Turno turno(int oraInizio, int oraFine) {
        return new Turno(LocalDate.of(2026, 11, 10), LocalTime.of(oraInizio, 0), LocalTime.of(oraFine, 0));
    }

    @BeforeEach
    void preparaDati() {
        repository = mock(IRepositoryDipendenti.class);
        gestoreTurni = new GestoreTurni(repository);

        dipendente = new Magazziniere(
                "Mario", "Rossi", "mario@email.it", "password", LocalDate.of(1990, 1, 1));
        dipendente.setMatricola("M001");

        when(repository.trovaTurniByMatricola("M001")).thenReturn(new ArrayList<>());
    }

    @Test
    void unTurnoValidoVieneAssegnatoESalvato() {
        Turno nuovo = turno(8, 16);

        assertTrue(gestoreTurni.assegnaTurno(dipendente, nuovo));

        verify(repository).salvaTurno("M001", nuovo);
    }

    @Test
    void unTurnoSovrappostoVieneRifiutatoENonSalvato() {
        when(repository.trovaTurniByMatricola("M001")).thenReturn(List.of(turno(8, 14)));

        assertFalse(gestoreTurni.assegnaTurno(dipendente, turno(10, 16)));

        verify(repository, never()).salvaTurno(anyString(), any(Turno.class));
    }

    @Test
    void iTurniConsecutiviVengonoAccettati() {
        Turno consecutivo = turno(14, 20);
        when(repository.trovaTurniByMatricola("M001")).thenReturn(List.of(turno(8, 14)));

        assertTrue(gestoreTurni.assegnaTurno(dipendente, consecutivo));

        verify(repository).salvaTurno("M001", consecutivo);
    }

    @Test
    void unTurnoConInizioDopoLaFineVieneRifiutato() {
        assertFalse(gestoreTurni.assegnaTurno(dipendente, turno(16, 8)));

        verify(repository, never()).salvaTurno(anyString(), any(Turno.class));
    }

    @Test
    void unTurnoConInizioUgualeAllaFineVieneRifiutato() {
        assertFalse(gestoreTurni.assegnaTurno(dipendente, turno(8, 8)));

        verify(repository, never()).salvaTurno(anyString(), any(Turno.class));
    }

    @Test
    void argomentiNulliVengonoRifiutati() {
        assertFalse(gestoreTurni.assegnaTurno(null, turno(8, 16)));
        assertFalse(gestoreTurni.assegnaTurno(dipendente, null));

        verify(repository, never()).salvaTurno(anyString(), any(Turno.class));
    }

    @Test
    void ilManagerRestituisceLEsitoDelGestoreTurni() {
        Manager manager = new Manager(
                "Luca", "Bianchi", "luca@test.it", "password", LocalDate.of(1985, 3, 10), repository);
        manager.setGestoreTurni(gestoreTurni);

        assertTrue(manager.assegnaTurno(dipendente, turno(8, 16)));

        when(repository.trovaTurniByMatricola("M001")).thenReturn(List.of(turno(8, 14)));

        assertFalse(manager.assegnaTurno(dipendente, turno(10, 12)));
    }

    @Test
    void unManagerSenzaGestoreTurniNonAssegnaNessunTurno() {
        Manager manager = new Manager(
                "Luca", "Bianchi", "luca@test.it", "password", LocalDate.of(1985, 3, 10), repository);

        assertFalse(manager.assegnaTurno(dipendente, turno(8, 16)));

        verify(repository, never()).salvaTurno(anyString(), any(Turno.class));
    }
}
