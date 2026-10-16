package unibg.it.Supermercato.persistenzadati;

import java.util.List;

import unibg.it.Supermercato.gestionemagazzino.OrdineFornitore;

public interface IRepositoryOrdini {

    void salvaOrdine(OrdineFornitore ordine);

    List<OrdineFornitore> trovaTuttiOrdini();
}