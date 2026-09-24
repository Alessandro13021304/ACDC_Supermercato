package unibg.it.Supermercato.sistemaaccesso;

import unibg.it.Supermercato.gestionemagazzino.Prodotto;

public class Magazziniere extends Dipendente {

    public void rifornisciScaffale(Prodotto p, int qty) {
    }

    public void segnalaScorteInsufficienti() {
    }

    @Override
    public String getRuolo() {
        return "Magazziniere";
    }
}
