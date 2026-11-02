/**
 * @author Chiara Tremolada
 */

package unibg.it.Supermercato.pagamentiesterni;

public class PaymentGateway implements IPagamento{
	
	/** questo metodo verifica che il pagamento sia maggiore di 0
	 * @param l'importo da pagare
	 * @return true se > 0, altrimenti false
	 */
	@Override
    public boolean paga(double importo) {
        return importo > 0;
    }
}