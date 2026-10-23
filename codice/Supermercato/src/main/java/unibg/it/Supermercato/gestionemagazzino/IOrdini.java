package unibg.it.Supermercato.gestionemagazzino;

import java.util.List;

public interface IOrdini {
	
	OrdineFornitore creaOrdine(Fornitore fornitore);
	
	void salvaOrdine(OrdineFornitore ordine);

    void aggiungiProdotto(OrdineFornitore ordine, Prodotto prodotto, int quantita);

    List<OrdineFornitore> visualizzaOrdini();
    
    void riceviOrdine(OrdineFornitore ordine);

}
