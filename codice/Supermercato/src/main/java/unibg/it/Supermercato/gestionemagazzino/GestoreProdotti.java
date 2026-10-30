package unibg.it.Supermercato.gestionemagazzino;

import java.util.List;

public class GestoreProdotti implements IProdotti {

    private IRepositoryProdotti repositoryProdotti;

    public GestoreProdotti(IRepositoryProdotti repositoryProdotti) {
        this.repositoryProdotti = repositoryProdotti;
    }

    @Override
    public void aggiungiProdotto(Prodotto prodotto) {
        if (prodotto == null) {
            return;
        }

        repositoryProdotti.salvaProdotto(prodotto);
    }

    @Override
    public void rimuoviProdotto(Prodotto prodotto) {
        if (prodotto == null) {
            return;
        }

        repositoryProdotti.rimuoviProdotto(prodotto.getCodiceABarre());
    }

    @Override
    public void modificaProdotto(Prodotto prodotto) {
        if (prodotto == null) {
            return;
        }

        repositoryProdotti.modificaProdotto(prodotto);
    }

    @Override
    public Prodotto cercaProdotto(String codiceABarre) {
        if (codiceABarre == null) {
            return null;
        }

        return repositoryProdotti.trovaProdottoByCodiceABarre(codiceABarre);
    }

    @Override
    public List<Prodotto> visualizzaProdotti() {
        return repositoryProdotti.trovaTuttiProdotti();
    }
}