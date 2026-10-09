package unibg.it.Supermercato.gestionemagazzino;

public class Fornitore {

    private String nome;
    private String contatto;
    private String email;
    
    public Fornitore(String nome, String contatto, String email) {
        this.nome = nome;
        this.contatto = contatto;
        this.email = email;
    }
    
    public Fornitore(String nome) {
        this(nome, null, null);
    }
    
    public String getNome() {
        return nome;
    }
    
    public String getContatto() {
        return contatto;
    }

    public String getEmail() {
        return email;
    }

}