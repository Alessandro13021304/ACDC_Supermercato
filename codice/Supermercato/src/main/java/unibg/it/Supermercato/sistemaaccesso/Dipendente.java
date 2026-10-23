package unibg.it.Supermercato.sistemaaccesso;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import unibg.it.Supermercato.gestionedipendenti.Turno;
import unibg.it.Supermercato.gestionedipendenti.IPresenze;
import unibg.it.Supermercato.persistenzadati.IRepositoryDipendenti;

public abstract class Dipendente extends Persona implements RuoloUtente {

    private String matricola;
    private IRepositoryDipendenti repositoryDipendenti;
    private IPresenze gestorePresenze;

    protected Dipendente(String nome, String cognome, String email, String password, LocalDate dataNascita) {
        super(nome, cognome, email, password, dataNascita);
    }
    
    public void setRepositoryDipendenti(IRepositoryDipendenti repositoryDipendenti) {
        this.repositoryDipendenti = repositoryDipendenti;
    }
    
    public void setGestorePresenze(IPresenze gestorePresenze) {
        this.gestorePresenze = gestorePresenze;
    }
    
    public void timbraIngresso() {
        if (gestorePresenze != null) {
            gestorePresenze.registraIngresso(this);
        }
    }

    public void timbraUscita() {
        if (gestorePresenze != null) {
            gestorePresenze.registraUscita(this);
        }
    }
    
    public List<Turno> visualizzaTurni() {
        if (repositoryDipendenti != null && matricola != null) {
            return repositoryDipendenti.trovaTurniByMatricola(matricola);
        }

        return new ArrayList<>();
    }

    public String getMatricola() {
        return matricola;
    }

    public void setMatricola(String matricola) {
        this.matricola = matricola;
    }
    
    @Override
    public String getRuolo() {
        return "Dipendente";
    }
}