package unibg.it.Supermercato;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.sql.Connection;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;

import unibg.it.Supermercato.persistenzadati.*;
import unibg.it.Supermercato.sistemaaccesso.*;
import unibg.it.Supermercato.gestionemagazzino.*;
import unibg.it.Supermercato.selfscanning.*;
import unibg.it.Supermercato.pagamentiesterni.*;

public class App {

    private static final Logger logger = LogManager.getLogger(App.class);

    public static void main(String[] args) {

        logger.info("Avvio dell'applicazione Supermercato.");

        DBConnection db = DBConnection.getInstance();
        
        DBClienti dbClienti = new DBClienti(true);
        DBDipendenti dbDipendenti = new DBDipendenti(true);
        DBProdotti dbProdotti = new DBProdotti(true);
        DBOrdini dbOrdini = new DBOrdini(true);
        
        try (
        		Connection connClienti = db.getConnection("clienti"); 
        		Connection connProdotti = db.getConnection("prodotti"); 
        		Connection connDipendenti = db.getConnection("dipendenti")
        ) {
            logger.info("Database inizializzati correttamente.");

        } catch (Exception e) {
            logger.error("Errore durante l'inizializzazione dei database.", e);
        }
        
        AuthService authService = new AuthService(dbClienti, dbDipendenti);
        
        SessioneSpesa sessioneSpesa = new SessioneSpesa(null, authService);
        sessioneSpesa.setRepositoryClienti(dbClienti);
        sessioneSpesa.setRepositoryProdotti(dbProdotti);
        
        Totem totem = new Totem(1, new PaymentGateway());
        sessioneSpesa.setTotem(totem);
        
        ControlloSpesa controlloSpesa = new ControlloSpesa();

        Cassiere cassiere = new Cassiere(
                "Anna",
                "Verdi",
                "anna.verdi@example.com",
                "password",
                LocalDate.of(1992, 3, 15),
                dbClienti,
                controlloSpesa
        );

        totem.setCassiere(cassiere);
        
        CarrelloFisico carrelloFisico = new CarrelloFisico();
        totem.setCarrelloFisico(carrelloFisico);
        
        GestoreProdotti gestoreProdotti = new GestoreProdotti(dbProdotti);
        
        // Creazione dei prodotti 
        Prodotto pasta = gestoreProdotti.cercaProdotto("DEMO0001");

        if (pasta == null) {
            pasta = new Prodotto(
                "DEMO0001",
                "Pasta",
                1.50,
                "Prodotto dimostrativo",
                0.0,
                10,
                20,
                1
            );
            gestoreProdotti.aggiungiProdotto(pasta);
        }

        Prodotto biscotti = gestoreProdotti.cercaProdotto("DEMO0002");

        if (biscotti == null) {
            biscotti = new Prodotto(
                "DEMO0002",
                "Biscotti",
                2.20,
                "Prodotto dimostrativo",
                0.0,
                8,
                15,
                1
            );
            gestoreProdotti.aggiungiProdotto(biscotti);
        }

        logger.info("Prodotti pronti.");
        
        GestoreScorte gestoreScorte = new GestoreScorte();
        
        Magazziniere magazziniere = new Magazziniere(
                "Mario",
                "Rossi",
                "mario.rossi@example.com",
                "password",
                LocalDate.of(1990, 1, 1)
        );

        magazziniere.setGestoreProdotti(gestoreProdotti);
        magazziniere.setGestoreScorte(gestoreScorte);
        
        ResponsabileDiReparto responsabile = new ResponsabileDiReparto(
                "Laura",
                "Bianchi",
                "laura.bianchi@example.com",
                "password",
                LocalDate.of(1985, 5, 10)
        );

        responsabile.setNumeroReparto(1);
        responsabile.setGestoreProdotti(gestoreProdotti);
        responsabile.setGestoreScorte(gestoreScorte);
        
        Fornitore fornitore = new Fornitore("Fornitore Demo");
        responsabile.setFornitore(fornitore);
        
        GestoreOrdini gestoreOrdini = new GestoreOrdini(dbOrdini);
        responsabile.setGestoreOrdini(gestoreOrdini);       
        
        // Creazione dell'ordine 
        List<Prodotto> prodottiDaOrdinare = new ArrayList<>();
        prodottiDaOrdinare.add(gestoreProdotti.cercaProdotto("DEMO0001"));
        prodottiDaOrdinare.add(gestoreProdotti.cercaProdotto("DEMO0002"));

        OrdineFornitore ordine = responsabile.elaboraOrdine(fornitore, prodottiDaOrdinare);

        if (ordine != null) {
            logger.info("Ordine creato e salvato.");
            logger.info("Fornitore: {}", fornitore.getNome());
            logger.info("Numero righe: {}", ordine.getRighe().size());
            logger.info("Totale ordine: {}", ordine.getTotale());
            
        } else {
            logger.error("Impossibile creare l'ordine dimostrativo.");
        }
               
        // Recupero degli ordini dal database
        List<OrdineFornitore> ordiniSalvati = gestoreOrdini.visualizzaOrdini();

        logger.info("========== ORDINI SALVATI ==========");
        logger.info("Numero totale di ordini: {}", ordiniSalvati.size());

        for (OrdineFornitore ordineSalvato : ordiniSalvati) {
            logger.info("Fornitore: {}", ordineSalvato.getFornitore().getNome());
            logger.info("Data ordine: {}", ordineSalvato.getDataOrdine());
            logger.info("Stato: {}", ordineSalvato.getStato());
            logger.info("Totale: {}", ordineSalvato.getTotale());

            for (RigaOrdine riga : ordineSalvato.getRighe()) {
                logger.info(
                    "Prodotto: {} | Quantità: {} | Prezzo unitario: {}",
                    riga.getProdotto().getNome(),
                    riga.getQuantita(),
                    riga.getProdotto().getPrezzo()
                );
            }

            logger.info("------------------------------------");
        }
        
        
        try (Scanner scanner = new Scanner(System.in)) {
            int scelta;

            do {
                mostraMenu();

                if (scanner.hasNextInt()) {
                    scelta = scanner.nextInt();

                    switch (scelta) {
                        case 0:
                            System.out.println("Chiusura della demo. Arrivederci!");
                            break;

                        case 1: visualizzaCatalogo(gestoreProdotti);
                        		break;
                        case 2: cercaProdotto(gestoreProdotti, scanner);
                        		break;
                        case 3: aggiungiProdottoDemo(gestoreProdotti, scanner);
                        		break;
                        case 4:
                        case 5:
                        case 6:
                        case 7:
                            System.out.println("Funzionalità da collegare nel prossimo passaggio.");
                            break;

                        default: System.out.println("Scelta non valida. Riprova.");
                    }
                } else {
                    System.out.println("Inserisci un numero valido.");
                    scanner.next();
                    scelta = -1;
                }

            } while (scelta != 0);
        }
    }
    
    
    
