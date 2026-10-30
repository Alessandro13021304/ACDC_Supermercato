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
        for (ElementoCarrello elemento : elementi) {
            if (elemento.prodotto == prodotto && elemento.getQuantita() == quantita) {
                return true;
            }
        }
        return false;
    }
}