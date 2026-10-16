package unibg.it.Supermercato.selfscanning;

public class Segnalazione {

    private SessioneSpesa sessione;

    public Segnalazione(SessioneSpesa sessione) {
        this.sessione = sessione;
    }

    public SessioneSpesa getSessione() {
        return sessione;
    }
}