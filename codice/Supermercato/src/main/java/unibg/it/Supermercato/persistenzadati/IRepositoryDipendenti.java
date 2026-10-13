package unibg.it.Supermercato.persistenzadati;

import java.util.List;

import unibg.it.Supermercato.sistemaaccesso.Dipendente;
import unibg.it.Supermercato.gestionedipendenti.Turno;

public interface IRepositoryDipendenti {

    void salvaDipendente(Dipendente dipendente);

    Dipendente verificaCredenziali(String email, String password);

    Dipendente trovaDipendenteByMatricola(String matricola);

    List<Dipendente> trovaTuttiDipendenti();
    
    void salvaTurno(String matricola, Turno turno);
    
    List<Turno> trovaTurniByMatricola(String matricola);
    
    void aggiornaPresenza(String matricola, java.time.LocalDate dataTurno, java.time.LocalTime orarioInizio,
            			  java.time.LocalTime oraIngresso, java.time.LocalTime oraUscita, boolean presenzaRegistrata, boolean completato);
}