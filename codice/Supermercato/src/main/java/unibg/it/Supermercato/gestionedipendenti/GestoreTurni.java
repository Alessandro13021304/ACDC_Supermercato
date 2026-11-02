/**
 * @author Alessandro Frassi
 */

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

    /** questo metodo assegna il turno al dipendente
	 * @param il dipendente e il turno da assegnargli
	 */
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

    /** questo metodo controlla se il dipendente ha già un turno in quelle ore
	 * @param il turno da aggiungere e il turno esistente
	 * @return true se i turni sono sovrapposti, altrimenti false
	 */
    private boolean turnoSovrapposto(Turno nuovoTurno, Turno turnoEsistente) {

        if (!nuovoTurno.getData().equals(turnoEsistente.getData())) {
            return false;
        }

        return nuovoTurno.getOrarioInizio().isBefore(turnoEsistente.getOrarioFine())
                && nuovoTurno.getOrarioFine().isAfter(turnoEsistente.getOrarioInizio());
    }

    /** questo metodo permette di visualizzare i turni di un dipendente 
	 * @param il dipendente di cui si vogliono visualizzare i turni
	 * @return la lista dei turni del dipendente
	 */
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