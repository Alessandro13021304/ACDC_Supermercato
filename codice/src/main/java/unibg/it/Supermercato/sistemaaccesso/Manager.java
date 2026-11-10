/**
 * @author Alessandro Frassi
 */

package unibg.it.Supermercato.sistemaaccesso;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Manager extends Dipendente {

    private ITurni gestoreTurni;
    private IStipendi calcoloStipendi;

    public Manager(String nome, String cognome, String email, String password, LocalDate dataNascita, IRepositoryDipendenti repositoryDipendenti) {
        super(nome, cognome, email, password, dataNascita);
        setRepositoryDipendenti(repositoryDipendenti);
    }

    public Manager(String nome, String cognome, String email, String password, LocalDate dataNascita, IRepositoryDipendenti repositoryDipendenti, ITurni gestoreTurni, IStipendi calcoloStipendi) {
        this(nome, cognome, email, password, dataNascita, repositoryDipendenti);
        this.gestoreTurni = gestoreTurni;
        this.calcoloStipendi = calcoloStipendi;
    }

    public void setGestoreTurni(ITurni gestoreTurni) {
        this.gestoreTurni = gestoreTurni;
    }

    public void setCalcoloStipendi(IStipendi calcoloStipendi) {
        this.calcoloStipendi = calcoloStipendi;
    }

    /** questo metodo assegna un turno a un dipendente
     * @param il dipendente e il turno da assegnargli
	 * @return true se il turno è stato assegnato, altrimenti false
	 */
    public boolean assegnaTurno(Dipendente dipendente, Turno turno) {
        return gestoreTurni != null && gestoreTurni.assegnaTurno(dipendente, turno);
    }

    /** questo metodo permette di calcolare gli stipendi mensili dei dipendenti
     * @param la lista dei dipendenti, l'anno e il mese in cui vengono calcolati gli stipendi
	 * @return la lista degli stipendi
	 */
    public List<Stipendio> calcolaStipendi(List<Dipendente> dipendenti, int anno, int mese) {
        List<Stipendio> stipendi = new ArrayList<>();

        if (dipendenti == null || calcoloStipendi == null) {
            return stipendi;
        }

        for (Dipendente dipendente : dipendenti) {
            if (dipendente != null) {
                stipendi.add(calcoloStipendi.calcolaStipendioMensile(dipendente, anno, mese));
            }
        }

        return stipendi;
    }

    @Override
    public String getRuolo() {
        return "Manager";
    }
}
