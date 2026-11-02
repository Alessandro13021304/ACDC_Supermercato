/**
 * @author Chiara Tremolada
 */

package unibg.it.Supermercato.gestionemagazzino;

public interface IScorte {
	
void aggiornaScorte(Prodotto prodotto, TipoScorta tipoScorta);
	
	void aggiungiObserver(IScorteObserver observer);
	
	void rimuoviObserver(IScorteObserver observer);

}
