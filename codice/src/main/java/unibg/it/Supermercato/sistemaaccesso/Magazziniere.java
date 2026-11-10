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

    /** questo metodo permette di visualizzare tutti prodotti
	 * @return la lista dei prodotti
	 */
    public List<Prodotto> visualizzaProdotti() {
        if (gestoreProdotti == null) {
            return new ArrayList<>();
        }

        return gestoreProdotti.visualizzaProdotti();
    }

    /** questo metodo aumenta la quantità di un prodotto nello scaffale, prendendola dal magazzino
	 * @param il prodotto e la quantità da aggiungere sullo scaffale
	 */
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

    /** questo metodo segnala se le scorte nel magazzino sono insufficienti (<5)
     * @param il prodotto di cui verificare la quantità
	 * @return true se la scorta è insufficiente, altrimenti false
	 */
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

    /** questo metodo aggiorna la quantità di un prodotto sullo scaffale dopo che è scesa sotto la soglia minima
	 * @param il prodotto di cui è cambiata la quantità e dove è cambiata (tipo di scorta)
	 */
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
