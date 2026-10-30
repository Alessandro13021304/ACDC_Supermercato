package unibg.it.Supermercato.sistemaaccesso;

import java.time.Duration;
import java.time.LocalTime;

public class Presenza {

    private Turno turno;

    public Presenza(Turno turno) {
        this.turno = turno;
    }

    public double calcolaOreLavorate() {
        if (turno == null || !turno.presenzaIniziata() || !turno.isCompletato()) {
            return 0.0;
        }

        LocalTime ingresso = turno.getOraIngresso();
        LocalTime uscita = turno.getOraUscita();

        if (ingresso == null || uscita == null) {
            return 0.0;
        }

        return Duration.between(ingresso, uscita).toMinutes() / 60.0;
    }

    public Turno getTurno() {
        return turno;
    }
}
