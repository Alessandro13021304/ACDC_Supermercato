package unibg.it.Supermercato.persistenzadati;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;

import java.util.List;

import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.jupiter.api.Test;

import unibg.it.Supermercato.sistemaaccesso.*;


class DBDipendentiTest {
	
	@BeforeEach
	void inizializzaDatabaseDiTest() throws Exception {
	    DBConnection.getInstance().resetTestDatabase("dipendenti");
	}

    @Test
    void testSalvaDipendente() {

        DBClienti repositoryClienti = new DBClienti();
        
        IControlloSpesa controlloSpesa = null;

        Cassiere cassiere = new Cassiere("Mario", "Rossi", "mario.rossi4@test.it", "password", LocalDate.of(1995, 5, 10), repositoryClienti, controlloSpesa);

        cassiere.setMatricola("C004");

        DBDipendenti repository = new DBDipendenti(true);

        assertDoesNotThrow(() -> repository.salvaDipendente(cassiere));
    }
    
    @Test
    void testVerificaCredenziali() {

        DBClienti repositoryClienti = new DBClienti();
        
        IControlloSpesa controlloSpesa = null;

        Cassiere cassiere = new Cassiere("Mario", "Rossi", "mario.login@test.it", "password", LocalDate.of(1995, 5, 10), repositoryClienti, controlloSpesa);

        cassiere.setMatricola("C007");

        DBDipendenti repository = new DBDipendenti(true);
        repository.salvaDipendente(cassiere);

        Dipendente risultato = repository.verificaCredenziali("mario.login@test.it", "password");

        assertNotNull(risultato);
        assertEquals("C007", risultato.getMatricola());
        assertEquals("Mario", risultato.getNome());
        assertEquals("Rossi", risultato.getCognome());
        assertEquals("mario.login@test.it", risultato.getEmail());
        assertEquals("Cassiere", risultato.getRuolo());
    }
    
    @Test
    void testVerificaCredenzialiErrate() {

        DBClienti repositoryClienti = new DBClienti();
        
        IControlloSpesa controlloSpesa = null;

        Cassiere cassiere = new Cassiere("Mario", "Rossi", "mario.rossi8@test.it", "password", LocalDate.of(1995, 5, 10), repositoryClienti, controlloSpesa);

        cassiere.setMatricola("C008");

        DBDipendenti repository = new DBDipendenti(true);
        repository.salvaDipendente(cassiere);

        Dipendente risultato = repository.verificaCredenziali("mario.login2@test.it", "passwordErrata");

        assertTrue(risultato == null);
    }
    
    @Test
    void testVerificaCredenzialiEmailInesistente() {

        DBDipendenti repository = new DBDipendenti(true);

        Dipendente risultato = repository.verificaCredenziali("email.inesistente@test.it", "password");

        assertTrue(risultato == null);
    }
    
    @Test
    void testTrovaDipendenteByMatricola() {

        DBClienti repositoryClienti = new DBClienti();
        
        IControlloSpesa controlloSpesa = null;

        Cassiere cassiere = new Cassiere("Mario", "Rossi", "mario.rossi6@test.it", "password", LocalDate.of(1995, 5, 10), repositoryClienti, controlloSpesa);

        cassiere.setMatricola("C006");

        DBDipendenti repository = new DBDipendenti(true);
        repository.salvaDipendente(cassiere);

        Dipendente risultato = repository.trovaDipendenteByMatricola("C006");

        assertNotNull(risultato);
        assertEquals("C006", risultato.getMatricola());
        assertEquals("Mario", risultato.getNome());
        assertEquals("Rossi", risultato.getCognome());
        assertEquals("Cassiere", risultato.getRuolo());
    }
    
    @Test
    void testTrovaTuttiDipendenti() {

        DBDipendenti repository = new DBDipendenti(true);

        Manager manager = new Manager("Luca", "Bianchi", "luca.bianchi@test.it", "password", LocalDate.of(1985, 3, 15), repository);
        manager.setMatricola("M001");

        Magazziniere magazziniere = new Magazziniere("Anna", "Verdi", "anna.verdi@test.it", "password", LocalDate.of(1990, 7, 20));
        magazziniere.setMatricola("MAG001");

        ResponsabileDiReparto responsabile = new ResponsabileDiReparto("Paolo", "Neri", "paolo.neri@test.it", "password", LocalDate.of(1988, 11, 5));
        responsabile.setMatricola("R001");

        repository.salvaDipendente(manager);
        repository.salvaDipendente(magazziniere);
        repository.salvaDipendente(responsabile);

        java.util.List<Dipendente> dipendenti = repository.trovaTuttiDipendenti();

        assertNotNull(dipendenti);

        assertTrue(dipendenti.stream().anyMatch(d -> "M001".equals(d.getMatricola())));

        assertTrue(dipendenti.stream().anyMatch(d -> "MAG001".equals(d.getMatricola())));

        assertTrue(dipendenti.stream().anyMatch(d -> "R001".equals(d.getMatricola())));
    }
    
    @Test
    void testSalvaTurno() {

        DBClienti repositoryClienti = new DBClienti();
        
        IControlloSpesa controlloSpesa = null;

        Cassiere cassiere = new Cassiere("Mario", "Rossi", "mario.rossi10@test.it", "password", LocalDate.of(1995, 5, 10), repositoryClienti, controlloSpesa);

        cassiere.setMatricola("C010");

        DBDipendenti repository = new DBDipendenti(true);

        repository.salvaDipendente(cassiere);

        Turno turno = new Turno(LocalDate.of(2026, 10, 5), LocalTime.of(8, 0), LocalTime.of(16, 0));

        assertDoesNotThrow(() -> repository.salvaTurno("C010", turno));
    }
    
    @Test
    void testTrovaTurniByMatricola() {

        DBClienti repositoryClienti = new DBClienti();
        
        IControlloSpesa controlloSpesa = null;

        Cassiere cassiere = new Cassiere("Mario", "Rossi", "mario.rossi11@test.it", "password", LocalDate.of(1995, 5, 10), repositoryClienti, controlloSpesa);

        cassiere.setMatricola("C011");

        DBDipendenti repository = new DBDipendenti(true);

        repository.salvaDipendente(cassiere);

        Turno turno = new Turno(LocalDate.of(2026, 10, 5), LocalTime.of(8, 0), LocalTime.of(16, 0));

        repository.salvaTurno("C011", turno);

        List<Turno> turni = repository.trovaTurniByMatricola("C011");

        assertNotNull(turni);
        assertEquals(1, turni.size());

        Turno risultato = turni.get(0);

        assertEquals(LocalDate.of(2026, 10, 5), risultato.getData());
        assertEquals(LocalTime.of(8, 0), risultato.getOrarioInizio());
        assertEquals(LocalTime.of(16, 0), risultato.getOrarioFine());
    }
}

