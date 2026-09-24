package unibg.it.Supermercato.gestionemagazzino;

import java.time.LocalDate;

public class OrdineFornitore {

    private LocalDate dataOrdine;
    private String stato;
    private double totale;

    public Fornitore fornitore;
    public Prodotto[] prodotto;
}