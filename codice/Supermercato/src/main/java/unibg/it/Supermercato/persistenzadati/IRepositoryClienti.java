package unibg.it.Supermercato.persistenzadati;

import java.util.List;

import unibg.it.Supermercato.selfscanning.Segnalazione;
import unibg.it.Supermercato.selfscanning.SessioneSpesa;
import unibg.it.Supermercato.sistemaaccesso.Cliente;
import unibg.it.Supermercato.selfscanning.AcquistoStorico;

public interface IRepositoryClienti {

    void salvaCliente(Cliente cliente);

    Cliente verificaCredenziali(String email, String password);

    Cliente trovaClienteById(int clienteId);

    List<Cliente> trovaTuttiIClienti();
    
    int trovaPuntiByCliente(int clienteId);

    void aggiungiPunti(int clienteId, int punti);

    void scalaPunti(int clienteId, int punti);

    void salvaStorico(int clienteId, SessioneSpesa sessione);
    
    List<AcquistoStorico> trovaStoricoByCliente(int clienteId);

    void registraSegnalazione(Segnalazione segnalazione);
    
    boolean isClienteSegnalato(int clienteId);
}
