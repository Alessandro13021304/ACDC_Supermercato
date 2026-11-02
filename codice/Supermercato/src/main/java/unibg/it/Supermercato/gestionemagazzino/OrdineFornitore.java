/**
 * @author Chiara Tremolada
 */

package unibg.it.Supermercato.gestionemagazzino;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class OrdineFornitore {

    private LocalDate dataOrdine;
    private String stato;
    private double totale;

    public Fornitore fornitore;
    public List<RigaOrdine> righe;
    
    public OrdineFornitore(Fornitore fornitore) {
        this.fornitore = fornitore;
        this.dataOrdine = LocalDate.now();
        this.stato = "CREATO";
        this.totale = 0.0;
        this.righe = new ArrayList<>();
    }
    
    public OrdineFornitore(Fornitore fornitore, LocalDate dataOrdine, String stato, double totale) {
		this.fornitore = fornitore;
		this.dataOrdine = dataOrdine;
		this.stato = stato;
		this.totale = totale;
		this.righe = new ArrayList<>();
	}

    /** questo metodo aggiunge un prodotto all'ordine
	 * @param il prodotto da aggiungere e la quantità del prodotto
	 */
    public void aggiungiProdotto(Prodotto prodotto, int quantita) {
        if (prodotto != null && quantita > 0) {
            RigaOrdine riga = new RigaOrdine(prodotto, quantita);
            righe.add(riga);
            totale += riga.calcolaTotale();
        }
    }
    
    /** questo metodo aggiunge una riga all'ordine
	 * @param la riga da aggiungere
	 */
    public void aggiungiRiga(RigaOrdine riga) {
        if (riga != null) {
            righe.add(riga);
        }
    }

    public List<RigaOrdine> getRighe() {
        return righe;
    }

    public Fornitore getFornitore() {
        return fornitore;
    }

    public LocalDate getDataOrdine() {
        return dataOrdine;
    }

    public String getStato() {
        return stato;
    }

    public double getTotale() {
        return totale;
    }
}