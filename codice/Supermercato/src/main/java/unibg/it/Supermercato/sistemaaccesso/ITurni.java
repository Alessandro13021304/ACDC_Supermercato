package unibg.it.Supermercato.sistemaaccesso;

import java.util.List;

public interface ITurni {

    void assegnaTurno(Dipendente dipendente, Turno turno);

    List<Turno> visualizzaTurni(Dipendente dipendente);
}
