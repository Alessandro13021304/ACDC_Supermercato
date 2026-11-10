/**
 * @author Davide Anthony Carmona Contreras
 */

package unibg.it.Supermercato.selfscanning;

import java.util.ArrayList;
import java.util.List;

import unibg.it.Supermercato.gestionemagazzino.Prodotto;
import unibg.it.Supermercato.sistemaaccesso.ICarrelloFisico;

public class CarrelloFisico implements ICarrelloFisico {

    private List<ElementoCarrello> elementi;

    public CarrelloFisico() {
        elementi = new ArrayList<>();
    }

    /** questo metodo aggiunge un elemento al carrello fisico
	 * @param l'elemento da aggiungere
	 */
    public void aggiungiElemento(ElementoCarrello e) {
        if (e != null) {
            elementi.add(e);
        }
    }

    /** questo metodo rimuove un elemento dal carrello fisico
	 * @param l'elemento da rimuovere
	 */
    public void rimuoviElemento(ElementoCarrello e) {
        if (e != null) {
            elementi.remove(e);
        }
    }

    /** questo metodo svuota il carrello fisico
	 */
    public void svuota() {
        elementi.clear();
    }

    @Override
    public int numeroElementi() {
        return elementi.size();
    }

    /** questo metodo verifica se un prodotto nel carrello corrisponde al prodotto e alla quantità nel carrello fisico
	 * @param il prodotto e la quantità da verificare
	 * @return true se corrispondono, altrimenti false
	 */
    @Override
    public boolean contieneElemento(Prodotto prodotto, int quantita) {
        if (prodotto == null) {
            return false;
        }

        for (ElementoCarrello elemento : elementi) {
            if (prodotto.equals(elemento.prodotto) && elemento.getQuantita() == quantita) {
                return true;
            }
        }
        return false;
    }
}
