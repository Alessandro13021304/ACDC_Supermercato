package unibg.it.Supermercato.selfscanning;

import unibg.it.Supermercato.gestionemagazzino.Prodotto;

public class ElementoCarrello {

    private int quantita;
    private double prezzoApplicato;

    public Prodotto prodotto;

    public double calcolaTotaleRiga() {
        return prezzoApplicato;
    }
}