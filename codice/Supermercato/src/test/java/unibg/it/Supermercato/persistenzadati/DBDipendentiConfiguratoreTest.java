package unibg.it.Supermercato.persistenzadati;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import unibg.it.Supermercato.sistemaaccesso.*;

class DBDipendentiConfiguratoreTest {

    @BeforeEach
    void inizializzaDatabaseDiTest() throws Exception {
        DBConnection.getInstance().resetTestDatabase("dipendenti");
    }

    @Test
    void iRuoliCaricatiDalDatabaseRicevonoICollaboratoriDelConfiguratore() {
        DBDipendenti repository = new DBDipendenti(true);

        Manager manager = new Manager("Luca", "Bianchi", "luca@test.it", "password", LocalDate.of(1985, 3, 15), repository);
        manager.setMatricola("M001");

        Cassiere cassiere = new Cassiere("Mario", "Rossi", "mario@test.it", "password", LocalDate.of(1995, 5, 10), null, null);
        cassiere.setMatricola("C001");

        repository.salvaDipendente(manager);
        repository.salvaDipendente(cassiere);

        ITurni gestoreTurni = mock(ITurni.class);
        IControlloSpesa controlloSpesa = mock(IControlloSpesa.class);

        repository.setConfiguratoreRuoli(dipendente -> {
            if (dipendente instanceof Manager m) {
                m.setGestoreTurni(gestoreTurni);
            } else if (dipendente instanceof Cassiere c) {
                c.setControlloSpesa(controlloSpesa);
            }
        });

        Manager managerCaricato = (Manager) repository.trovaDipendenteByMatricola("M001");
        Turno turno = new Turno(LocalDate.of(2026, 11, 2), LocalTime.of(8, 0), LocalTime.of(16, 0));

        managerCaricato.assegnaTurno(managerCaricato, turno);

        verify(gestoreTurni).assegnaTurno(managerCaricato, turno);

        Cassiere cassiereCaricato = (Cassiere) repository.trovaDipendenteByMatricola("C001");
        ISessioneControllabile sessione = mock(ISessioneControllabile.class);
        ICarrelloFisico carrelloFisico = mock(ICarrelloFisico.class);

        when(sessione.isInControllo()).thenReturn(true);
        when(controlloSpesa.verificaCorrispondenza(sessione, carrelloFisico)).thenReturn(true);

        assertTrue(cassiereCaricato.effettuaControllo(sessione, carrelloFisico));
        verify(sessione).completaDopoControllo();
    }

    @Test
    void ilConfiguratoreVieneInvocatoPerOgniRuolo() {
        DBDipendenti repository = new DBDipendenti(true);

        Manager manager = new Manager("Luca", "Bianchi", "luca@test.it", "password", LocalDate.of(1985, 3, 15), repository);
        manager.setMatricola("M001");

        Cassiere cassiere = new Cassiere("Mario", "Rossi", "mario@test.it", "password", LocalDate.of(1995, 5, 10), null, null);
        cassiere.setMatricola("C001");

        Magazziniere magazziniere = new Magazziniere("Anna", "Verdi", "anna@test.it", "password", LocalDate.of(1990, 7, 20));
        magazziniere.setMatricola("MAG001");

        ResponsabileDiReparto responsabile = new ResponsabileDiReparto("Paolo", "Neri", "paolo@test.it", "password", LocalDate.of(1988, 11, 5));
        responsabile.setMatricola("R001");

        repository.salvaDipendente(manager);
        repository.salvaDipendente(cassiere);
        repository.salvaDipendente(magazziniere);
        repository.salvaDipendente(responsabile);

        List<String> ruoliConfigurati = new ArrayList<>();
        repository.setConfiguratoreRuoli(dipendente -> ruoliConfigurati.add(dipendente.getRuolo()));

        repository.trovaTuttiDipendenti();

        assertEquals(4, ruoliConfigurati.size());
        assertTrue(ruoliConfigurati.containsAll(List.of("Manager", "Cassiere", "Magazziniere", "ResponsabileDiReparto")));
    }
}

