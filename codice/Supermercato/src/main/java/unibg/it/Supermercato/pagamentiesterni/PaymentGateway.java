package unibg.it.Supermercato.pagamentiesterni;

public class PaymentGateway implements IPagamento{
	
	@Override
    public boolean paga(double importo) {
        return importo > 0;
    }
}