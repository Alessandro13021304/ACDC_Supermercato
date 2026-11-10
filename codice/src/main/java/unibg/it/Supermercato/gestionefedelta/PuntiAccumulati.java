/**
 * @author Chiara Tremolada
 */

package unibg.it.Supermercato.gestionefedelta;

public class PuntiAccumulati {
	
	/** questo metodo calcola i punti che si accumulano dopo una spesa
	 * logica: euro pagati/2
	 * @param il totale pagato della spesa
	 * @return i punti accumulati
	 */
	public int calcolaPunti(double totaleSpesa) {
        if (totaleSpesa <= 0) {
            return 0;
        }

        return (int) (totaleSpesa / 2);
    }
}
