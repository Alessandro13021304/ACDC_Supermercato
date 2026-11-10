/**
 * @author Chiara Tremolada
 */

package unibg.it.Supermercato.gestionefedelta;

public class Promozione {
	
	private static final int PUNTI_MINIMI = 100;
	private static final int PUNTI_MASSIMI = 600;
    private static final double SCONTO_MASSIMO = 30.0;

    /** questo metodo calcola lo sconto di una spesa che un cliente può avere in base ai suoi punti fedeltà
     * logica: punti a disposizione/20 %, il massimo sconto è 30%
	 * @param i punti fedeltà del cliente che fa la spesa
	 * @return lo sconto in percentuale
	 */
    public double calcolaSconto(int puntiFedelta) {
        if (puntiFedelta < PUNTI_MINIMI) {
            return 0.0;
        }

        return Math.min(puntiFedelta / 20.0, SCONTO_MASSIMO);
    }

    /** questo metodo calcola il totale della spesa in base allo sconto che si ha 
	 * @param il totale della spesa e lo sconto a disposizione
	 * @return il totale scontato
	 */
    public double applicaSconto(double totale, double percentuale) {
        if (totale <= 0 || percentuale <= 0) {
            return totale;
        }

        return totale * (1 - percentuale / 100.0);
    }

    /** questo metodo calcola i punti da scalare dopo aver usufruito dello sconto 
	 * @param i punti usati per usufruire dello sconto
	 * @return i punti giusti (se sono meno di 100 non si scalano, se sono più di 600 si scala 600 - sconto massimo 30%)
	 */
    public int calcolaPuntiDaScalare(int puntiFedelta) {

        if (puntiFedelta < PUNTI_MINIMI) {
            return 0;
        }

        return Math.min(puntiFedelta, PUNTI_MASSIMI);
    }
}
