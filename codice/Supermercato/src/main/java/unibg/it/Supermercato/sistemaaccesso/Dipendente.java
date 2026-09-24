package unibg.it.Supermercato.sistemaaccesso;

import java.util.List;
import unibg.it.Supermercato.gestionedipendenti.Turno;

public abstract class Dipendente extends Persona implements RuoloUtente {

    private String matricola;
    private double stipendioBase;

    public void timbra() {
    }

    public List<Turno> visualizzaTurni() {
        return null;
    }
  
    @Override
    public String getRuolo() {
        return "Dipendente";
    }
}