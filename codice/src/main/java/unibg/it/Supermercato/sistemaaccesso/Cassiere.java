/**
 * @author Alessandro Frassi
 */

package unibg.it.Supermercato.sistemaaccesso;

import java.time.LocalDate;

public class Cassiere extends Dipendente {

    private IRepositoryClienti repositoryClienti;
    private IControlloSpesa controlloSpesa;
    private int numeroTotemControllo;

    public Cassiere(String nome, String cognome, String email, String password, LocalDate dataNascita, IRepositoryClienti repositoryClienti, IControlloSpesa controlloSpesa) {
        super(nome, cognome, email, password, dataNascita);
        this.repositoryClienti = repositoryClienti;
        this.controlloSpesa = controlloSpesa;
    }

    public void setControlloSpesa(IControlloSpesa controlloSpesa) {
        this.controlloSpesa = controlloSpesa;
    }

    public boolean haRichiestaDiControllo(ISessioneControllabile s) {
        return s != null && s.richiedeControllo();
    }

    /** questo metodo notifica il cassiere per una richiesta di controllo
	 * @param la sessione spesa del cliente e il numero del totem dove effettuare il controllo
	 * @return true se va a buon fine, altrimenti false
	 */
    public boolean notificaRichiestaDiControllo(ISessioneControllabile s, int numeroTotem) {
        if (s == null || !s.richiedeControllo()) {
            return false;
        }

        numeroTotemControllo = numeroTotem;

        return true;
    }

    /** questo metodo effettua il controllo della spesa
	 * @param la sessione spesa del cliente e il carrello fisico
	 * @return true se il controllo è superato, altrimenti false
	 */
    public boolean effettuaControllo(ISessioneControllabile s, ICarrelloFisico carrelloFisico) {
        if (s == null || !s.isInControllo() || controlloSpesa == null) {
            return false;
        }

        boolean risultato = controlloSpesa.verificaCorrispondenza(s, carrelloFisico);

        if (risultato) {
            s.completaDopoControllo();
        } else {
            registraSegnalazione(s);
        }

        return risultato;
    }

    /** questo metodo registra una segnalazione del cliente e la salva in persistenza
	 * @param la sessione spesa del cliente segnalato
	 */
    private void registraSegnalazione(ISessioneControllabile s) {
        s.segnala();

        Cliente cliente = s.getCliente();

        if (cliente != null) {
            cliente.segnala();
            repositoryClienti.registraSegnalazione(cliente.getId(), s.getDataOra());
        }
    }

    public int getNumeroTotemControllo() {
        return numeroTotemControllo;
    }

    @Override
    public String getRuolo() {
        return "Cassiere";
    }
}
