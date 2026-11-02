/**
 * @author Davide Anthony Carmona Contreras
 */

package unibg.it.Supermercato.selfscanning;

import unibg.it.Supermercato.pagamentiesterni.IPagamento;
import unibg.it.Supermercato.sistemaaccesso.Cassiere;

public class Totem {

    private int numero;
    private IPagamento pagamento;
    private Cassiere cassiere;
    private CarrelloFisico carrelloFisico;
    private boolean cassiereArrivato;

    public Totem(int numero, IPagamento pagamento) {
        this.numero = numero;
        this.pagamento = pagamento;
    }

    public void setCassiere(Cassiere cassiere) {
        this.cassiere = cassiere;
    }

    public void setCarrelloFisico(CarrelloFisico carrelloFisico) {
        this.carrelloFisico = carrelloFisico;
    }

    public boolean richiediControllo(SessioneSpesa sessione) {
        if (cassiere == null) {
            return false;
        }

        return cassiere.notificaRichiestaDiControllo(sessione, numero);
    }

    public boolean effettuaPagamento(double importo) {
        if (pagamento == null) {
            return false;
        }

        return pagamento.paga(importo);
    }

    public boolean effettuaControlloSpesa(SessioneSpesa sessione) {
        if (cassiere == null || carrelloFisico == null) {
            return false;
        }

        return cassiere.effettuaControllo(sessione, carrelloFisico);
    }

    public void registraArrivoCassiere(SessioneSpesa sessione) {
        cassiereArrivato = true;

        if (sessione != null) {
            sessione.avviaControllo();
        }
    }

    public void registraArrivoCassiere() {
        cassiereArrivato = true;
    }

    public void registraPartenzaCassiere() {
        cassiereArrivato = false;
    }

    public boolean isCassiereArrivato() {
        return cassiereArrivato;
    }

    public int getNumero() {
        return numero;
    }
}