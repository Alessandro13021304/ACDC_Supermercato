package unibg.it.Supermercato.sistemaaccesso;

import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

import unibg.it.Supermercato.gestionemagazzino.Fornitore;
import unibg.it.Supermercato.gestionemagazzino.OrdineFornitore;
import unibg.it.Supermercato.gestionemagazzino.Prodotto;
import unibg.it.Supermercato.gestionemagazzino.IProdotti;
import unibg.it.Supermercato.gestionemagazzino.ScorteObserver;
import unibg.it.Supermercato.gestionemagazzino.ETipoScorta;
import unibg.it.Supermercato.gestionemagazzino.IScorte;
import unibg.it.Supermercato.gestionemagazzino.IOrdini;

public class ResponsabileDiReparto extends Dipendente implements ScorteObserver {
	
	private static final int SOGLIA_MINIMA = 5;
	private static final int QUANTITA_ORDINE_STANDARD = 100;
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
    
    public void aggiungiProdotto(Prodotto prodotto) {
        if (gestoreProdotti != null) {
            gestoreProdotti.aggiungiProdotto(prodotto);
        }
    }

    public void rimuoviProdotto(Prodotto prodotto) {
        if (gestoreProdotti != null) {
            gestoreProdotti.rimuoviProdotto(prodotto);
        }
    }

    public void modificaProdotto(Prodotto prodotto) {
        if (gestoreProdotti != null) {
            gestoreProdotti.modificaProdotto(prodotto);
        }
    }

    public boolean segnalaScorteInsufficientiScaffale(Prodotto p) {
        if (p == null) {
            return false;
        }

        boolean insufficiente = p.getQuantitaScaffale() < SOGLIA_MINIMA;

        if (insufficiente && gestoreScorte != null) {
            gestoreScorte.aggiornaScorte(p, ETipoScorta.SCAFFALE);
        }

        return insufficiente;
    }
    
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
            	gestoreOrdini.aggiungiProdotto(ordine, prodotto, QUANTITA_ORDINE_STANDARD);
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

