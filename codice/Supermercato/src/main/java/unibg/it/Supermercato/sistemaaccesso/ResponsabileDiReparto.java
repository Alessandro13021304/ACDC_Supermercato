package unibg.it.Supermercato.sistemaaccesso;

import unibg.it.Supermercato.gestionemagazzino.Fornitore;
import unibg.it.Supermercato.gestionemagazzino.OrdineFornitore;
import unibg.it.Supermercato.gestionemagazzino.Prodotto;

public class ResponsabileDiReparto extends Dipendente {

    public OrdineFornitore elaboraOrdine(Fornitore f) {
        return null;
    }

    public void aggiungiProdotto(Prodotto p) {
    }

    public void rimuoviProdotto(Prodotto p) {
    }

    public void modificaProdotto(Prodotto p) {
    }

    @Override
    public String getRuolo() {
        return "ResponsabileDiReparto";
    }
}
