/**
 * @author Chiara Dentella
 */

package unibg.it.Supermercato.gestionemagazzino;

import java.util.List;

public interface IRepositoryProdotti {

    void salvaProdotto(Prodotto prodotto);

    void rimuoviProdotto(String codiceABarre);

    void modificaProdotto(Prodotto prodotto);

    Prodotto trovaProdottoByCodiceABarre(String codiceABarre);

    List<Prodotto> trovaTuttiProdotti();
}