/**
 * @author Chiara Tremolada
 */

package unibg.it.Supermercato.gestionemagazzino;

import java.util.List;

public class GestoreOrdini implements IOrdini {

    private IRepositoryOrdini repositoryOrdini;
    private IRepositoryProdotti repositoryProdotti;

    public GestoreOrdini(IRepositoryOrdini repositoryOrdini) {
        this.repositoryOrdini = repositoryOrdini;
    }

    public void setRepositoryProdotti(IRepositoryProdotti repositoryProdotti) {
        this.repositoryProdotti = repositoryProdotti;
    }

    /** questo metodo crea un ordine
	 * @param il fornitore a cui è associato l'ordine
	 * @return l'ordine creato
	 */
    @Override
    public OrdineFornitore creaOrdine(Fornitore fornitore) {
        if (fornitore == null) {
            return null;
        }

        return new OrdineFornitore(fornitore);
    }

    /** questo metodo salva un ordine in persistenza
	 * @param l'ordine da salvare
	 */
    @Override
    public void salvaOrdine(OrdineFornitore ordine) {
        if (ordine == null) {
            return;
        }

        repositoryOrdini.salvaOrdine(ordine);
    }

    /** questo metodo aggiunge un prodotto all'ordine
	 * @param l'ordine a cui aggiungere un prodotto, il prodotto da aggiungere e la quantità del prodotto
	 */
    @Override
    public void aggiungiProdotto(OrdineFornitore ordine, Prodotto prodotto, int quantita) {

        if (ordine == null || prodotto == null || quantita <= 0) {
            return;
        }

        ordine.aggiungiProdotto(prodotto, quantita);
    }

    /** questo metodo permette di visualizzare tutti gli ordini fatti
	 * @return la lista degli ordini
	 */
    @Override
    public List<OrdineFornitore> visualizzaOrdini() {
        return repositoryOrdini.trovaTuttiOrdini();
    }

    /** questo metodo riceve un ordine 
	 * @param l'ordine con i prodotti da aggiornare
	 */
    @Override
    public void riceviOrdine(OrdineFornitore ordine) {

        if (ordine == null || ordine.isRicevuto()) {
            return;
        }

        if (repositoryProdotti == null) {
            throw new IllegalStateException("Repository dei prodotti non impostato.");
        }

        for (RigaOrdine riga : ordine.getRighe()) {
            if (riga != null && riga.getProdotto() != null) {
                aggiornaMagazzino(riga);
            }
        }

        ordine.segnaRicevuto();
        repositoryOrdini.aggiornaStato(ordine);
    }

    /** questo metodo aggiorna le scorte in magazzino dei prodotti ordinati
	 * @param la riga dell'ordine
	 */
    private void aggiornaMagazzino(RigaOrdine riga) {
        Prodotto salvato = repositoryProdotti.trovaProdottoByCodiceABarre(riga.getProdotto().getCodiceABarre());

        if (salvato != null) {
            salvato.setQuantitaMagazzino(salvato.getQuantitaMagazzino() + riga.getQuantita());
            repositoryProdotti.modificaProdotto(salvato);
        }
    }
}
