package unibg.it.Supermercato.sistemaaccesso;

import java.time.LocalDateTime;
import java.util.Map;

import unibg.it.Supermercato.gestionemagazzino.Prodotto;

public interface ISessioneControllabile {

    boolean richiedeControllo();

    boolean isInControllo();

    void segnala();

    void completaDopoControllo();

    Cliente getCliente();

    LocalDateTime getDataOra();

    Map<Prodotto, Integer> getContenutoCarrello();
}
