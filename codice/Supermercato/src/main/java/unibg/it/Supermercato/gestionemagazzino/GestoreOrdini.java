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

    @Override
    public OrdineFornitore creaOrdine(Fornitore fornitore) {
        if (fornitore == null) {
            return null;
        }

        return new OrdineFornitore(fornitore);
    }

    @Override
    public void salvaOrdine(OrdineFornitore ordine) {
        if (ordine == null) {
            return;
        }

        repositoryOrdini.salvaOrdine(ordine);
    }

    @Override
    public void aggiungiProdotto(OrdineFornitore ordine, Prodotto prodotto, int quantita) {

        if (ordine == null || prodotto == null || quantita <= 0) {
            return;
        }

        ordine.aggiungiProdotto(prodotto, quantita);
    }

    @Override
    public List<OrdineFornitore> visualizzaOrdini() {
        return repositoryOrdini.trovaTuttiOrdini();
    }

    @Override
    public void riceviOrdine(OrdineFornitore ordine) {

        if (ordine == null) {
            return;
        }

        for (RigaOrdine riga : ordine.getRighe()) {
            if (riga != null && riga.getProdotto() != null) {
                aggiornaMagazzino(riga);
            }
        }
    }

    private void aggiornaMagazzino(RigaOrdine riga) {
        Prodotto salvato = repositoryProdotti.trovaProdottoByCodiceABarre(
                riga.getProdotto().getCodiceABarre());

        if (salvato != null) {
            salvato.setQuantitaMagazzino(salvato.getQuantitaMagazzino() + riga.getQuantita());
            repositoryProdotti.modificaProdotto(salvato);
        }
    }
}