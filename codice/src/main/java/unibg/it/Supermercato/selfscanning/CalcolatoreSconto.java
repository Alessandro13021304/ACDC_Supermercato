/**
 * @author Davide Anthony Carmona Contreras
 */

package unibg.it.Supermercato.selfscanning;

public class CalcolatoreSconto {

	/** questo metodo calcola l'importo scontato
	 * @param il totale e lo sconto da applicargli
	 * @return l'importo scontato
	 */
	public double applicaSconto(double prezzo, double sconto) {
        return prezzo - (prezzo * sconto / 100);
    }
}