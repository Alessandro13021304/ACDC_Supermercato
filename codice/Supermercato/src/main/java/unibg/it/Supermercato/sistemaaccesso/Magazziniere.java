package unibg.it.Supermercato.sistemaaccesso;

import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

import unibg.it.Supermercato.gestionemagazzino.*;

public class Magazziniere extends Dipendente implements IScorteObserver {

    private IProdotti gestoreProdotti;
    private IScorte gestoreScorte;
    private IRepositoryProdotti repositoryProdotti;

    public Magazziniere(String nome, String cognome, String email, String password, LocalDate dataNascita) {
        super(nome, cognome, email, password, dataNascita);
    }

    public void setGestoreProdotti(IProdotti gestoreProdotti) {
        this.gestoreProdotti = gestoreProdotti;
    }

    public void setRepositoryProdotti(IRepositoryProdotti repositoryProdotti) {
        this.repositoryProdotti = repositoryProdotti;
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

        if (repositoryProdotti != null) {
            repositoryProdotti.spostaDalMagazzinoAlloScaffale(p.getCodiceABarre(), qty);
        }
    }

    public boolean segnalaScorteInsufficientiMagazzino(Prodotto p) {
        if (p == null) {
            return false;
        }

        boolean insufficiente = p.getQuantitaMagazzino() < ParametriScorte.SOGLIA_MINIMA;

        if (insufficiente && gestoreScorte != null) {
            gestoreScorte.aggiornaScorte(p, ETipoScorta.MAGAZZINO);
        }

        return insufficiente;
    }

    @Override
    public String getRuolo() {
        return "Magazziniere";
    }

    @Override
    public void aggiorna(Prodotto prodotto, ETipoScorta tipoScorta) {
        if (prodotto == null || tipoScorta != ETipoScorta.SCAFFALE) {
            return;
        }

        int quantitaMagazzino = prodotto.getQuantitaMagazzino();

        if (quantitaMagazzino <= 0) {
            return;
        }

        int quantitaDaSpostare = Math.min(ParametriScorte.QUANTITA_RIFORNIMENTO, quantitaMagazzino);

        rifornisciScaffale(prodotto, quantitaDaSpostare);

        segnalaScorteInsufficientiMagazzino(prodotto);
    }
}
