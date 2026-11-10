/**
 * @author Chiara Dentella
 */

package unibg.it.Supermercato.sistemaaccesso;

import java.time.LocalDateTime;
import java.util.List;

public interface IRepositoryClienti {

    void salvaCliente(Cliente cliente);

    Cliente verificaCredenziali(String email, String password);

    Cliente trovaClienteById(int clienteId);

    List<Cliente> trovaTuttiIClienti();

    int trovaPuntiByCliente(int clienteId);

    void aggiungiPunti(int clienteId, int punti);

    void scalaPunti(int clienteId, int punti);

    void salvaStorico(int clienteId, LocalDateTime data, double totale);

    List<AcquistoStorico> trovaStoricoByCliente(int clienteId);

    void registraSegnalazione(int clienteId, LocalDateTime data);

    List<LocalDateTime> trovaSegnalazioniByCliente(int clienteId);

    boolean isClienteSegnalato(int clienteId);
}