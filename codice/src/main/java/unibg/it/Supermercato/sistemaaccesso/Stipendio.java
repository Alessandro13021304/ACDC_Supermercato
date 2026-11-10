/**
 * @author Alessandro Frassi
 */

package unibg.it.Supermercato.sistemaaccesso;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;

public class Stipendio {

    private Dipendente dipendente;
    private List<Presenza> presenze;
    private double importo;

    public Stipendio(Dipendente dipendente) {
        this.dipendente = dipendente;
        this.presenze = new ArrayList<>();
        this.importo = 0.0;
    }

    public void aggiungiPresenza(Presenza presenza) {
        if (presenza != null) {
            presenze.add(presenza);
        }
    }
    
    /** questo metodo associa le presenze ai turni
	 * @param il dipendente di cui si devono trovare i turni
	 */
    public void aggiungiPresenzeDaTurni(Dipendente dipendente) {
        if (dipendente == null) {
            return;
        }

        for (Turno turno : dipendente.visualizzaTurni()) {
            if (turno != null && turno.isCompletato()) {
            	aggiungiPresenza(new Presenza(turno));
            }
        }
    }

    /** questo metodo ritorna lo stipendio orario dei vari ruoli
	 * @return lo stipendio orario
	 */
    private double getTariffaOraria() {
        if (dipendente instanceof unibg.it.Supermercato.sistemaaccesso.Cassiere) {
            return 10.0;
        }

        if (dipendente instanceof unibg.it.Supermercato.sistemaaccesso.Magazziniere) {
            return 11.0;
        }

        if (dipendente instanceof unibg.it.Supermercato.sistemaaccesso.ResponsabileDiReparto) {
            return 14.0;
        }

        if (dipendente instanceof unibg.it.Supermercato.sistemaaccesso.Manager) {
            return 16.0;
        }

        return 0.0;
    }
    
    /** questo metodo calcola lo stipendio dei dipendenti (ore lavorate * tariffa oraria)
	 * @return lo stipendio calcolato
	 */
    public double calcolaStipendio() {
        double oreTotali = 0.0;

        for (Presenza presenza : presenze) {
            oreTotali += presenza.calcolaOreLavorate();
        }

        importo = oreTotali * getTariffaOraria();
        return importo;
    }
    
    /** questo metodo calcola lo stipendio mensile dei dipendenti (ore lavorate in quel mese * tariffa oraria)
     * @param il mese e l'anno in cui calcolare lo stipendio 
	 * @return lo stipendio mensile calcolato
	 */
    public double calcolaStipendioMensile(int anno, int mese) {
        double oreTotali = 0.0;

        LocalDate primoGiorno = LocalDate.of(anno, mese, 1);
        LocalDate ultimoGiorno = primoGiorno.withDayOfMonth(primoGiorno.lengthOfMonth());

        for (Presenza presenza : presenze) {
            Turno turno = presenza.getTurno();

            if (turno != null) {
                LocalDate dataTurno = turno.getData();

                if (dataTurno != null && !dataTurno.isBefore(primoGiorno) && !dataTurno.isAfter(ultimoGiorno)) {

                    oreTotali += presenza.calcolaOreLavorate();
                }
            }
        }

        importo = oreTotali * getTariffaOraria();
        return importo;
    }
    
    public List<Presenza> getPresenze() {
        return presenze;
    }

    public Dipendente getDipendente() {
        return dipendente;
    }
    
    public double getImporto() {
        return importo;
    }
}

