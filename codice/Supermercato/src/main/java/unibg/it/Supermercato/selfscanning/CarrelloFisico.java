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

    public void aggiungiElemento(ElementoCarrello e) {
        if (e != null) {
            elementi.add(e);
        }
    }

    public void rimuoviElemento(ElementoCarrello e) {
        if (e != null) {
            elementi.remove(e);
        }
    }

    public void svuota() {
        elementi.clear();
    }

    @Override
    public int numeroElementi() {
        return elementi.size();
    }

    @Override
    public boolean contieneElemento(Prodotto prodotto, int quantita) {
        if (prodotto == null) {
            return false;
        }

        for (ElementoCarrello elemento : elementi) {
            // Il confronto usa il codice a barre, non il riferimento all'oggetto
            if (prodotto.equals(elemento.prodotto) && elemento.getQuantita() == quantita) {
                return true;
            }
        }
        return false;
    }
}
