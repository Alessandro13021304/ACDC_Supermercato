/**
 * @author Davide Anthony Carmona Contreras
 */

package unibg.it.Supermercato.selfscanning;

import unibg.it.Supermercato.sistemaaccesso.IAutenticazione;
import unibg.it.Supermercato.gestionemagazzino.Prodotto;
import unibg.it.Supermercato.sistemaaccesso.Persona;
import unibg.it.Supermercato.sistemaaccesso.Cliente;

public class LettoreOttico {

    private IAutenticazione autenticazione;
    private ICarrello carrello;
    
    public LettoreOttico(IAutenticazione autenticazione, ICarrello carrello) {
        this.autenticazione = autenticazione;
        this.carrello = carrello;
    }
    
    /** questo metodo scansiona un prodotto e lo aggiunge al carrello
	 * @param il prodotto scansionato
	 * @return true se va a buon fine, altrimenti false
	 */
    public boolean scansionaProdotto(Prodotto p) {
	    if (p == null || carrello == null) {
	        return false;
	    }
	
	    return carrello.aggiungiProdotto(p);
	}
    
    /** questo metodo rimuove un prodotto dal carrello
	 * @param il prodotto da rimuovere
	 * @return true se va a buon fine, altrimenti false
	 */
	public boolean rimuoviProdotto(Prodotto p) {

	    if (p == null || carrello == null) {
	        return false;
	    }

	    ElementoCarrello elemento = carrello.trovaElemento(p);

	    if (elemento == null) {
	        return false;
	    }

	    if (elemento.getQuantita() > 1) {
	        elemento.decrementaQuantita();
	    } else {
	        carrello.rimuoviElemento(elemento);
	    }

	    return true;
	}
    
	/** questo metodo autentica un cliente
	 * @param la mail e la password del cliente
	 * @return il cliente autenticato
	 */
    public Cliente autentica(String email, String password) {
        if (autenticazione == null) {
            return null;
        }

        Persona persona = autenticazione.autentica(email, password);

        if (persona instanceof Cliente) {
            return (Cliente) persona;
        }

        return null;
    }
}