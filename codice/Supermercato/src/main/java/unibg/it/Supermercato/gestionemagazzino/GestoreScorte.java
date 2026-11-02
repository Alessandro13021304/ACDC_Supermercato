package unibg.it.Supermercato.gestionemagazzino;

import java.util.ArrayList;
import java.util.List;

public class GestoreScorte implements IScorte{
	
	private List<ScorteObserver> osservatori;
    private static final int SOGLIA_MINIMA = 5;

    public GestoreScorte() {
        osservatori = new ArrayList<>();
    }
    
    @Override
    public void aggiornaScorte(Prodotto prodotto, ETipoScorta tipoScorta) {
        if (prodotto == null || tipoScorta == null) {
            return;
        }
        
        if (tipoScorta == ETipoScorta.SCAFFALE
                && prodotto.getQuantitaScaffale() >= SOGLIA_MINIMA) {
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

    public void notificaObserver(Prodotto prodotto, ETipoScorta tipoScorta) {
        for (ScorteObserver observer : osservatori) {
            observer.aggiorna(prodotto, tipoScorta);
        }
    }
}
