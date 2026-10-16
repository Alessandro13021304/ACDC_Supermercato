package unibg.it.Supermercato.gestionemagazzino;

import java.util.List;

public interface IProdotti {
	
	void aggiungiProdotto(Prodotto prodotto);

    void rimuoviProdotto(Prodotto prodotto);

    void modificaProdotto(Prodotto prodotto);

    Prodotto cercaProdotto(String codiceABarre);

    List<Prodotto> visualizzaProdotti();


}
