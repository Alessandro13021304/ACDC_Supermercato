/**
 * @author Alessandro Frassi
 */

package unibg.it.Supermercato.gestionedipendenti;

import java.time.LocalDate;
import java.util.List;

import unibg.it.Supermercato.sistemaaccesso.Dipendente;
import unibg.it.Supermercato.sistemaaccesso.IAutenticazione;
import unibg.it.Supermercato.sistemaaccesso.IRepositoryDipendenti;
import unibg.it.Supermercato.sistemaaccesso.Persona;
import unibg.it.Supermercato.sistemaaccesso.Presenza;
import unibg.it.Supermercato.sistemaaccesso.Turno;

public class GestorePresenze implements IPresenze {

    private IRepositoryDipendenti repositoryDipendenti;
    private IAutenticazione autenticazione;

    public GestorePresenze(IRepositoryDipendenti repositoryDipendenti, IAutenticazione autenticazione) {
        this.repositoryDipendenti = repositoryDipendenti;
        this.autenticazione = autenticazione;
    }

    /**
	 * @param
	 * @return 
	 */
    @Override
    public void registraIngresso(Dipendente dipendente) {
        if (dipendente == null) {
            return;
        }

        Persona persona = autenticazione.autentica(
                dipendente.getEmail(),
                dipendente.getPassword()
        );

        if (!(persona instanceof Dipendente)) {
            return;
        }

        List<Turno> turni = repositoryDipendenti.trovaTurniByMatricola(
                dipendente.getMatricola()
        );

        for (Turno turno : turni) {
            if (turno != null
                    && turno.appartieneAllaData(LocalDate.now())
                    && !turno.presenzaIniziata()
                    && !turno.isCompletato()) {

                turno.registraInizio();

                repositoryDipendenti.aggiornaPresenza(
                        dipendente.getMatricola(),
                        turno.getData(),
                        turno.getOrarioInizio(),
                        turno.getOraIngresso(),
                        turno.getOraUscita(),
                        turno.presenzaIniziata(),
                        turno.isCompletato()
                );

                break;
            }
        }
    }

    /**
	 * @param
	 * @return 
	 */
    @Override
    public void registraUscita(Dipendente dipendente) {
        if (dipendente == null) {
            return;
        }

        Persona persona = autenticazione.autentica(
                dipendente.getEmail(),
                dipendente.getPassword()
        );

        if (!(persona instanceof Dipendente)) {
            return;
        }

        List<Turno> turni = repositoryDipendenti.trovaTurniByMatricola(
                dipendente.getMatricola()
        );

        for (Turno turno : turni) {
            if (turno != null
                    && turno.appartieneAllaData(LocalDate.now())
                    && turno.presenzaIniziata()
                    && !turno.isCompletato()) {

                turno.registraFine();

                repositoryDipendenti.aggiornaPresenza(
                        dipendente.getMatricola(),
                        turno.getData(),
                        turno.getOrarioInizio(),
                        turno.getOraIngresso(),
                        turno.getOraUscita(),
                        turno.presenzaIniziata(),
                        turno.isCompletato()
                );

                break;
            }
        }
    }

    /**
	 * @param
	 * @return 
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
}