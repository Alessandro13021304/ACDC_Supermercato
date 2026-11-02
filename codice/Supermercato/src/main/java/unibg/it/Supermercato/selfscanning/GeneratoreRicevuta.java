/**
 * @author Davide Anthony Carmona Contreras
 */

package unibg.it.Supermercato.selfscanning;

import java.util.Locale;

public class GeneratoreRicevuta {

    public String genera(SessioneSpesa sessione, Pagamento pagamento) {

        if (sessione == null || pagamento == null) {
            return "";
        }

        StringBuilder ricevuta = new StringBuilder();

        ricevuta.append("===== RICEVUTA =====\n");
        ricevuta.append(String.format(Locale.ITALY, "Totale: € %.2f%n", pagamento.getImporto()));
        ricevuta.append("Pagamento: ");
        ricevuta.append(pagamento.getModalita());
        ricevuta.append("\n");
        ricevuta.append("Data: ");
        ricevuta.append(sessione.getDataOra().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) + "\n");
        ricevuta.append("\n");
        ricevuta.append("====================");

        return ricevuta.toString();
    }
}