package unibg.it.Supermercato.sistemaaccesso;

import java.time.LocalDate;
import unibg.it.Supermercato.selfscanning.SessioneSpesa;
import unibg.it.Supermercato.selfscanning.CarrelloFisico;
import unibg.it.Supermercato.selfscanning.Segnalazione;
import unibg.it.Supermercato.persistenzadati.IRepositoryClienti;
import unibg.it.Supermercato.selfscanning.IControlloSpesa;

public class Cassiere extends Dipendente {
	
	private IRepositoryClienti repositoryClienti;
	private IControlloSpesa controlloSpesa;
	private int numeroTotemControllo;

	public Cassiere(String nome, String cognome, String email, String password, LocalDate dataNascita, 
			IRepositoryClienti repositoryClienti, IControlloSpesa controlloSpesa) {
		super(nome, cognome, email, password, dataNascita);
		this.repositoryClienti = repositoryClienti;
		this.controlloSpesa = controlloSpesa;
	}

	public boolean haRichiestaDiControllo(SessioneSpesa s) {
	    return s != null && s.richiedeControllo();
	}
	
	public boolean notificaRichiestaDiControllo(SessioneSpesa s) {
	    if (s == null || !s.richiedeControllo() || s.getTotem() == null) {
	        return false;
	    }

	    numeroTotemControllo = s.getTotem().getNumero();

	    return true;
	}
	
    public boolean effettuaControllo(SessioneSpesa s, CarrelloFisico carrelloFisico) {
    	
    	if (s == null || !s.isInControllo()) {
    	    return false;
    	}

    	boolean risultato = controlloSpesa.verificaCorrispondenza(s, carrelloFisico);

        if (!risultato) {
            s.segnala();
            
            if (s.getCliente() != null) {
                s.getCliente().segnala();
            }
            
            Segnalazione segnalazione = new Segnalazione(s);
            repositoryClienti.registraSegnalazione(segnalazione);
        } else {
            s.completaDopoControllo();
        }

        return risultato;
    }

    public int getNumeroTotemControllo() {
        return numeroTotemControllo;
    }
    
    @Override
    public String getRuolo() {
        return "Cassiere";
    }
}
