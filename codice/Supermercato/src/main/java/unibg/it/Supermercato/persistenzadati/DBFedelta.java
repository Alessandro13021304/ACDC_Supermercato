package unibg.it.Supermercato.persistenzadati;

import unibg.it.Supermercato.gestionefedelta.IRepositoryFedelta;
import unibg.it.Supermercato.selfscanning.SessioneSpesa;

public class DBFedelta implements IRepositoryFedelta {

    @Override
    public int trovaPuntiByCliente(int clienteId) {
        return clienteId;
    }

    @Override
    public void aggiornaPunti(int clienteId, int punti) {
    }

    @Override
    public void salvaStorico(int clienteId, SessioneSpesa sessione) {
    }
}
