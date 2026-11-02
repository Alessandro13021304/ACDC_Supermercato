/**
 * @author Davide Anthony Carmona Contreras
 */

package unibg.it.Supermercato.selfscanning;

public class CalcolatoreSconto {

    public double applicaSconto(double prezzo, double sconto) {
        return prezzo - (prezzo * sconto / 100);
    }
}