    private static void visualizzaCatalogo(GestoreProdotti gestoreProdotti) {

        System.out.println("\n========== CATALOGO PRODOTTI ==========");

        List<Prodotto> prodotti = gestoreProdotti.visualizzaProdotti();

        if (prodotti == null || prodotti.isEmpty()) {
            System.out.println("Nessun prodotto presente nel catalogo.");
        } else {
            for (Prodotto prodotto : prodotti) {
                System.out.println(
                    "Codice: " + prodotto.getCodiceABarre()
                    + " | Nome: " + prodotto.getNome()
        			+ " | Prezzo: " + String.format(java.util.Locale.ITALY, "%.2f", prodotto.getPrezzo()) + " €"
                    + " | Scaffale: " + prodotto.getQuantitaScaffale()
                    + " | Magazzino: " + prodotto.getQuantitaMagazzino()
                );
            }
        }

        System.out.println("=======================================\n");
    }
    
    
    
    private static void cercaProdotto(GestoreProdotti gestoreProdotti, Scanner scanner) {

    	scanner.nextLine();
    	
    	System.out.print("Inserisci il codice a barre del prodotto: ");
        String codice = scanner.nextLine().trim();

        Prodotto prodotto = gestoreProdotti.cercaProdotto(codice);

        if (prodotto == null) {
            System.out.println("Prodotto non trovato.");
        } else {
            System.out.println("\n========== PRODOTTO TROVATO ==========");
            System.out.println("Codice: " + prodotto.getCodiceABarre());
            System.out.println("Nome: " + prodotto.getNome());
            System.out.println("Prezzo: " + String.format(java.util.Locale.ITALY, "%.2f", prodotto.getPrezzo()) + " €");
            System.out.println("Quantità scaffale: " + prodotto.getQuantitaScaffale());
            System.out.println("Quantità magazzino: " + prodotto.getQuantitaMagazzino());
            System.out.println("======================================\n");
        }
    }
    
    
    
