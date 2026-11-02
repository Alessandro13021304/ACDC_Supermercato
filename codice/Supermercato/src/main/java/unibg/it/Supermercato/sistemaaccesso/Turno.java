/**
 * @author Alessandro Frassi
 */

package unibg.it.Supermercato.sistemaaccesso;

import java.time.LocalDate;
import java.time.LocalTime;

public class Turno {

    private LocalDate data;
    private LocalTime orarioInizio;
    private LocalTime orarioFine;
    private LocalTime oraIngresso;
    private LocalTime oraUscita;
    private boolean presenzaRegistrata;
    private boolean completato;

    public Turno(LocalDate data, LocalTime orarioInizio, LocalTime orarioFine) {
        this.data = data;
        this.orarioInizio = orarioInizio;
        this.orarioFine = orarioFine;
        this.presenzaRegistrata = false;
        this.completato = false;
    }

    public void registraInizio() {
        if (!presenzaRegistrata) {
            oraIngresso = LocalTime.now();
            presenzaRegistrata = true;
        }
    }

    public void registraFine() {
        if (presenzaRegistrata && !completato) {
            oraUscita = LocalTime.now();
            completato = true;
        }
    }

    public boolean appartieneAllaData(LocalDate data) {
        return this.data != null && this.data.equals(data);
    }

    public boolean isCompletato() {
        return completato;
    }

    public LocalTime getOrarioInizio() {
        return orarioInizio;
    }
    
    public LocalTime getOrarioFine() {
        return orarioFine;
    }

    public boolean presenzaIniziata() {
        return presenzaRegistrata;
    }
    
    public LocalTime getOraIngresso() {
        return oraIngresso;
    }

    public LocalTime getOraUscita() {
        return oraUscita;
    }
    
    public LocalDate getData() {
        return data;
    }
    
    public void impostaOraIngresso(LocalTime oraIngresso) {
        this.oraIngresso = oraIngresso;
        this.presenzaRegistrata = oraIngresso != null;
    }

    public void impostaOraUscita(LocalTime oraUscita) {
        this.oraUscita = oraUscita;
        this.completato = oraUscita != null;
    }
}