package unibg.it.Supermercato.gestionemagazzino;

public interface IScorte {
	
void aggiornaScorte(Prodotto prodotto, TipoScorta tipoScorta);
	
	void aggiungiObserver(ScorteObserver observer);
	
	void rimuoviObserver(ScorteObserver observer);

}