	private static void aggiungiProdottoDemo(GestoreProdotti gestoreProdotti, Scanner scanner) {
	
	    scanner.nextLine();
	
	    System.out.println("\n========== AGGIUNGI PRODOTTO ==========");
	
	    System.out.print("Codice a barre: ");
	    String codice = scanner.nextLine().trim();
	
	    if (codice.isEmpty()) {
	        System.out.println("Il codice a barre non può essere vuoto.");
	        return;
	    }
	
	    if (gestoreProdotti.cercaProdotto(codice) != null) {
	        System.out.println("Esiste già un prodotto con questo codice.");
	        return;
	    }
	
	    System.out.print("Nome: ");
	    String nome = scanner.nextLine().trim();
	
	    if (nome.isEmpty()) {
	        System.out.println("Il nome non può essere vuoto.");
	        return;
	    }
	
	    try {
	        System.out.print("Prezzo (€): ");
	        double prezzo = Double.parseDouble(scanner.nextLine().trim().replace(',', '.'));
	
	        System.out.print("Descrizione: ");
	        String descrizione = scanner.nextLine().trim();
	
	        System.out.print("Sconto (%): ");
	        double sconto = Double.parseDouble(scanner.nextLine().trim().replace(',', '.'));
	
	        System.out.print("Quantità sullo scaffale: ");
	        int quantitaScaffale = Integer.parseInt(scanner.nextLine().trim());
	
	        System.out.print("Quantità in magazzino: ");
	        int quantitaMagazzino = Integer.parseInt(scanner.nextLine().trim());
	
	        System.out.print("Numero reparto: ");
	        int numeroReparto = Integer.parseInt(scanner.nextLine().trim());
	
	        if (prezzo < 0 || sconto < 0 || sconto > 100 || quantitaScaffale < 0 || quantitaMagazzino < 0 || numeroReparto <= 0) {
	            System.out.println("Uno o più valori inseriti non sono validi.");
	            return;
	        }
	
	        Prodotto prodotto = new Prodotto(
	            codice,
	            nome,
	            prezzo,
	            descrizione,
	            sconto,
	            quantitaScaffale,
	            quantitaMagazzino,
	            numeroReparto
	        );
	
	        gestoreProdotti.aggiungiProdotto(prodotto);
	
	        System.out.println("Prodotto aggiunto correttamente!");
	
	    } catch (NumberFormatException e) {
	        System.out.println("Errore: inserisci numeri validi per prezzo, sconto e quantità.");
	    } catch (RuntimeException e) {
	        System.out.println("Impossibile salvare il prodotto: " + e.getMessage());
	    }
	
	    System.out.println("========================================\n");
	}
    
    
    
    private static void mostraMenu() {
        System.out.println("\n====================================");
        System.out.println("       SUPERMERCATO - DEMO");
        System.out.println("====================================");
        System.out.println("1. Visualizza catalogo prodotti");
        System.out.println("2. Cerca un prodotto");
        System.out.println("3. Aggiungi un prodotto");
        System.out.println("4. Simula una spesa");
        System.out.println("5. Visualizza le scorte");
        System.out.println("6. Crea un ordine al fornitore");
        System.out.println("7. Visualizza gli ordini salvati");
        System.out.println("0. Esci");
        System.out.println("====================================");
        System.out.print("Seleziona un'operazione: ");
    }
}
