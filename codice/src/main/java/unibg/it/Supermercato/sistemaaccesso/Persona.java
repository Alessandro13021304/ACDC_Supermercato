/**
 * @author Alessandro Frassi
 */

package unibg.it.Supermercato.sistemaaccesso;

import java.time.LocalDate;

public abstract class Persona {

    protected String nome;
    protected String cognome;
    protected String email;
    protected String password;
    protected LocalDate dataNascita;

    protected Persona(String nome, String cognome, String email, String password, LocalDate dataNascita) {
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
        this.password = password;
        this.dataNascita = dataNascita;
    }
    
    public String getNome() {
        return nome;
    }

    public String getCognome() {
        return cognome;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public LocalDate getDataNascita() {
        return dataNascita;
    }
}