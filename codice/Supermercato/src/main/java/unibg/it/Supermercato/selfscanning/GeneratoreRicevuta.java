package unibg.it.Supermercato.selfscanning;

public class GeneratoreRicevuta {

    public String genera(SessioneSpesa sessione, Pagamento pagamento) {

        if (sessione == null || pagamento == null) {
            return "";
        }

        StringBuilder ricevuta = new StringBuilder();

        ricevuta.append("===== RICEVUTA =====\n");
        ricevuta.append(String.format("Totale: %.2f €%n", pagamento.getImporto()));
        ricevuta.append("Pagamento: ");
        ricevuta.append(pagamento.getModalita());
        ricevuta.append("\n");
        ricevuta.append("Data: ");
        ricevuta.append(pagamento.getDataOra());
        ricevuta.append("\n");
        ricevuta.append("====================");

        return ricevuta.toString();
    }
}