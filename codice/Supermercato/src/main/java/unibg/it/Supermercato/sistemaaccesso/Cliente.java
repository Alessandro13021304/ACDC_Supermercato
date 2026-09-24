package unibg.it.Supermercato.sistemaaccesso;

import java.util.List;
import unibg.it.Supermercato.selfscanning.SessioneSpesa;

public class Cliente extends Persona implements RuoloUtente {

    private int puntiFedelta;
    private boolean isSegnalato;

    public void registrazione() {
    }

    public int visualizzaPunti() {
        return puntiFedelta;
    }

    public List<SessioneSpesa> visualizzaStorico() {
        return null;
    }

    @Override
    public String getRuolo() {
        return "Cliente";
    }
}
