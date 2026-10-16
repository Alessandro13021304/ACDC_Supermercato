package unibg.it.Supermercato.selfscanning;

public interface IControlloSpesa {

    boolean verificaCorrispondenza(SessioneSpesa sessione, CarrelloFisico carrelloFisico);

}