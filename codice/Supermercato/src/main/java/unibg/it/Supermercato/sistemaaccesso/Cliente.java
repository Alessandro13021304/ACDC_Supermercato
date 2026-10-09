package unibg.it.Supermercato.sistemaaccesso;

import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

import unibg.it.Supermercato.persistenzadati.IRepositoryClienti;
import unibg.it.Supermercato.selfscanning.AcquistoStorico;


public class Cliente extends Persona implements RuoloUtente {

	private int id;
	private int puntiFedelta;
    private boolean isSegnalato;
    private IRepositoryClienti repositoryClienti;

    public Cliente(String nome, String cognome, String email, String password, LocalDate dataNascita) {
        super(nome, cognome, email, password, dataNascita);
    }
    
    public void setRepositoryClienti(IRepositoryClienti repositoryClienti) {
        this.repositoryClienti = repositoryClienti;
    }

    public int visualizzaPunti() {
        if (repositoryClienti != null && id > 0) {
            return repositoryClienti.trovaPuntiByCliente(id);
        }

        return puntiFedelta;
    }
    
    public List<AcquistoStorico> visualizzaStorico() {
        if (repositoryClienti != null && id > 0) {
            return repositoryClienti.trovaStoricoByCliente(id);
        }

        return new ArrayList<>();
    }

    public void setPuntiFedelta(int puntiFedelta) {
        this.puntiFedelta = puntiFedelta;
    }

    
    @Override
    public String getRuolo() {
        return "Cliente";
    }
    
    public boolean isSegnalato() {
        return isSegnalato;
    }
    
    public void segnala() {
        isSegnalato = true;
    }
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}