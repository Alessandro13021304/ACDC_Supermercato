/**
 * @author Davide Anthony Carmona Contreras
 */

package unibg.it.Supermercato.selfscanning;

import java.util.ArrayList;
import java.util.List;
import unibg.it.Supermercato.gestionemagazzino.Prodotto;

public class Carrello implements ICarrello {

    private double totale;

    private List<ElementoCarrello> elementi;

    public Carrello() {
        elementi = new ArrayList<>();
    }

    /** questo metodo aggiunge un elemento al carrello
	 * @param l'elemento da aggiungere
	 */
    @Override
    public void aggiungiElemento(ElementoCarrello e) {
        if (e != null) {
            elementi.add(e);
        }
    }

    /** questo metodo rimuove un elemento dal carrello
	 * @param l'elemento da rimuovere
	 */
    @Override
    public void rimuoviElemento(ElementoCarrello e) {
        if (e != null) {
            elementi.remove(e);
        }
    }
    
    /** questo metodo verifica se un elemento c'è già nel carrello
	 * @param il prodotto da trovare
	 * @return l'elemento trovato
	 */
	@Override
	public ElementoCarrello trovaElemento(Prodotto p) {
	    if (p != null) {
	        for (ElementoCarrello elemento : elementi) {
	            if (elemento.prodotto.getCodiceABarre().equals(p.getCodiceABarre())) {
	                return elemento;
	            }
	        }
	    }
	    return null;
	}
    
	/** questo metodo calcola il totale del carrello
	 * @return il totale del carrello
	 */
    @Override
    public double calcolaTotale() {
        totale = 0.0;

        for (ElementoCarrello elemento : elementi) {
            totale += elemento.calcolaTotaleRiga();
        }

        return totale;
    }
    
    public int numeroElementi() {
        return elementi.size();
    }

    public List<ElementoCarrello> getElementi() {
        return elementi;
    }
	
    /** questo metodo aggiunge un prodotto al carrello
	 * @param il prodotto da aggiungere
	 */
	@Override
	public boolean aggiungiProdotto(Prodotto p) {
	
	    if (p == null || p.getQuantitaScaffale() <= 0) {
	        return false;
	    }
	
	    ElementoCarrello elemento = trovaElemento(p);
	
	    if (elemento != null && elemento.getQuantita() >= p.getQuantitaScaffale()) {
	        return false;
	    }
	
	    if (elemento != null) {
	        elemento.incrementaQuantita();
	
	    } else {
	        CalcolatoreSconto calcolatore = new CalcolatoreSconto();
	
	        double prezzoApplicato = calcolatore.applicaSconto(p.getPrezzo(), p.getSconto());
	
	        elemento = new ElementoCarrello(p, 1, prezzoApplicato);
	
	        aggiungiElemento(elemento);
	    }
	
	    return true;
	}
}