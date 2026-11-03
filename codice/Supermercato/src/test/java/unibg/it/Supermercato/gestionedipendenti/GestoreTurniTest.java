package unibg.it.Supermercato.gestionedipendenti;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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

class GestoreTurniTest {

    @BeforeEach
    void inizializzaDatabaseDiTest() throws Exception {
        DBConnection.getInstance().resetTestDatabase("dipendenti");
    }

    @Test
    void testAssegnaEVisualizzaTurno() {

        DBClienti repositoryClienti = new DBClienti();
        
        IControlloSpesa controlloSpesa = null;

        Dipendente cassiere = new Cassiere("Mario", "Rossi", "mario.gestoreturni@test.it", "password", LocalDate.of(1995, 5, 10), repositoryClienti, controlloSpesa);

        cassiere.setMatricola("C012");

        DBDipendenti repository = new DBDipendenti(true);

        repository.salvaDipendente(cassiere);

        GestoreTurni gestoreTurni = new GestoreTurni(repository);

        Turno turno = new Turno(LocalDate.of(2026, 10, 6), LocalTime.of(8, 0), LocalTime.of(16, 0));

        gestoreTurni.assegnaTurno(cassiere, turno);

        List<Turno> turni = gestoreTurni.visualizzaTurni(cassiere);

        assertNotNull(turni);
        assertEquals(1, turni.size());

        Turno risultato = turni.get(0);

        assertEquals(LocalDate.of(2026, 10, 6), risultato.getData());
        assertEquals(LocalTime.of(8, 0), risultato.getOrarioInizio());
        assertEquals(LocalTime.of(16, 0), risultato.getOrarioFine());
    }
}