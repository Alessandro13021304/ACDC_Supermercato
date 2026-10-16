package unibg.it.Supermercato.sistemaaccesso;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import unibg.it.Supermercato.selfscanning.IControlloSpesa;
import unibg.it.Supermercato.gestionedipendenti.Stipendio;
import unibg.it.Supermercato.gestionedipendenti.Turno;
import unibg.it.Supermercato.persistenzadati.IRepositoryClienti;
import unibg.it.Supermercato.persistenzadati.DBDipendenti;
import unibg.it.Supermercato.persistenzadati.DBConnection;

public class ManagerTest {

    @Test
    void calcolaStipendioDiManagerECassiere() throws Exception {
    	
    	DBConnection.getInstance().resetTestDatabase("dipendenti");

    	DBDipendenti repositoryDipendenti = new DBDipendenti(true);

        Manager manager = new Manager(
                "Mario",
                "Rossi",
                "manager@email.it",
                "password",
                LocalDate.of(1985, 1, 1),
                repositoryDipendenti
        );
        
        manager.setMatricola("M001");
        repositoryDipendenti.salvaDipendente(manager);

        IRepositoryClienti repository = null;
        
        IControlloSpesa controlloSpesa = null;

        Cassiere cassiere = new Cassiere(
                "Luca",
                "Bianchi",
                "cassiere@email.it",
                "password",
                LocalDate.of(1990, 1, 1),
                repository,
                controlloSpesa
        );
        
        cassiere.setMatricola("C001");
        cassiere.setRepositoryDipendenti(repositoryDipendenti);
        repositoryDipendenti.salvaDipendente(cassiere);

        Turno turnoManager = new Turno(
                LocalDate.of(2026, 9, 10),
                LocalTime.of(8, 0),
                LocalTime.of(16, 0)
        );

        turnoManager.impostaOraIngresso(LocalTime.of(8, 0));
        turnoManager.impostaOraUscita(LocalTime.of(16, 0));

        Turno turnoCassiere = new Turno(
                LocalDate.of(2026, 9, 10),
                LocalTime.of(8, 0),
                LocalTime.of(14, 0)
        );

        turnoCassiere.impostaOraIngresso(LocalTime.of(8, 0));
        turnoCassiere.impostaOraUscita(LocalTime.of(14, 0));

        repositoryDipendenti.salvaTurno(
                manager.getMatricola(),
                turnoManager
        );

        repositoryDipendenti.aggiornaPresenza(
                manager.getMatricola(),
                turnoManager.getData(),
                turnoManager.getOrarioInizio(),
                LocalTime.of(8, 0),
                LocalTime.of(16, 0),
                true,
                true
        );
        
        repositoryDipendenti.salvaTurno(
                cassiere.getMatricola(),
                turnoCassiere
        );

        repositoryDipendenti.aggiornaPresenza(
                cassiere.getMatricola(),
                turnoCassiere.getData(),
                turnoCassiere.getOrarioInizio(),
                LocalTime.of(8, 0),
                LocalTime.of(14, 0),
                true,
                true
        );

        List<Dipendente> dipendenti = new ArrayList<>();
        dipendenti.add(manager);
        dipendenti.add(cassiere);

        List<Stipendio> stipendi = manager.calcolaStipendi(dipendenti, 2026, 9);

        assertEquals(2, stipendi.size());

        assertEquals(128.0, stipendi.get(0).getImporto(), 0.01);

        assertEquals(60.0, stipendi.get(1).getImporto(), 0.01);
    }
}