package unibg.it.Supermercato.selfscanning;

import java.time.LocalDateTime;

public class Pagamento {

    private double importo;
    private EModalitaPagamento modalita;
    private LocalDateTime dataOra;

    public boolean processa() {
        return false;
    }
}