package unibg.it.Supermercato.selfscanning;

import java.util.Map;

import unibg.it.Supermercato.gestionemagazzino.Prodotto;
import unibg.it.Supermercato.sistemaaccesso.ICarrelloFisico;
import unibg.it.Supermercato.sistemaaccesso.IControlloSpesa;
import unibg.it.Supermercato.sistemaaccesso.ISessioneControllabile;

public class ControlloSpesa implements IControlloSpesa {

    @Override
    public boolean verificaCorrispondenza(ISessioneControllabile sessione, ICarrelloFisico carrelloFisico) {
        if (sessione == null || carrelloFisico == null) {
            return false;
        }

        Map<Prodotto, Integer> contenuto = sessione.getContenutoCarrello();

        if (contenuto.size() != carrelloFisico.numeroElementi()) {
            return false;
        }

        for (Map.Entry<Prodotto, Integer> voce : contenuto.entrySet()) {
            if (!carrelloFisico.contieneElemento(voce.getKey(), voce.getValue())) {
                return false;
            }
        }

        return true;
    }
}