/**
 * @author Chiara Tremolada
 */

package unibg.it.Supermercato.gestionemagazzino;

import java.util.ArrayList;
import java.util.List;

public class GestoreScorte implements IScorte {

    private List<IScorteObserver> osservatori;

    public GestoreScorte() {
        osservatori = new ArrayList<>();
    }

    /** questo metodo chiama la notifica gli observer quando la quantità delle scorte cambia
	 * @param il prodotto la cui quantità è cambiata e il tipo di scorta 
	 */
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

    /** questo metodo aggiunge un observer alla lista
	 * @param l'observer da aggiungere
	 */
    @Override
    public void aggiungiObserver(IScorteObserver observer) {
        if (observer != null && !osservatori.contains(observer)) {
            osservatori.add(observer);
        }
    }

    /** questo metodo rimuove un observer dalla lista
	 * @param l'observer da rimuovere
	 */
    @Override
    public void rimuoviObserver(IScorteObserver observer) {
        osservatori.remove(observer);
    }

    /** questo metodo notifica gli observer quando la quantità delle scorte cambia
	 * @param il prodotto la cui quantità è cambiata e il tipo di scorta
	 */
    public void notificaObserver(Prodotto prodotto, ETipoScorta tipoScorta) {
        for (IScorteObserver observer : osservatori) {
            observer.aggiorna(prodotto, tipoScorta);
        }
    }
}
