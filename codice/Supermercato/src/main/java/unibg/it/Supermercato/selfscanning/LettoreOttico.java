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
    
	public boolean scansionaProdotto(Prodotto p) {
	    if (p == null || carrello == null) {
	        return false;
	    }
	
	    return carrello.aggiungiProdotto(p);
	}
    
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