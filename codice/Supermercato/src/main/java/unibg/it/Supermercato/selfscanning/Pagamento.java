package unibg.it.Supermercato.selfscanning;

import java.time.LocalDateTime;

public class Pagamento {

    private double importo;
    private EModalitaPagamento modalita;
    private LocalDateTime dataOra;

    public Pagamento(double importo, EModalitaPagamento modalita) {
        this.importo = importo;
        this.modalita = modalita;
        this.dataOra = LocalDateTime.now();
    }

    public double getImporto() {
        return importo;
    }

    public EModalitaPagamento getModalita() {
        return modalita;
    }

    public LocalDateTime getDataOra() {
        return dataOra;
    }
}