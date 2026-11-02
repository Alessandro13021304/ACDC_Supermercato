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

    public GestorePresenze(IRepositoryDipendenti repositoryDipendenti, IAutenticazione autenticazione,
            Clock orologio) {
        this.repositoryDipendenti = repositoryDipendenti;
        this.autenticazione = autenticazione;
        this.orologio = orologio;
    }

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

    @Override
    public void registraIngresso(Dipendente dipendente) {
        if (dipendente != null) {
            registraIngresso(dipendente, dipendente.getEmail(), dipendente.getPassword());
        }
    }

    @Override
    public void registraUscita(Dipendente dipendente) {
        if (dipendente != null) {
            registraUscita(dipendente, dipendente.getEmail(), dipendente.getPassword());
        }
    }

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

    // Le credenziali devono essere valide e appartenere proprio al dipendente che registra la presenza
    private boolean autenticato(Dipendente dipendente, String email, String password) {
        if (dipendente == null || email == null || password == null) {
            return false;
        }

        Persona persona = autenticazione.autentica(email, password);

        return persona instanceof Dipendente autenticato
                && Objects.equals(autenticato.getMatricola(), dipendente.getMatricola());
    }

    // Cerca il turno di oggi: non ancora iniziato (ingresso) oppure iniziato e non concluso (uscita)
    private Turno trovaTurnoDiOggi(Dipendente dipendente, boolean presenzaGiaIniziata) {
        LocalDate oggi = LocalDate.now(orologio);

        for (Turno turno : repositoryDipendenti.trovaTurniByMatricola(dipendente.getMatricola())) {
            if (turno != null
                    && turno.appartieneAllaData(oggi)
                    && !turno.isCompletato()
                    && turno.presenzaIniziata() == presenzaGiaIniziata) {
                return turno;
            }
        }

        return null;
    }

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
