package unibg.it.Supermercato.gestionemagazzino;

public class Prodotto {

    private String codiceABarre;
    private String nome;
    private double prezzo;
    private String descrizione;
    private double sconto;
    private int quantitaScaffale;
    private int quantitaMagazzino;
    private int numeroReparto;
    
    public Prodotto(String codiceABarre, String nome, double prezzo, String descrizione, double sconto, int quantitaScaffale,  int quantitaMagazzino, int numeroReparto) {
        this.codiceABarre = codiceABarre;
        this.nome = nome;
        this.prezzo = prezzo;
        this.descrizione = descrizione;
        this.sconto = sconto;
        this.quantitaScaffale = quantitaScaffale;
        this.quantitaMagazzino = quantitaMagazzino;
        this.numeroReparto = numeroReparto;
    }
    
    public double getPrezzo() {
        return prezzo;
    }
    
    public double getSconto() {
        return sconto;
    }
    
    public String getCodiceABarre() {
        return codiceABarre;
    }

    public String getNome() {
        return nome;
    }

    public String getDescrizione() {
        return descrizione;
    }
    
    public int getQuantitaScaffale() {
        return quantitaScaffale;
    }

    public int getQuantitaMagazzino() {
        return quantitaMagazzino;
    }

    public void setQuantitaScaffale(int quantitaScaffale) {
        this.quantitaScaffale = quantitaScaffale;
    }

    public void setQuantitaMagazzino(int quantitaMagazzino) {
        this.quantitaMagazzino = quantitaMagazzino;
    }
    
    public int getNumeroReparto() {
        return numeroReparto;
    }
}