package unibg.it.Supermercato.sistemaaccesso;

import java.time.LocalDate;

public abstract class Persona {

    protected String email;
    protected String password;
    protected String nome;
    protected String cognome;
    protected LocalDate dataNascita;

    public boolean autenticati() {
        return false;
    }
}