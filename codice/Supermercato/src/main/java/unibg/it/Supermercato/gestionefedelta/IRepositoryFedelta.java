package unibg.it.Supermercato.gestionefedelta;

import unibg.it.Supermercato.selfscanning.SessioneSpesa;

public interface IRepositoryFedelta {

    int trovaPuntiByCliente(int clienteId);

    void aggiornaPunti(int clienteId, int punti);

    void salvaStorico(int clienteId, SessioneSpesa sessione);
}