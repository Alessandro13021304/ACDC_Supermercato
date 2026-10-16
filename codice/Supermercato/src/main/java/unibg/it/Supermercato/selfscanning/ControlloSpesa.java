package unibg.it.Supermercato.selfscanning;

public class ControlloSpesa implements IControlloSpesa {

    @Override
	public boolean verificaCorrispondenza(SessioneSpesa sessione, CarrelloFisico carrelloFisico) {
    	if (sessione == null || carrelloFisico == null) {
            return false;
        }

        Carrello carrello = sessione.carrello;

        if (carrello.numeroElementi() != carrelloFisico.numeroElementi()) {
            return false;
        }

        for (ElementoCarrello elemento : carrello.getElementi()) {
            if (!carrelloFisico.contieneElemento(elemento.prodotto, elemento.getQuantita())) {
                return false;
            }
        }

        return true;
    }
}