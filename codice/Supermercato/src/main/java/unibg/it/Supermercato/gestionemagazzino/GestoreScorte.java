/**
 * @author Chiara Tremolada
 */

package unibg.it.Supermercato.gestionemagazzino;

import java.util.ArrayList;
import java.util.List;

public class GestoreScorte implements IScorte{
	
	private List<IScorteObserver> osservatori;
    private static final int SOGLIA_MINIMA = 5;

    public GestoreScorte() {
        osservatori = new ArrayList<>();
    }
    
    /** questo metodo chiama la notifica gli observer quando la quantità delle scorte cambia
	 * @param il prodotto la cui quantità è cambiata e il tipo di scorta 
	 */
    @Override
    public void aggiornaScorte(Prodotto prodotto, TipoScorta tipoScorta) {
        if (prodotto == null || tipoScorta == null) {
            return;
        }
        
        if (tipoScorta == TipoScorta.SCAFFALE
                && prodotto.getQuantitaScaffale() >= SOGLIA_MINIMA) {
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
	 * @return 
	 */
    @Override
    public void rimuoviObserver(IScorteObserver observer) {
        osservatori.remove(observer);
    }

    /** questo metodo notifica gli observer quando la quantità delle scorte cambia
	 * @param il prodotto la cui quantità è cambiata e il tipo di scorta
	 * @return 
	 */
    public void notificaObserver(Prodotto prodotto, TipoScorta tipoScorta) {
        for (IScorteObserver observer : osservatori) {
            observer.aggiorna(prodotto, tipoScorta);
        }
    }
}
