/**
 * @author Alessandro Frassi
 */

package unibg.it.Supermercato.sistemaaccesso;

public class AuthService implements IAutenticazione {

    private final IRepositoryClienti repositoryClienti;
    private final IRepositoryDipendenti repositoryDipendenti;

    public AuthService(IRepositoryClienti repositoryClienti, IRepositoryDipendenti repositoryDipendenti) {
        this.repositoryClienti = repositoryClienti;
        this.repositoryDipendenti = repositoryDipendenti;
    }

    /** questo metodo registra un cliente/dipendente nel sistema e lo salva in persistenza
     * @param la persona da registrare
     * @return true se va a buon fine, altrimenti false
	 */
    @Override
    public boolean registraUtente(Persona persona) {

        if (persona == null || persona.getEmail() == null) {
            return false;
        }

        if (persona instanceof Cliente cliente) {
            try {
                repositoryClienti.salvaCliente(cliente);
                return true;
            } catch (RuntimeException e) {
                return false;
            }
        }

        if (persona instanceof Dipendente dipendente) {
            try {
                repositoryDipendenti.salvaDipendente(dipendente);
                return true;
            } catch (RuntimeException e) {
                return false;
            }
        }

        return false;
    }

    /** questo metodo autentica un cliente/dipendente
     * @param la mail e la password della persona che si sta autenticando
     * @return la persona autenticata
	 */
    @Override
    public Persona autentica(String email, String password) {

        if (email == null || password == null) {
            return null;
        }

        Persona persona = repositoryClienti.verificaCredenziali(email, password);

        if (persona != null) {
            return persona;
        }

        return repositoryDipendenti.verificaCredenziali(email, password);
    }
}