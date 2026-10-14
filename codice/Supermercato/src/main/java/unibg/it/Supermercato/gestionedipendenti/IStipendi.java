package unibg.it.Supermercato.gestionedipendenti;

import unibg.it.Supermercato.sistemaaccesso.Dipendente;

public interface IStipendi {

    Stipendio calcolaStipendio(Dipendente dipendente);

    Stipendio calcolaStipendioMensile(Dipendente dipendente, int anno, int mese);
}