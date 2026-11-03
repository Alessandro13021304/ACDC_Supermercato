/**
 * @author Alessandro Frassi
 */

package unibg.it.Supermercato.sistemaaccesso;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Cliente extends Persona implements IRuoloUtente {

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

    /** questo metodo permette di visualizzare i punti fedeltà del cliente
	 * @return il numero di punti fedeltà
	 */
    public int visualizzaPunti() {
        if (repositoryClienti != null && id > 0) {
            return repositoryClienti.trovaPuntiByCliente(id);
        }

        return puntiFedelta;
    }

    /** questo metodo permette di visualizzare gli storici spese del cliente
	 * @return la lista degli storici spese
	 */
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