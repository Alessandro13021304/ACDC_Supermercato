package unibg.it.Supermercato.pagamentiesterni;

public class Transazione {
	
	private double importo;
    private boolean completata;

    public Transazione(double importo) {
        this.importo = importo;
        this.completata = false;
    }

    public double getImporto() {
        return importo;
    }

    public boolean isCompletata() {
        return completata;
    }

    public void completa() {
        if (importo > 0) {
            completata = true;
        }
    }
}