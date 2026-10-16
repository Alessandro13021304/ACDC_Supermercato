package unibg.it.Supermercato.sistemaaccesso;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import unibg.it.Supermercato.gestionedipendenti.Stipendio;
import unibg.it.Supermercato.gestionedipendenti.Turno;
import unibg.it.Supermercato.gestionedipendenti.ITurni;
import unibg.it.Supermercato.gestionedipendenti.GestoreTurni;
import unibg.it.Supermercato.gestionedipendenti.IStipendi;
import unibg.it.Supermercato.gestionedipendenti.CalcoloStipendi;
import unibg.it.Supermercato.persistenzadati.IRepositoryDipendenti;

public class Manager extends Dipendente {

	private ITurni gestoreTurni;
	private IStipendi calcoloStipendi;
	
	public Manager(String nome, String cognome, String email, String password, LocalDate dataNascita, IRepositoryDipendenti repositoryDipendenti) {
	    super(nome, cognome, email, password, dataNascita);

	    this.setRepositoryDipendenti(repositoryDipendenti);
	    this.gestoreTurni = new GestoreTurni(repositoryDipendenti);
	    this.calcoloStipendi = new CalcoloStipendi();
	}

	public void assegnaTurno(Dipendente dipendente, Turno turno) {
	    gestoreTurni.assegnaTurno(dipendente, turno);
	}

	public List<Stipendio> calcolaStipendi(List<Dipendente> dipendenti, int anno, int mese) {

	    List<Stipendio> stipendi = new ArrayList<>();

	    if (dipendenti == null) {
	        return stipendi;
	    }

	    for (Dipendente dipendente : dipendenti) {

	        if (dipendente == null) {
	            continue;
	        }

	        Stipendio stipendio = calcoloStipendi.calcolaStipendioMensile(dipendente, anno, mese);

	        stipendi.add(stipendio);
	    }

	    return stipendi;
	}

    @Override
    public String getRuolo() {
        return "Manager";
    }
}
