package unibg.it.Supermercato.selfscanning;

import java.time.LocalDateTime;

import unibg.it.Supermercato.gestionemagazzino.Prodotto;

public class SessioneSpesa {

    private LocalDateTime dataOra;
    private EStatoSessione stato;

    public Carrello carrello;
    public Pagamento pagamento;

    public void scansionaProdotto(Prodotto p) {
    }

    public void rimuoviProdotto(Prodotto p) {
    }

    public void terminaSpesa() {
    }
}
