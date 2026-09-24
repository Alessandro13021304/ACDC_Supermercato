package unibg.it.Supermercato.sistemaaccesso;

import unibg.it.Supermercato.selfscanning.SessioneSpesa;

public class Cassiere extends Dipendente {

    public boolean effettuaControllo(SessioneSpesa s) {
        return false;
    }

    @Override
    public String getRuolo() {
        return "Cassiere";
    }
}
