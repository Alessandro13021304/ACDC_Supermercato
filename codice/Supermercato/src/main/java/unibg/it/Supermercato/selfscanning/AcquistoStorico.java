package unibg.it.Supermercato.selfscanning;

import java.time.LocalDateTime;

public class AcquistoStorico {

    private LocalDateTime data;
    private double totale;

    public AcquistoStorico(LocalDateTime data, double totale) {
        this.data = data;
        this.totale = totale;
    }

    public LocalDateTime getData() {
        return data;
    }

    public double getTotale() {
        return totale;
    }
}