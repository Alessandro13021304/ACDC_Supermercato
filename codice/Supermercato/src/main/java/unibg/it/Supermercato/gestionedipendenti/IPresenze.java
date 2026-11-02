package unibg.it.Supermercato.gestionedipendenti;

import unibg.it.Supermercato.sistemaaccesso.Dipendente;

public interface IPresenze {

    
    void registraIngresso(Dipendente dipendente, String email, String password);

    void registraUscita(Dipendente dipendente, String email, String password);

    
    void registraIngresso(Dipendente dipendente);

    void registraUscita(Dipendente dipendente);

    double calcolaOreLavorate(Dipendente dipendente);
}
