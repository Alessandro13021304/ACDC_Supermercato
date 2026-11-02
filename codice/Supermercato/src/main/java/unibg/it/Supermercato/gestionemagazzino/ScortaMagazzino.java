package unibg.it.Supermercato.gestionemagazzino;

import java.util.ArrayList;
import java.util.List;

public class ScortaMagazzino {

    private List<Prodotto> prodotti;

    public ScortaMagazzino() {
        prodotti = new ArrayList<>();
    }

    public void aggiungiProdotto(Prodotto prodotto) {
        if (prodotto != null && !prodotti.contains(prodotto)) {
            prodotti.add(prodotto);
        }
    }

    public List<Prodotto> getProdottiDaRiordinare() {
        List<Prodotto> prodottiDaRiordinare = new ArrayList<>();

        for (Prodotto prodotto : prodotti) {
            if (prodotto.getQuantitaScaffale() < ParametriScorte.SOGLIA_MINIMA && prodotto.getQuantitaMagazzino() < ParametriScorte.SOGLIA_MINIMA) {
                prodottiDaRiordinare.add(prodotto);
            }
        }

        return prodottiDaRiordinare;
    }
}
