package unibg.it.Supermercato.gestionedipendenti;

import java.util.List;
import unibg.it.Supermercato.sistemaaccesso.Dipendente;

public interface ITurni {

    void assegnaTurno(Dipendente dipendente, Turno turno);

    List<Turno> visualizzaTurni(Dipendente dipendente);
}
