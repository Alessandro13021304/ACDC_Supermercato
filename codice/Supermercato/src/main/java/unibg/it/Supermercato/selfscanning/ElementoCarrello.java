package unibg.it.Supermercato.selfscanning;

import unibg.it.Supermercato.gestionemagazzino.Prodotto;

public class ElementoCarrello {

    private int quantita;
    private double prezzoApplicato;

    public Prodotto prodotto;

    public ElementoCarrello(Prodotto prodotto, int quantita, double prezzoApplicato) {
        this.prodotto = prodotto;
        this.quantita = quantita;
        this.prezzoApplicato = prezzoApplicato;
    }

    public double calcolaTotaleRiga() {
        return prezzoApplicato * quantita;
    }

    public int getQuantita() {
        return quantita;
    }

    void incrementaQuantita() {
        quantita++;
    }
    
    void decrementaQuantita() {
        quantita--;
    }
}