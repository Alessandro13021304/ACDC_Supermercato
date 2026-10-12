package unibg.it.Supermercato.gestionemagazzino;

import java.util.List;

import unibg.it.Supermercato.persistenzadati.DBOrdini;
import unibg.it.Supermercato.persistenzadati.IRepositoryOrdini;
import unibg.it.Supermercato.persistenzadati.IRepositoryProdotti;

public class GestoreOrdini implements IOrdini{
	
	private IRepositoryOrdini repositoryOrdini;
    private IRepositoryProdotti repositoryProdotti;

    public GestoreOrdini() {
        this.repositoryOrdini = new DBOrdini();
    }
    
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

        OrdineFornitore ordine = new OrdineFornitore(fornitore);
        repositoryOrdini.salvaOrdine(ordine);

        return ordine;
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

            if (riga == null || riga.getProdotto() == null) {
                continue;
            }

            Prodotto prodotto = riga.getProdotto();

            Prodotto prodottoSalvato =
                    repositoryProdotti.trovaProdottoByCodiceABarre(
                            prodotto.getCodiceABarre()
                    );

            if (prodottoSalvato == null) {
                continue;
            }

            int nuovaQuantitaMagazzino =
                    prodottoSalvato.getQuantitaMagazzino()
                    + riga.getQuantita();

            prodottoSalvato.setQuantitaMagazzino(
                    nuovaQuantitaMagazzino
            );

            repositoryProdotti.modificaProdotto(prodottoSalvato);
        }
    }
}
