package unibg.it.Supermercato.gestionefedelta;

public class Promozione {
	
	private static final int PUNTI_MINIMI = 100;
    private static final double SCONTO_MASSIMO = 30.0;

    public double calcolaSconto(int puntiFedelta) {
        if (puntiFedelta < PUNTI_MINIMI) {
            return 0.0;
        }

        return Math.min(puntiFedelta / 20.0, SCONTO_MASSIMO);
    }

    public double applicaSconto(double totale, double percentuale) {
        if (totale <= 0 || percentuale <= 0) {
            return totale;
        }

        return totale * (1 - percentuale / 100.0);
    }

    public int calcolaPuntiDaScalare(int puntiFedelta) {

        if (puntiFedelta < PUNTI_MINIMI) {
            return 0;
        }

        return Math.min(puntiFedelta, 600);
    }
}
