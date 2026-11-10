/**
 * @author Chiara Tremolada
 */

package unibg.it.Supermercato.gestionemagazzino;

import java.util.ArrayList;
import java.util.List;

public class ScortaMagazzino {

    private List<Prodotto> prodotti;

    public ScortaMagazzino() {
        prodotti = new ArrayList<>();
    }

    /** questo metodo aggiunge un prodotto alle scorte del magazzino
	 * @param il prodotto da aggiungere alle scorte
	 * @return 
	 */
    public void aggiungiProdotto(Prodotto prodotto) {
        if (prodotto != null && !prodotti.contains(prodotto)) {
            prodotti.add(prodotto);
        }
    }

    /** questo metodo fa vedere quali prodotti sono da ordinare
	 * @return la lista di prodotti da ordinare
	 */
    public List<Prodotto> getProdottiDaRiordinare() {
        List<Prodotto> prodottiDaRiordinare = new ArrayList<>();

        for (Prodotto prodotto : prodotti) {
            if (prodotto.getQuantitaScaffale() < ParametriScorte.SOGLIA_MINIMA && prodotto.getQuantitaMagazzino() < ParametriScorte.SOGLIA_MINIMA) {
                prodottiDaRiordinare.add(prodotto);
            }
        }

        return prodottiDaRiordinare;
    }
}
