package unibg.it.Supermercato.selfscanning;

import unibg.it.Supermercato.gestionemagazzino.Prodotto;

public interface ICarrello {

    void aggiungiElemento(ElementoCarrello e);

    void rimuoviElemento(ElementoCarrello e);

    ElementoCarrello trovaElemento(Prodotto p);

    double calcolaTotale();
    
    void aggiungiProdotto(Prodotto p);
}