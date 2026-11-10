package unibg.it.Supermercato.sistemaaccesso;

import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

import unibg.it.Supermercato.gestionemagazzino.*;

public class ResponsabileDiReparto extends Dipendente implements IScorteObserver {

    private IProdotti gestoreProdotti;
    private Fornitore fornitore;
    private List<OrdineFornitore> ordiniElaborati;
    private IScorte gestoreScorte;
    private IOrdini gestoreOrdini;
    private int numeroReparto;

    public ResponsabileDiReparto(String nome, String cognome, String email, String password, LocalDate dataNascita) {
        super(nome, cognome, email, password, dataNascita);
        ordiniElaborati = new ArrayList<>();
    }

    public void setNumeroReparto(int numeroReparto) {
        this.numeroReparto = numeroReparto;
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

    public void setFornitore(Fornitore fornitore) {
        this.fornitore = fornitore;
    }

    public void setGestoreOrdini(IOrdini gestoreOrdini) {
        this.gestoreOrdini = gestoreOrdini;
    }

    public List<OrdineFornitore> getOrdiniElaborati() {
        return ordiniElaborati;
    }

    /** questo metodo cerca un prodotto tramite il suo codice a barre
     * @param il coidce a barre del prodotto da cercare
	 * @return il prodotto trovato
	 */
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

    /** questo metodo salva un prodotto in persistenza
     * @param il prodotto da salvare
	 */
    public void aggiungiProdotto(Prodotto prodotto) {
        if (gestoreProdotti != null) {
            gestoreProdotti.aggiungiProdotto(prodotto);
        }
    }

    /** questo metodo rimuove un prodotto dalla persistenza
     * @param il prodotto da rimuovere
	 */
    public void rimuoviProdotto(Prodotto prodotto) {
        if (gestoreProdotti != null) {
            gestoreProdotti.rimuoviProdotto(prodotto);
        }
    }

    /** questo metodo modifica un prodotto in persistenza
     * @param il prodotto da modificare
	 */
    public void modificaProdotto(Prodotto prodotto) {
        if (gestoreProdotti != null) {
            gestoreProdotti.modificaProdotto(prodotto);
        }
    }

    /** questo metodo segnala se le scorte nello scaffale sono insufficienti (<5)
     * @param il prodotto di cui verificare la quantità
	 * @return true se la scorta è insufficiente, altrimenti false
	 */
    public boolean segnalaScorteInsufficientiScaffale(Prodotto p) {
        if (p == null) {
            return false;
        }

        boolean insufficiente = p.getQuantitaScaffale() < ParametriScorte.SOGLIA_MINIMA;

        if (insufficiente && gestoreScorte != null) {
            gestoreScorte.aggiornaScorte(p, ETipoScorta.SCAFFALE);
        }

        return insufficiente;
    }

    /** questo metodo crea un ordine
     * @param il fornitore a cui chiedere l'ordine e la lista dei prodotti da ordinare
	 * @return l'ordine creato
	 */
    public OrdineFornitore elaboraOrdine(Fornitore f, List<Prodotto> prodotti) {
        if (f == null || prodotti == null || prodotti.isEmpty()) {
            return null;
        }

        if (gestoreOrdini == null) {
            return null;
        }

        OrdineFornitore ordine = gestoreOrdini.creaOrdine(f);

        for (Prodotto prodotto : prodotti) {
            if (prodotto != null) {
                gestoreOrdini.aggiungiProdotto(ordine, prodotto, ParametriScorte.QUANTITA_ORDINE_STANDARD);
            }
        }

        if (ordine.getRighe().isEmpty()) {
            return null;
        }

        gestoreOrdini.salvaOrdine(ordine);

        return ordine;
    }

    @Override
    public String getRuolo() {
        return "ResponsabileDiReparto";
    }

    /** questo metodo aggiorna la quantità di un prodotto nel magazzino dopo che è scesa sotto la soglia minima
	 * @param il prodotto di cui è cambiata la quantità e dove è cambiata (tipo di scorta)
	 */
    @Override
    public void aggiorna(Prodotto prodotto, ETipoScorta tipoScorta) {
        if (prodotto == null || tipoScorta != ETipoScorta.MAGAZZINO) {
            return;
        }

        if (prodotto.getNumeroReparto() != numeroReparto) {
            return;
        }

        if (fornitore != null) {
            List<Prodotto> prodotti = new ArrayList<>();
            prodotti.add(prodotto);

            OrdineFornitore ordine = elaboraOrdine(fornitore, prodotti);

            if (ordine != null) {
                ordiniElaborati.add(ordine);
            }
        }
    }

    public int getNumeroReparto() {
        return numeroReparto;
    }
}
