/**
 * @author Alessandro Frassi
 */

package unibg.it.Supermercato.gestionedipendenti;

import unibg.it.Supermercato.sistemaaccesso.Dipendente;
import unibg.it.Supermercato.sistemaaccesso.IStipendi;
import unibg.it.Supermercato.sistemaaccesso.Stipendio;

public class CalcoloStipendi implements IStipendi {

	/** questo metodo calcola gli stipendi 
	 * @param il dipendente di cui calcolare lo stipendio
	 * @return l'oggetto stipendio
	 */
	@Override
    public Stipendio calcolaStipendio(Dipendente dipendente) {
        if (dipendente == null) {
            return null;
        }

        Stipendio stipendio = new Stipendio(dipendente);
        stipendio.aggiungiPresenzeDaTurni(dipendente);
        stipendio.calcolaStipendio();

        return stipendio;
    }

	/** questo metodo calcola gli stipendi del mese in considerazione 
	 * @param il dipendente di cui calcolare lo stipendio, l'anno e il mese in considerazione
	 * @return l'oggetto stipendio
	 */
    @Override
    public Stipendio calcolaStipendioMensile(Dipendente dipendente, int anno, int mese) {
        if (dipendente == null) {
            return null;
        }

        Stipendio stipendio = new Stipendio(dipendente);
        stipendio.aggiungiPresenzeDaTurni(dipendente);
        stipendio.calcolaStipendioMensile(anno, mese);

        return stipendio;
    }
}

