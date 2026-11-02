/**
 * @author Alessandro Frassi
 */

package unibg.it.Supermercato.sistemaaccesso;

public interface IAutenticazione {

    boolean registraUtente(Persona persona);

    Persona autentica(String email, String password);
}
