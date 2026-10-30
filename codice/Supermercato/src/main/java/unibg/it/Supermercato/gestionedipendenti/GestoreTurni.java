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

    public void assegnaTurno(Dipendente dipendente, Turno turno) {
        if (dipendente == null || turno == null) {
            return;
        }

        dipendente.setRepositoryDipendenti(repositoryDipendenti);

        List<Turno> turniEsistenti =
                repositoryDipendenti.trovaTurniByMatricola(
                        dipendente.getMatricola()
                );

        for (Turno turnoEsistente : turniEsistenti) {
            if (turnoSovrapposto(turno, turnoEsistente)) {
                return;
            }
        }

        repositoryDipendenti.salvaTurno(
                dipendente.getMatricola(),
                turno
        );
    }

    private boolean turnoSovrapposto(Turno nuovoTurno, Turno turnoEsistente) {

        if (!nuovoTurno.getData().equals(turnoEsistente.getData())) {
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