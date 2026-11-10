/**
 * @author Davide Anthony Carmona Contreras
 */

package unibg.it.Supermercato.selfscanning;

import java.util.Map;

import unibg.it.Supermercato.gestionemagazzino.Prodotto;
import unibg.it.Supermercato.sistemaaccesso.ICarrelloFisico;
import unibg.it.Supermercato.sistemaaccesso.IControlloSpesa;
import unibg.it.Supermercato.sistemaaccesso.ISessioneControllabile;

public class ControlloSpesa implements IControlloSpesa {

	/** questo metodo verifica se il carrello e il carrello fisico corrispondono
	 * @param la sessione spesa del cliente e il carrello fisico
	 * @return true se corrispondono, altrimenti false
	 */
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