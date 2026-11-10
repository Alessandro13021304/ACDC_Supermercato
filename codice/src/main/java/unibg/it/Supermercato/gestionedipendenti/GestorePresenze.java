/**
 * @author Alessandro Frassi
 */

package unibg.it.Supermercato.gestionedipendenti;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

import unibg.it.Supermercato.sistemaaccesso.Dipendente;
import unibg.it.Supermercato.sistemaaccesso.IAutenticazione;
import unibg.it.Supermercato.sistemaaccesso.IRepositoryDipendenti;
import unibg.it.Supermercato.sistemaaccesso.Persona;
import unibg.it.Supermercato.sistemaaccesso.Presenza;
import unibg.it.Supermercato.sistemaaccesso.Turno;

public class GestorePresenze implements IPresenze {

    private final IRepositoryDipendenti repositoryDipendenti;
    private final IAutenticazione autenticazione;
    private final Clock orologio;

    public GestorePresenze(IRepositoryDipendenti repositoryDipendenti, IAutenticazione autenticazione) {
        this(repositoryDipendenti, autenticazione, Clock.systemDefaultZone());
    }

    public GestorePresenze(IRepositoryDipendenti repositoryDipendenti, IAutenticazione autenticazione, Clock orologio) {
        this.repositoryDipendenti = repositoryDipendenti;
        this.autenticazione = autenticazione;
        this.orologio = orologio;
    }

    /** questo metodo registra l'inizio del turno di un dipendente
	 * @param il dipendente che registra l'ingresso, la sua mail e la password
	 */
    @Override
    public void registraIngresso(Dipendente dipendente, String email, String password) {
        if (!autenticato(dipendente, email, password)) {
            return;
        }

        Turno turno = trovaTurnoDiOggi(dipendente, false);

        if (turno == null) {
            return;
        }

        turno.registraInizio(LocalTime.now(orologio));
        salvaPresenza(dipendente, turno);
    }

    /** questo metodo registra la fine del turno di un dipendente
	 * @param il dipendente che registra l'uscita, la sua mail e la password
	 */
    @Override
    public void registraUscita(Dipendente dipendente, String email, String password) {
        if (!autenticato(dipendente, email, password)) {
            return;
        }

        Turno turno = trovaTurnoDiOggi(dipendente, true);

        if (turno == null) {
            return;
        }

        turno.registraFine(LocalTime.now(orologio));
        salvaPresenza(dipendente, turno);
    }

    /** questo metodo registra l'inizio del turno di un dipendente
	 * @param il dipendente che registra l'ingresso
	 */
    @Override
    public void registraIngresso(Dipendente dipendente) {
        if (dipendente != null) {
            registraIngresso(dipendente, dipendente.getEmail(), dipendente.getPassword());
        }
    }

    /** questo metodo registra la fine del turno di un dipendente
	 * @param il dipendente che registra l'uscita
	 */
    @Override
    public void registraUscita(Dipendente dipendente) {
        if (dipendente != null) {
            registraUscita(dipendente, dipendente.getEmail(), dipendente.getPassword());
        }
    }

    /** questo metodo calcola le ore in cui un dipendente ha lavorato
	 * @param il dipendente di cui calcolare le ore
	 * @return le ore in cui il dipendente ha lavorato
	 */
    @Override
    public double calcolaOreLavorate(Dipendente dipendente) {
        if (dipendente == null) {
            return 0.0;
        }

        double oreTotali = 0.0;

        for (Turno turno : dipendente.visualizzaTurni()) {
            if (turno != null && turno.isCompletato()) {
                Presenza presenza = new Presenza(turno);
                oreTotali += presenza.calcolaOreLavorate();
            }
        }

        return oreTotali;
    }

    /** questo metodo autentica un dipendente
	 * @param il dipendente che si autentica, la sua mail e la password
	 * @return true se la mail e la password sono corrette, altrimenti false
	 */
    private boolean autenticato(Dipendente dipendente, String email, String password) {
        if (dipendente == null || email == null || password == null) {
            return false;
        }

        Persona persona = autenticazione.autentica(email, password);

        return persona instanceof Dipendente autenticato && Objects.equals(autenticato.getMatricola(), dipendente.getMatricola());
    }

    /** questo metodo trova il turno che il dipendente deve timbrare
	 * @param il dipendente che deve timbrare, true/false se il turno è già stato timbrato
	 * @return il turno da timbrare
	 */
    private Turno trovaTurnoDiOggi(Dipendente dipendente, boolean presenzaGiaIniziata) {
        LocalDate oggi = LocalDate.now(orologio);

        for (Turno turno : repositoryDipendenti.trovaTurniByMatricola(dipendente.getMatricola())) {
            if (turno != null && turno.appartieneAllaData(oggi) && !turno.isCompletato() && turno.presenzaIniziata() == presenzaGiaIniziata) {
                return turno;
            }
        }

        return null;
    }

    /** questo metodo salva la presenza di un dipendente in persistenza
	 * @param il dipendente e il suo turno da salvare
	 */
    private void salvaPresenza(Dipendente dipendente, Turno turno) {
        repositoryDipendenti.aggiornaPresenza(
                dipendente.getMatricola(),
                turno.getData(),
                turno.getOrarioInizio(),
                turno.getOraIngresso(),
                turno.getOraUscita(),
                turno.presenzaIniziata(),
                turno.isCompletato()
        );
    }
}
