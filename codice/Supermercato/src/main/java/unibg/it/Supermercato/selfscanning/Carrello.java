package unibg.it.Supermercato.selfscanning;

import java.util.ArrayList;
import java.util.List;
import unibg.it.Supermercato.gestionemagazzino.Prodotto;

public class Carrello implements ICarrello {

    private double totale;

    private List<ElementoCarrello> elementi;

    public Carrello() {
        elementi = new ArrayList<>();
    }

    @Override
    public void aggiungiElemento(ElementoCarrello e) {
        if (e != null) {
            elementi.add(e);
        }
    }

    @Override
    public void rimuoviElemento(ElementoCarrello e) {
        if (e != null) {
            elementi.remove(e);
        }
    }
    
    @Override
    public ElementoCarrello trovaElemento(Prodotto p) {
        if (p != null) {
            for (ElementoCarrello elemento : elementi) {
                if (elemento.prodotto == p) {
                    return elemento;
                }
            }
        }
        return null;
    }
    
    @Override
    public double calcolaTotale() {
        totale = 0.0;

        for (ElementoCarrello elemento : elementi) {
            totale += elemento.calcolaTotaleRiga();
        }

        return totale;
    }
    
    int numeroElementi() {
        return elementi.size();
    }
    
    List<ElementoCarrello> getElementi() {
        return elementi;
    }
    
    @Override
    public void aggiungiProdotto(Prodotto p) {
        if (p == null) {
            return;
        }

        ElementoCarrello elemento = trovaElemento(p);

        if (elemento != null && elemento.getQuantita() >= p.getQuantitaScaffale()) {
            return;
        }

        if (elemento != null) {
            elemento.incrementaQuantita();
        } else {
            if (p.getQuantitaScaffale() <= 0) {
                return;
            }

            CalcolatoreSconto calcolatore = new CalcolatoreSconto();

            double prezzoApplicato = calcolatore.applicaSconto(
                    p.getPrezzo(),
                    p.getSconto()
            );

            elemento = new ElementoCarrello(
                    p,
                    1,
                    prezzoApplicato
            );

            aggiungiElemento(elemento);
        }
    }
}