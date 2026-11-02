package unibg.it.Supermercato.gestionemagazzino;

import java.util.List;

public interface IRepositoryOrdini {

    void salvaOrdine(OrdineFornitore ordine);

    List<OrdineFornitore> trovaTuttiOrdini();

    void aggiornaStato(OrdineFornitore ordine);
}
