/**
 * @author Alessandro Frassi
 */

package unibg.it.Supermercato.sistemaaccesso;

public interface IStipendi {

    Stipendio calcolaStipendio(Dipendente dipendente);

    Stipendio calcolaStipendioMensile(Dipendente dipendente, int anno, int mese);
}