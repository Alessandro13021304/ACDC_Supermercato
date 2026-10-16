package unibg.it.Supermercato.gestionemagazzino;

public class RigaOrdine {
	
	private Prodotto prodotto;
    private int quantita;

    public RigaOrdine(Prodotto prodotto, int quantita) {
        this.prodotto = prodotto;
        this.quantita = quantita;
    }

    public Prodotto getProdotto() {
        return prodotto;
    }

    public int getQuantita() {
        return quantita;
    }

    public double calcolaTotale() {
        return prodotto.getPrezzo() * quantita;
    }
}
