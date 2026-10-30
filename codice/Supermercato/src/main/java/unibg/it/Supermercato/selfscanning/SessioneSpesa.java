package unibg.it.Supermercato.selfscanning;

import java.time.LocalDateTime;
import java.util.Random;

import unibg.it.Supermercato.gestionemagazzino.Prodotto;
import unibg.it.Supermercato.gestionemagazzino.IScorte;
import unibg.it.Supermercato.gestionemagazzino.TipoScorta;
import unibg.it.Supermercato.sistemaaccesso.Cliente;
import unibg.it.Supermercato.persistenzadati.IRepositoryClienti;
import unibg.it.Supermercato.persistenzadati.IRepositoryProdotti;
import unibg.it.Supermercato.gestionefedelta.Promozione;
import unibg.it.Supermercato.gestionefedelta.PuntiAccumulati;
import unibg.it.Supermercato.sistemaaccesso.IAutenticazione;

public class SessioneSpesa {

    private LocalDateTime dataOra;
    private EStatoSessione stato;
    private Cliente cliente;
    private IRepositoryClienti repositoryClienti;
    private IRepositoryProdotti repositoryProdotti;
    private double totaleDaPagare;
    private boolean premioFedeltaApplicato;
    private IScorte gestoreScorte;

    public Carrello carrello;
    public Pagamento pagamento;
    public LettoreOttico lettoreOttico;
    public Totem totem;

    public SessioneSpesa(Cliente cliente, IAutenticazione autenticazione) {
        dataOra = LocalDateTime.now();
        stato = EStatoSessione.IN_CORSO;
        carrello = new Carrello();
        lettoreOttico = new LettoreOttico(autenticazione, carrello);
        totem = null;
        this.cliente = cliente;
        premioFedeltaApplicato = false;
    }

	public boolean scansionaProdotto(Prodotto p) {
	    if (p == null || stato != EStatoSessione.IN_CORSO) {
	        return false;
	    }
	
	    return lettoreOttico.scansionaProdotto(p);
	}

	public boolean rimuoviProdotto(Prodotto p) {
	    if (p == null || stato != EStatoSessione.IN_CORSO) {
	        return false;
	    }

	    return lettoreOttico.rimuoviProdotto(p);
	}

    public void terminaSpesa() {
        if (stato == EStatoSessione.IN_CORSO) {
            totaleDaPagare = carrello.calcolaTotale();
            stato = EStatoSessione.PAGAMENTO;
        }
    }
    
    public boolean usaPremioFedelta() {
        if (stato != EStatoSessione.PAGAMENTO) {
            return false;
        }
        
        if (premioFedeltaApplicato) {
            return false;
        }

        if (repositoryClienti == null || cliente == null) {
            return false;
        }

        int puntiDisponibili = repositoryClienti.trovaPuntiByCliente(cliente.getId());

        Promozione promozione = new Promozione();
        double percentualeSconto = promozione.calcolaSconto(puntiDisponibili);

        if (percentualeSconto <= 0) {
            return false;
        }

        totaleDaPagare = promozione.applicaSconto(totaleDaPagare, percentualeSconto);

        int puntiDaScalare = promozione.calcolaPuntiDaScalare(puntiDisponibili);
        repositoryClienti.scalaPunti(cliente.getId(), puntiDaScalare);

        premioFedeltaApplicato = true;
        
        return true;
    }
    
    public boolean effettuaPagamento(EModalitaPagamento modalita) {
        if (stato != EStatoSessione.PAGAMENTO) {
            return false;
        }

        if (modalita == null) {
            return false;
        }

        if (totem == null) {
            return false;
        }

        pagamento = new Pagamento(totaleDaPagare, modalita);

        if (totem.effettuaPagamento(totaleDaPagare)) {

            if (controlloNecessario()) {
            	
            	if (totem.richiediControllo(this)) {
            		totem.registraArrivoCassiere(this);
                } else {
                    return false;
                }

            } else {
            	registraAcquistoCompletato();
            	stato = EStatoSessione.COMPLETATA;
            }

            return true;
        }

        return false;
    }
    
    private void registraAcquistoCompletato() {

        if (repositoryClienti != null && cliente != null) {
        	PuntiAccumulati puntiAccumulati = new PuntiAccumulati();
        	int nuoviPunti = puntiAccumulati.calcolaPunti(totaleDaPagare);

        	repositoryClienti.aggiungiPunti(cliente.getId(), nuoviPunti);

        	int saldoAggiornato = repositoryClienti.trovaPuntiByCliente(cliente.getId());
        	cliente.setPuntiFedelta(saldoAggiornato);

        	repositoryClienti.salvaStorico(cliente.getId(), this);
        }

        if (repositoryProdotti != null) {
            for (ElementoCarrello elemento : carrello.getElementi()) {

                Prodotto prodotto = elemento.prodotto;
                int quantitaAcquistata = elemento.getQuantita();

                int nuovaQuantitaScaffale =
                        prodotto.getQuantitaScaffale() - quantitaAcquistata;

                prodotto.setQuantitaScaffale(nuovaQuantitaScaffale);
                repositoryProdotti.modificaProdotto(prodotto);
                
                if (gestoreScorte != null) {
                    gestoreScorte.aggiornaScorte(
                            prodotto,
                            TipoScorta.SCAFFALE
                    );
                }
            }
        }
    }
    
    public String generaRicevuta() {
        if (stato != EStatoSessione.COMPLETATA || pagamento == null) {
            return "";
        }

        GeneratoreRicevuta generatore = new GeneratoreRicevuta();

        return generatore.genera(this, pagamento);
    }
    
    public void segnala() {
        stato = EStatoSessione.SEGNALATA;
    }
    
    public void avviaControllo() {
        if (stato == EStatoSessione.PAGAMENTO) {
            stato = EStatoSessione.CONTROLLO;
        }
    }
    
    public void completaDopoControllo() {
        if (stato == EStatoSessione.CONTROLLO) {
        	registraAcquistoCompletato();
            stato = EStatoSessione.COMPLETATA;
        }
    }
    
    public EStatoSessione getStato() {
        return stato;
    }
    
    public boolean isInControllo() {
        return stato == EStatoSessione.CONTROLLO;
    }
    
    public boolean richiedeControllo() {
        return stato == EStatoSessione.PAGAMENTO;
    }
    
    public Cliente getCliente() {
        return cliente;
    }
    
    public boolean clienteGiaSegnalato() {
        if (repositoryClienti == null || cliente == null) {
            return false;
        }

        return repositoryClienti.isClienteSegnalato(cliente.getId());
    }
    
    public boolean controlloNecessario() {
        return clienteGiaSegnalato() || controlloCasuale();
    }
    
    public boolean controlloCasuale() {
        Random random = new Random();
        return random.nextBoolean();
    }
    
    public double getTotaleDaPagare() {
        return totaleDaPagare;
    }
    
    public void setRepositoryClienti(IRepositoryClienti repositoryClienti) {
        this.repositoryClienti = repositoryClienti;
    }
    
    public void setRepositoryProdotti(IRepositoryProdotti repositoryProdotti) {
        this.repositoryProdotti = repositoryProdotti;
    }
    
    public LocalDateTime getDataOra() {
        return dataOra;
    }
    
    public void setTotem(Totem totem) {
        this.totem = totem;
    }
    
    public Totem getTotem() {
        return totem;
    }
    
    public void setGestoreScorte(IScorte gestoreScorte) {
        this.gestoreScorte = gestoreScorte;
    }
}
