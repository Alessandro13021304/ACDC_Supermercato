package unibg.it.Supermercato.persistenzadati;

import java.util.List;
import unibg.it.Supermercato.gestionemagazzino.Prodotto;

public interface IRepositoryProdotti {

    void salvaProdotto(Prodotto prodotto);
    
    void rimuoviProdotto(String codiceABarre);

    void modificaProdotto(Prodotto prodotto);

    Prodotto trovaProdottoByCodiceABarre(String codiceABarre);

    List<Prodotto> trovaTuttiProdotti();
}