package unibg.it.Supermercato.sistemaaccesso;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.jupiter.api.Test;

import unibg.it.Supermercato.gestionedipendenti.GestoreTurni;
import unibg.it.Supermercato.persistenzadati.DBConnection;
import unibg.it.Supermercato.persistenzadati.DBDipendenti;

public class DipendenteTest {

    @Test
    void managerAssegnaTurnoAlDipendente() throws Exception {
    
        DBConnection.getInstance().resetTestDatabase("dipendenti");
    
        DBDipendenti repository = new DBDipendenti(true);
    
        Manager manager = new Manager("Mario", "Rossi", "mario@email.it", "password", LocalDate.of(1990, 1, 1), repository);

        manager.setGestoreTurni(new GestoreTurni(repository));

        Manager dipendente = new Manager("Luigi", "Bianchi", "luigi@email.it", "password", LocalDate.of(1992, 5, 10), repository);
        
        dipendente.setMatricola("M002");

        repository.salvaDipendente(dipendente);

        Turno turno = new Turno(LocalDate.now(), LocalTime.of(8, 0), LocalTime.of(14, 0));

        manager.assegnaTurno(dipendente, turno);

        assertEquals(1, dipendente.visualizzaTurni().size());
        assertEquals(turno.getData(), dipendente.visualizzaTurni().get(0).getData());
    }
}