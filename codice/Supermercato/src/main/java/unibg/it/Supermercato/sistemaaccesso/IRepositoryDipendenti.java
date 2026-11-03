/**
 * @author Chiara Dentella
 */

package unibg.it.Supermercato.sistemaaccesso;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface IRepositoryDipendenti {

    void salvaDipendente(Dipendente dipendente);

    Dipendente verificaCredenziali(String email, String password);

    Dipendente trovaDipendenteByMatricola(String matricola);

    List<Dipendente> trovaTuttiDipendenti();

    void salvaTurno(String matricola, Turno turno);

    List<Turno> trovaTurniByMatricola(String matricola);

    void aggiornaPresenza(String matricola, LocalDate dataTurno, LocalTime orarioInizio,
            			  LocalTime oraIngresso, LocalTime oraUscita, boolean presenzaRegistrata, boolean completato);
}