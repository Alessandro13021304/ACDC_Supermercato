package unibg.it.Supermercato.sistemaaccesso;

import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

import unibg.it.Supermercato.gestionemagazzino.Prodotto;
import unibg.it.Supermercato.gestionemagazzino.IProdotti;
import unibg.it.Supermercato.gestionemagazzino.ScorteObserver;
import unibg.it.Supermercato.gestionemagazzino.TipoScorta;
import unibg.it.Supermercato.gestionemagazzino.IScorte;

public class Magazziniere extends Dipendente implements ScorteObserver {
	
    private static final int SOGLIA_MINIMA = 5;
    private static final int QUANTITA_RIFORNIMENTO = 20;
    private IProdotti gestoreProdotti;
    private IScorte gestoreScorte;

    public Magazziniere(String nome, String cognome, String email, String password, LocalDate dataNascita) {
        super(nome, cognome, email, password, dataNascita);
    }

    public void setGestoreProdotti(IProdotti gestoreProdotti) {
        this.gestoreProdotti = gestoreProdotti;
    }
    
    public void setGestoreScorte(IScorte gestoreScorte) {
        this.gestoreScorte = gestoreScorte;
        
        if (gestoreScorte != null) {
            gestoreScorte.aggiungiObserver(this);
        }
    }
    
    public Prodotto visualizzaProdotto(String codiceABarre) {
        if (gestoreProdotti == null) {
            return null;
        }

        return gestoreProdotti.cercaProdotto(codiceABarre);
    }
    
    public List<Prodotto> visualizzaProdotti() {
        if (gestoreProdotti == null) {
            return new ArrayList<>();
        }

        return gestoreProdotti.visualizzaProdotti();
    }
    
    public void rifornisciScaffale(Prodotto p, int qty) {
        if (p == null || qty <= 0) {
            return;
        }

        if (p.getQuantitaMagazzino() < qty) {
            return;
        }

        p.setQuantitaMagazzino(p.getQuantitaMagazzino() - qty);
        p.setQuantitaScaffale(p.getQuantitaScaffale() + qty);
    }

    public boolean segnalaScorteInsufficientiMagazzino(Prodotto p) {
        if (p == null) {
            return false;
        }

        boolean insufficiente = p.getQuantitaMagazzino() < SOGLIA_MINIMA;

        if (insufficiente && gestoreScorte != null) {
            gestoreScorte.aggiornaScorte(p, TipoScorta.MAGAZZINO);
        }

        return insufficiente;
    }
 
    @Override
    public String getRuolo() {
        return "Magazziniere";
    }
    
    @Override
    public void aggiorna(Prodotto prodotto, TipoScorta tipoScorta) {
        if (prodotto == null || tipoScorta != TipoScorta.SCAFFALE) {
            return;
        }

        int quantitaMagazzino = prodotto.getQuantitaMagazzino();

        if (quantitaMagazzino <= 0) {
            return;
        }

        int quantitaDaSpostare = Math.min(QUANTITA_RIFORNIMENTO, quantitaMagazzino);

        rifornisciScaffale(prodotto, quantitaDaSpostare);

        segnalaScorteInsufficientiMagazzino(prodotto);
    }
}
