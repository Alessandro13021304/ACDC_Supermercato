/**
 * @author Alessandro Frassi
 */

package unibg.it.Supermercato.sistemaaccesso;

import unibg.it.Supermercato.gestionemagazzino.Prodotto;

public interface ICarrelloFisico {

    int numeroElementi();

    boolean contieneElemento(Prodotto prodotto, int quantita);
}
