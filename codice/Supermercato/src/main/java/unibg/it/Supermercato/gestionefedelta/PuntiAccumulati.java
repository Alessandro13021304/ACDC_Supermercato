package unibg.it.Supermercato.gestionefedelta;

public class PuntiAccumulati {
	
	public int calcolaPunti(double totaleSpesa) {
        if (totaleSpesa <= 0) {
            return 0;
        }

        return (int) (totaleSpesa / 2);
    }
}
