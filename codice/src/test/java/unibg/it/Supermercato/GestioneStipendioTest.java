package unibg.it.Supermercato;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

import java.time.LocalTime;
import java.util.List;

import unibg.it.Supermercato.gestionedipendenti.CalcoloStipendi;
import unibg.it.Supermercato.sistemaaccesso.*;

class GestioneStipendioTest {

    @Test
    void calcolaStipendioMensileConsiderandoSoloITurniDelMese() {

        Dipendente dipendente = new Magazziniere("Anna", "Verdi", "anna.stipendio@test.it", "password", LocalDate.of(1990, 7, 20));

        dipendente.setMatricola("M001");

        Turno turnoOttobre = new Turno(LocalDate.of(2026, 10, 5), LocalTime.of(8, 0), LocalTime.of(16, 0));

        turnoOttobre.impostaOraIngresso(LocalTime.of(8, 0));
        turnoOttobre.impostaOraUscita(LocalTime.of(16, 0));

        Turno turnoSettembre = new Turno(LocalDate.of(2026, 9, 20), LocalTime.of(8, 0), LocalTime.of(16, 0));

        turnoSettembre.impostaOraIngresso(LocalTime.of(8, 0));
        turnoSettembre.impostaOraUscita(LocalTime.of(16, 0));

        IRepositoryDipendenti repository = mock(IRepositoryDipendenti.class);

        when(repository.trovaTurniByMatricola("M001")).thenReturn(List.of(turnoOttobre, turnoSettembre));

        dipendente.setRepositoryDipendenti(repository);

        CalcoloStipendi calcoloStipendi = new CalcoloStipendi();

        Stipendio risultato = calcoloStipendi.calcolaStipendioMensile(dipendente, 2026, 10);

        assertNotNull(risultato);
        assertEquals(88.0, risultato.getImporto(), 0.01);

        verify(repository).trovaTurniByMatricola("M001");
    }

    @Test
    void calcolaStipendioMensileSenzaTurniNelMeseRestituisceZero() {

    	Dipendente dipendente = new Magazziniere("Anna", "Verdi", "anna.stipendio@test.it", "password", LocalDate.of(1990, 7, 20));

        dipendente.setMatricola("M002");

        Turno turnoSettembre = new Turno(LocalDate.of(2026, 9, 20), LocalTime.of(8, 0), LocalTime.of(16, 0));

        turnoSettembre.impostaOraIngresso(LocalTime.of(8, 0));
        turnoSettembre.impostaOraUscita(LocalTime.of(16, 0));

        IRepositoryDipendenti repository = mock(IRepositoryDipendenti.class);

        when(repository.trovaTurniByMatricola("M002")).thenReturn(List.of(turnoSettembre));

        dipendente.setRepositoryDipendenti(repository);

        CalcoloStipendi calcoloStipendi = new CalcoloStipendi();

        Stipendio risultato = calcoloStipendi.calcolaStipendioMensile(dipendente, 2026, 10);

        assertNotNull(risultato);
        assertEquals(0.0, risultato.getImporto(), 0.01);

        verify(repository).trovaTurniByMatricola("M002");
    }

    @Test
    void managerCalcolaGliStipendiDeiDipendenti() {

        IRepositoryDipendenti repository = mock(IRepositoryDipendenti.class);

        Manager manager = new Manager("Luca", "Bianchi", "luca.manager@test.it", "password", LocalDate.of(1985, 3, 10), repository);

        manager.setCalcoloStipendi(new CalcoloStipendi());

        Dipendente dipendente = new Magazziniere("Anna", "Verdi", "anna.stipendio@test.it", "password", LocalDate.of(1990, 7, 20));

        dipendente.setMatricola("M003");
        dipendente.setRepositoryDipendenti(repository);

        Turno turno = new Turno(LocalDate.of(2026, 10, 5), LocalTime.of(8, 0), LocalTime.of(16, 0));

        turno.impostaOraIngresso(LocalTime.of(8, 0));
        turno.impostaOraUscita(LocalTime.of(16, 0));

        when(repository.trovaTurniByMatricola("M003")).thenReturn(List.of(turno));

        List<Dipendente> dipendenti = List.of(dipendente);

        List<Stipendio> stipendi = manager.calcolaStipendi(dipendenti, 2026, 10);

        assertNotNull(stipendi);
        assertEquals(1, stipendi.size());
        assertNotNull(stipendi.get(0));

        assertEquals(88.0, stipendi.get(0).getImporto(), 0.01);

        verify(repository).trovaTurniByMatricola("M003");
    }
}