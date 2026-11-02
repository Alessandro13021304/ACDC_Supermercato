package unibg.it.Supermercato.gestionedipendenti;

import java.util.List;

import unibg.it.Supermercato.sistemaaccesso.Dipendente;
import unibg.it.Supermercato.sistemaaccesso.IRepositoryDipendenti;
import unibg.it.Supermercato.sistemaaccesso.ITurni;
import unibg.it.Supermercato.sistemaaccesso.Turno;

public class GestoreTurni implements ITurni {

    private IRepositoryDipendenti repositoryDipendenti;

    public GestoreTurni(IRepositoryDipendenti repositoryDipendenti) {
        this.repositoryDipendenti = repositoryDipendenti;
    }

    @Override
    public boolean assegnaTurno(Dipendente dipendente, Turno turno) {
        // Il turno deve avere una data e un orario di inizio precedente a quello di fine
        if (dipendente == null || turno == null || turno.getData() == null || !turno.isOrarioValido()) {
            return false;
        }

        dipendente.setRepositoryDipendenti(repositoryDipendenti);

        List<Turno> turniEsistenti =
                repositoryDipendenti.trovaTurniByMatricola(
                        dipendente.getMatricola()
                );

        for (Turno turnoEsistente : turniEsistenti) {
            if (turnoSovrapposto(turno, turnoEsistente)) {
                return false;
            }
        }

        repositoryDipendenti.salvaTurno(
                dipendente.getMatricola(),
                turno
        );

        return true;
    }

    private boolean turnoSovrapposto(Turno nuovoTurno, Turno turnoEsistente) {

        if (turnoEsistente == null || !nuovoTurno.getData().equals(turnoEsistente.getData())) {
            return false;
        }

        return nuovoTurno.getOrarioInizio().isBefore(turnoEsistente.getOrarioFine())
                && nuovoTurno.getOrarioFine().isAfter(turnoEsistente.getOrarioInizio());
    }

    @Override
    public List<Turno> visualizzaTurni(Dipendente dipendente) {
        if (dipendente == null) {
            return List.of();
        }
        return repositoryDipendenti.trovaTurniByMatricola(
                dipendente.getMatricola()
        );
    }
}
