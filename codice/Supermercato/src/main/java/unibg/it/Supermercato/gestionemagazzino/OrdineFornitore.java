package unibg.it.Supermercato.gestionemagazzino;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class OrdineFornitore {

    private int id;
    private LocalDate dataOrdine;
    private EStatoOrdine stato;
    private double totale;

    public Fornitore fornitore;
    public List<RigaOrdine> righe;

    public OrdineFornitore(Fornitore fornitore) {
        this.fornitore = fornitore;
        this.dataOrdine = LocalDate.now();
        this.stato = EStatoOrdine.CREATO;
        this.totale = 0.0;
        this.righe = new ArrayList<>();
    }

    public OrdineFornitore(Fornitore fornitore, LocalDate dataOrdine, String stato, double totale) {
        this.fornitore = fornitore;
        this.dataOrdine = dataOrdine;
        this.stato = EStatoOrdine.valueOf(stato);
        this.totale = totale;
        this.righe = new ArrayList<>();
    }

    public void aggiungiProdotto(Prodotto prodotto, int quantita) {
        if (prodotto != null && quantita > 0) {
            RigaOrdine riga = new RigaOrdine(prodotto, quantita);
            righe.add(riga);
            totale += riga.calcolaTotale();
        }
    }

    public void aggiungiRiga(RigaOrdine riga) {
        if (riga != null) {
            righe.add(riga);
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
        return stato.name();
    }

    public EStatoOrdine getStatoOrdine() {
        return stato;
    }

    public boolean isRicevuto() {
        return stato == EStatoOrdine.RICEVUTO;
    }

    public void segnaRicevuto() {
        stato = EStatoOrdine.RICEVUTO;
    }

    public double getTotale() {
        return totale;
    }
}
