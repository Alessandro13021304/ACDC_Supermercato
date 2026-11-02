package unibg.it.Supermercato.gestionemagazzino;

import java.util.ArrayList;
import java.util.List;

public class GestoreScorte implements IScorte {

    private List<ScorteObserver> osservatori;

    public GestoreScorte() {
        osservatori = new ArrayList<>();
    }

    @Override
    public void aggiornaScorte(Prodotto prodotto, TipoScorta tipoScorta) {
        if (prodotto == null || tipoScorta == null) {
            return;
        }

        if (tipoScorta == TipoScorta.SCAFFALE && prodotto.getQuantitaScaffale() >= ParametriScorte.SOGLIA_MINIMA) {
            return;
        }

        notificaObserver(prodotto, tipoScorta);
    }

    @Override
    public void aggiungiObserver(ScorteObserver observer) {
        if (observer != null && !osservatori.contains(observer)) {
            osservatori.add(observer);
        }
    }

    @Override
    public void rimuoviObserver(ScorteObserver observer) {
        osservatori.remove(observer);
    }

    public void notificaObserver(Prodotto prodotto, TipoScorta tipoScorta) {
        for (ScorteObserver observer : osservatori) {
            observer.aggiorna(prodotto, tipoScorta);
        }
    }
}
