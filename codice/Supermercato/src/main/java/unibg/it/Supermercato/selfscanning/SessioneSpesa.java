package unibg.it.Supermercato.selfscanning;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

import unibg.it.Supermercato.gestionefedelta.Promozione;
import unibg.it.Supermercato.gestionefedelta.PuntiAccumulati;
import unibg.it.Supermercato.gestionemagazzino.*;
import unibg.it.Supermercato.sistemaaccesso.*;

public class SessioneSpesa implements ISessioneControllabile {

    private LocalDateTime dataOra;
    private EStatoSessione stato;
    private Cliente cliente;
    private IRepositoryClienti repositoryClienti;
    private IRepositoryProdotti repositoryProdotti;
    private double totaleDaPagare;
    private boolean premioFedeltaApplicato;
    private int puntiDaScalare;
    private IScorte gestoreScorte;
    private final Random random;

    public Carrello carrello;
    public Pagamento pagamento;
    public LettoreOttico lettoreOttico;
    public Totem totem;

    public SessioneSpesa(Cliente cliente, IAutenticazione autenticazione) {
        this(cliente, autenticazione, new Random());
    }

    public SessioneSpesa(Cliente cliente, IAutenticazione autenticazione, Random random) {
        dataOra = LocalDateTime.now(ZoneId.systemDefault());
        stato = EStatoSessione.IN_CORSO;
        carrello = new Carrello();
        lettoreOttico = new LettoreOttico(autenticazione, carrello);
        totem = null;
        this.cliente = cliente;
        this.random = random;
        premioFedeltaApplicato = false;
        puntiDaScalare = 0;
    }

    public boolean scansionaProdotto(Prodotto p) {
        if (p == null || stato != EStatoSessione.IN_CORSO) {
            return false;
        }

        ElementoCarrello prima = carrello.trovaElemento(p);
        int quantitaPrima = (prima == null) ? 0 : prima.getQuantita();

        lettoreOttico.scansionaProdotto(p);

        ElementoCarrello dopo = carrello.trovaElemento(p);
        int quantitaDopo = (dopo == null) ? 0 : dopo.getQuantita();

        return quantitaDopo > quantitaPrima;
    }

    public boolean rimuoviProdotto(Prodotto p) {
        if (p == null || stato != EStatoSessione.IN_CORSO || carrello.trovaElemento(p) == null) {
            return false;
        }

        lettoreOttico.rimuoviProdotto(p);
        return true;
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

        puntiDaScalare = promozione.calcolaPuntiDaScalare(puntiDisponibili);

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

        boolean serveControllo = controlloNecessario();

        if (serveControllo && !totem.isCassiereDisponibile()) {
            return false;
        }

        pagamento = new Pagamento(totaleDaPagare, modalita);

        if (!totem.effettuaPagamento(totaleDaPagare)) {
            return false;
        }

        if (serveControllo) {
            totem.richiediControllo(this);
            totem.registraArrivoCassiere(this);
        } else {
            registraAcquistoCompletato();
            stato = EStatoSessione.COMPLETATA;
        }

        return true;
    }

    private void registraAcquistoCompletato() {

        if (repositoryClienti != null && cliente != null) {

            if (puntiDaScalare > 0) {
                repositoryClienti.scalaPunti(cliente.getId(), puntiDaScalare);
                puntiDaScalare = 0;
            }

            PuntiAccumulati puntiAccumulati = new PuntiAccumulati();
            int nuoviPunti = puntiAccumulati.calcolaPunti(totaleDaPagare);

            repositoryClienti.aggiungiPunti(cliente.getId(), nuoviPunti);
            repositoryClienti.salvaStorico(cliente.getId(), dataOra, totaleDaPagare);
        }

        if (repositoryProdotti != null) {
            for (ElementoCarrello elemento : carrello.getElementi()) {

                Prodotto prodotto = elemento.prodotto;
                int quantitaAcquistata = elemento.getQuantita();

                prodotto.setQuantitaScaffale(prodotto.getQuantitaScaffale() - quantitaAcquistata);

                repositoryProdotti.decrementaQuantitaScaffale(prodotto.getCodiceABarre(), quantitaAcquistata);

                if (gestoreScorte != null) {
                    gestoreScorte.aggiornaScorte(prodotto, TipoScorta.SCAFFALE);
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

    @Override
    public void segnala() {
        stato = EStatoSessione.SEGNALATA;
    }

    public void avviaControllo() {
        if (stato == EStatoSessione.PAGAMENTO) {
            stato = EStatoSessione.CONTROLLO;
        }
    }

    @Override
    public void completaDopoControllo() {
        if (stato == EStatoSessione.CONTROLLO) {
            registraAcquistoCompletato();
            stato = EStatoSessione.COMPLETATA;
        }
    }

    public EStatoSessione getStato() {
        return stato;
    }

    @Override
    public boolean isInControllo() {
        return stato == EStatoSessione.CONTROLLO;
    }

    @Override
    public boolean richiedeControllo() {
        return stato == EStatoSessione.PAGAMENTO;
    }

    @Override
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
        return random.nextBoolean();
    }

    public double getTotaleDaPagare() {
        return totaleDaPagare;
    }

    @Override
    public Map<Prodotto, Integer> getContenutoCarrello() {
        Map<Prodotto, Integer> contenuto = new LinkedHashMap<>();
        for (ElementoCarrello elemento : carrello.getElementi()) {
            contenuto.put(elemento.prodotto, elemento.getQuantita());
        }
        return contenuto;
    }

    public void setRepositoryClienti(IRepositoryClienti repositoryClienti) {
        this.repositoryClienti = repositoryClienti;
    }

    public void setRepositoryProdotti(IRepositoryProdotti repositoryProdotti) {
        this.repositoryProdotti = repositoryProdotti;
    }

    @Override
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
