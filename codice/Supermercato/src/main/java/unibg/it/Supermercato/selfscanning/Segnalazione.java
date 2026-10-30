package unibg.it.Supermercato.selfscanning;

import java.time.LocalDateTime;

import unibg.it.Supermercato.sistemaaccesso.Cliente;

public class Segnalazione {

    private SessioneSpesa sessione;
    private Cliente cliente;
    private LocalDateTime dataOra;

    // Costruttore utilizzato quando viene creata una nuova segnalazione
    public Segnalazione(SessioneSpesa sessione) {
        this.sessione = sessione;

        if (sessione != null) {
            this.cliente = sessione.getCliente();
            this.dataOra = sessione.getDataOra();
        }
    }

    public Segnalazione(Cliente cliente, LocalDateTime dataOra) {
        this.cliente = cliente;
        this.dataOra = dataOra;
        this.sessione = null;
    }

    public SessioneSpesa getSessione() {
        return sessione;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public LocalDateTime getDataOra() {
        return dataOra;
    }
}