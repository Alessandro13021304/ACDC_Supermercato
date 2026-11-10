/**
 * @author Chiara Tremolada
 */

package unibg.it.Supermercato.gestionemagazzino;

import java.util.List;

public class GestoreProdotti implements IProdotti {

    private IRepositoryProdotti repositoryProdotti;

    public GestoreProdotti(IRepositoryProdotti repositoryProdotti) {
        this.repositoryProdotti = repositoryProdotti;
    }

    /** questo metodo salva un prodotto in persistenza
	 * @param il prodotto da salvare
	 */
    @Override
    public void aggiungiProdotto(Prodotto prodotto) {
        if (prodotto == null) {
            return;
        }

        repositoryProdotti.salvaProdotto(prodotto);
    }

    /** questo metodo elimina un prodotto in persistenza
	 * @param il prodotto da rimuovere
	 */
    @Override
    public void rimuoviProdotto(Prodotto prodotto) {
        if (prodotto == null) {
            return;
        }

        repositoryProdotti.rimuoviProdotto(prodotto.getCodiceABarre());
    }

    /** questo metodo modifica un prodotto in persistenza
	 * @param il prodotto da modificare
	 */
    @Override
    public void modificaProdotto(Prodotto prodotto) {
        if (prodotto == null) {
            return;
        }

        repositoryProdotti.modificaProdotto(prodotto);
    }

    /** questo metodo cerca un prodotto 
	 * @param il codice a barre del prodotto da cercare
	 * @return il prodotto trovato
	 */
    @Override
    public Prodotto cercaProdotto(String codiceABarre) {
        if (codiceABarre == null) {
            return null;
        }

        return repositoryProdotti.trovaProdottoByCodiceABarre(codiceABarre);
    }

    /** questo metodo permette di visualizzare la lista di tutti i prodotti
	 * @return la lista dei prodotti
	 */
    @Override
    public List<Prodotto> visualizzaProdotti() {
        return repositoryProdotti.trovaTuttiProdotti();
    }
}