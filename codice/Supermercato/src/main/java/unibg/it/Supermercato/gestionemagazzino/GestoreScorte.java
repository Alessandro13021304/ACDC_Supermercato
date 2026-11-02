package unibg.it.Supermercato.gestionemagazzino;

import java.util.ArrayList;
import java.util.List;

public class GestoreScorte implements IScorte {

    private List<IScorteObserver> osservatori;

    public GestoreScorte() {
        osservatori = new ArrayList<>();
    }

    @Override
    public void aggiornaScorte(Prodotto prodotto, ETipoScorta tipoScorta) {
        if (prodotto == null || tipoScorta == null) {
            return;
        }

        if (tipoScorta == ETipoScorta.SCAFFALE && prodotto.getQuantitaScaffale() >= ParametriScorte.SOGLIA_MINIMA) {
            return;
        }

        notificaObserver(prodotto, tipoScorta);
    }

    @Override
    public void aggiungiObserver(IScorteObserver observer) {
        if (observer != null && !osservatori.contains(observer)) {
            osservatori.add(observer);
        }
    }

    @Override
    public void rimuoviObserver(IScorteObserver observer) {
        osservatori.remove(observer);
    }

    public void notificaObserver(Prodotto prodotto, ETipoScorta tipoScorta) {
        for (IScorteObserver observer : osservatori) {
            observer.aggiorna(prodotto, tipoScorta);
        }
    }
}
