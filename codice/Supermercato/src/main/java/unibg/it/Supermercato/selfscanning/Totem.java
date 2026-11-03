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

    public boolean isCassiereDisponibile() {
        return cassiere != null;
    }

    /** questo metodo manda una notifica al cassiere se è necessario un controllo
	 * @param la sessione spesa da controllare
	 * @return true se va a buon fine, altrimenti false
	 */
    public boolean richiediControllo(SessioneSpesa sessione) {
        if (cassiere == null) {
            return false;
        }

        return cassiere.notificaRichiestaDiControllo(sessione, numero);
    }

    /** questo metodo permette di pagare il totale
	 * @param l'importo da pagare
	 * @return true se va a buon fine, altrimenti false
	 */
    public boolean effettuaPagamento(double importo) {
        if (pagamento == null) {
            return false;
        }

        return pagamento.paga(importo);
    }

    /** questo metodo effettua il controllo della spesa
	 * @param la sessione spesa in controllo
	 * @return true se il controllo passa, altrimenti false
	 */
    public boolean effettuaControlloSpesa(SessioneSpesa sessione) {
        if (cassiere == null || carrelloFisico == null) {
            return false;
        }

        boolean esito = cassiere.effettuaControllo(sessione, carrelloFisico);

        registraPartenzaCassiere();

        return esito;
    }

    /** questo metodo registra l'arrivo del cassiere al totem
	 * @param la sessione spesa da controllare
	 */
    public void registraArrivoCassiere(SessioneSpesa sessione) {
        cassiereArrivato = true;

        if (sessione != null) {
            sessione.avviaControllo();
        }
    }

    /** questo metodo registra l'arrivo del cassiere al totem senza che debba fare un controllo
	 */
    public void registraArrivoCassiere() {
        cassiereArrivato = true;
    }

    /** questo metodo registra la partenza del cassiere dal totem
	 */
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
