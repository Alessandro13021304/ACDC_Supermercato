package unibg.it.Supermercato;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.sql.Connection;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Scanner;
import java.util.Locale;

import unibg.it.Supermercato.persistenzadati.*;
import unibg.it.Supermercato.sistemaaccesso.*;
import unibg.it.Supermercato.gestionemagazzino.*;
import unibg.it.Supermercato.gestionedipendenti.*;
import unibg.it.Supermercato.selfscanning.*;
import unibg.it.Supermercato.pagamentiesterni.*;
import unibg.it.Supermercato.gestionefedelta.*;

public class App {

    private static final Logger logger = LogManager.getLogger(App.class);

    public static void main(String[] args) {

        logger.info("Avvio dell'applicazione Supermercato.");

        DBConnection db = DBConnection.getInstance();
        
        DBClienti dbClienti = new DBClienti();
        DBDipendenti dbDipendenti = new DBDipendenti();
        DBProdotti dbProdotti = new DBProdotti();
        DBOrdini dbOrdini = new DBOrdini();
        
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

        Totem totem = new Totem(1, new PaymentGateway());
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
              
        Fornitore fornitore = new Fornitore("Fornitore Demo");        
        GestoreOrdini gestoreOrdini = new GestoreOrdini(dbOrdini);
               
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
                        case 4: visualizzaScorte(gestoreProdotti);
                				break;
                        case 5: creaOrdineDemo(gestoreOrdini, gestoreProdotti, fornitore, scanner);
                            	break;
                        case 6: visualizzaOrdini(gestoreOrdini);
                        		break;                        
                        case 7: registraCliente(dbClienti, scanner);
                        		break;
                        case 8: autenticaCliente(authService, dbClienti, scanner);
                        		break;
                        case 9: registraDipendente(dbDipendenti, dbClienti, scanner);
                        		break;
                        case 10: autenticaDipendente(authService, scanner);
                				 break;
                        case 11: autenticaManager(authService, dbDipendenti, scanner);
                        		 break;
                        case 12: simulaSpesa(authService, dbClienti, dbProdotti, totem, carrelloFisico, scanner);
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
    
    
    
    private static void mostraMenu() {
        System.out.println("\n====================================");
        System.out.println("       SUPERMERCATO - DEMO");
        System.out.println("====================================");
        System.out.println("1. Visualizza catalogo prodotti");
        System.out.println("2. Cerca un prodotto");
        System.out.println("3. Aggiungi un prodotto");
        System.out.println("4. Visualizza le scorte");
        System.out.println("5. Crea un ordine al fornitore");
        System.out.println("6. Visualizza gli ordini salvati");
        System.out.println("7. Registrazione cliente");
        System.out.println("8. Autenticazione cliente");
        System.out.println("9. Registrazione dipendente");
        System.out.println("10. Autenticazione dipendente e visualizzazione dei turni");
        System.out.println("11. Autenticazione manager e gestione turni");
        System.out.println("12. Simula una spesa");
        System.out.println("0. Esci");
        System.out.println("====================================");
        System.out.print("Seleziona un'operazione: ");
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
        			+ " | Prezzo: € " + String.format(Locale.ITALY, "%.2f", prodotto.getPrezzo())
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
	
	    String codice;
	
	    while (true) {
	        codice = leggiTestoNonVuoto(scanner, "Codice a barre: ");
	
	        if (gestoreProdotti.cercaProdotto(codice) != null) {
	            System.out.println("Codice già esistente. Inseriscine un altro.");
	        } else {
	            break;
	        }
	    }
	
	    String nome = leggiTestoNonVuoto(scanner, "Nome: ");
	
	    String descrizione = leggiTestoNonVuoto(scanner, "Descrizione: ");
	
	    double prezzo = leggiPrezzo(scanner);
	
	    double sconto = leggiPercentuale(scanner);
	
	    int quantitaScaffale = leggiInteroNonNegativo(scanner, "Quantità sullo scaffale: ");
	
	    int quantitaMagazzino = leggiInteroNonNegativo(scanner, "Quantità in magazzino: ");
	
	    int numeroReparto = leggiInteroPositivo(scanner, "Numero reparto: ");
	
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
	
	    try {
	        gestoreProdotti.aggiungiProdotto(prodotto);
	        System.out.println("Prodotto aggiunto correttamente!");
	        
	    } catch (RuntimeException e) {
	        System.out.println("Impossibile salvare il prodotto: " + e.getMessage());
	    }
	
	    System.out.println("========================================\n");
	}
	
	
	
	private static void visualizzaScorte(GestoreProdotti gestoreProdotti) {
		
	    List<Prodotto> prodotti = gestoreProdotti.visualizzaProdotti();
	
	    System.out.println("\n========== SITUAZIONE SCORTE ==========");
	
	    if (prodotti == null || prodotti.isEmpty()) {
	        System.out.println("Nessun prodotto presente.");
	        System.out.println("=======================================\n");
	        return;
	    }
	
	    boolean scorteInsufficienti = false;
	
	    for (Prodotto prodotto : prodotti) {
	        System.out.println(
	            "Prodotto: " + prodotto.getNome()
	            + " | Scaffale: " + prodotto.getQuantitaScaffale()
	            + " | Magazzino: " + prodotto.getQuantitaMagazzino()
	        );
	
	        if (prodotto.getQuantitaScaffale() < 5 && prodotto.getQuantitaMagazzino() < 5) {
	            System.out.println("ATTENZIONE: scorte insufficienti, prodotto da riordinare.");
	            scorteInsufficienti = true;
	        }
	    }
	
	    if (!scorteInsufficienti) {
	        System.out.println("Nessun prodotto sotto la soglia di riordino.");
	    }
	
	    System.out.println("=======================================\n");
	}

	
	
	private static void creaOrdineDemo(GestoreOrdini gestoreOrdini, GestoreProdotti gestoreProdotti, Fornitore fornitore, Scanner scanner) {
	
	    System.out.println("\n========== NUOVO ORDINE FORNITORE ==========");
	
	    OrdineFornitore ordine = gestoreOrdini.creaOrdine(fornitore);
	
	    if (ordine == null) {
	        System.out.println("Impossibile creare l'ordine.");
	        return;
	    }
	
	    boolean aggiungiAltro = true;
	    int numeroRighe = 0;
	
	    while (aggiungiAltro) {
	        String codice = leggiTestoNonVuoto(scanner, "Codice a barre del prodotto: ");
	
	        Prodotto prodotto = gestoreProdotti.cercaProdotto(codice);
	
	        if (prodotto == null) {
	            System.out.println("Prodotto non trovato. Riprova.");
	            continue;
	        }
	
	        int quantita = leggiInteroPositivo(scanner, "Quantità da ordinare: ");
	
	        gestoreOrdini.aggiungiProdotto(ordine, prodotto, quantita);
	        numeroRighe++;
	
	        String risposta;

	        while (true) {
	            System.out.print("Vuoi aggiungere un altro prodotto? (s/n): ");
	            risposta = scanner.nextLine().trim();

	            if (risposta.equalsIgnoreCase("s")) {
	                aggiungiAltro = true;
	                break;
	                
	            } else if (risposta.equalsIgnoreCase("n")) {
	                aggiungiAltro = false;
	                break;
	                
	            } else {
	                System.out.println("Risposta non valida. Inserisci s oppure n.");
	            }
	        }
	    }
	
	    if (numeroRighe == 0) {
	        System.out.println("Ordine annullato: non sono stati aggiunti prodotti.");
	        return;
	    }
	
	    try {
	        gestoreOrdini.salvaOrdine(ordine);
	
	        System.out.println("\nOrdine salvato correttamente!");
	        System.out.println("Fornitore: " + fornitore.getNome());
	
	        System.out.printf(java.util.Locale.ITALY, "Totale ordine: %.2f €%n", ordine.getTotale());
	
	    } catch (RuntimeException e) {
	        System.out.println("Errore durante il salvataggio dell'ordine.");
	        logger.error("Errore durante il salvataggio dell'ordine.", e);
	    }
	
	    System.out.println("============================================\n");
	}

	

	private static void visualizzaOrdini(GestoreOrdini gestoreOrdini) {
	
	    List<OrdineFornitore> ordiniSalvati = gestoreOrdini.visualizzaOrdini();
	
	    System.out.println("\n========== ORDINI SALVATI ==========");
	
	    if (ordiniSalvati.isEmpty()) {
	        System.out.println("Non ci sono ordini salvati.");
	        
	    } else {
	    	System.out.println("Numero totale di ordini: " + ordiniSalvati.size() + "\n");
	    	
	        for (OrdineFornitore ordineSalvato : ordiniSalvati) {
	            System.out.println("Fornitore: " + ordineSalvato.getFornitore().getNome());
	            System.out.println("Data ordine: " + ordineSalvato.getDataOrdine());
	            System.out.println("Stato: " + ordineSalvato.getStato());
	            System.out.printf(java.util.Locale.ITALY, "Totale: %.2f €%n", ordineSalvato.getTotale());
	
	            for (RigaOrdine riga : ordineSalvato.getRighe()) {
	                System.out.printf(java.util.Locale.ITALY, "Prodotto: %s | Quantità: %d | Prezzo unitario: %.2f €%n", riga.getProdotto().getNome(), riga.getQuantita(), riga.getProdotto().getPrezzo());
	            }
	
	            System.out.println("------------------------------------");
	        }
	    }
	
	    System.out.println("====================================\n");
	}
	

	
	private static void registraCliente(DBClienti dbClienti, Scanner scanner) {
	
	    scanner.nextLine();
	
	    System.out.println("\n========== REGISTRAZIONE CLIENTE ==========");
	
	    String nome = leggiTestoNonVuoto(scanner, "Nome: ");
	    String cognome = leggiTestoNonVuoto(scanner, "Cognome: ");
	    
	    String email;

	    while (true) {
	        email = leggiTestoNonVuoto(scanner, "Email: ");

	        boolean emailEsistente = false;

	        for (Cliente clienteEsistente : dbClienti.trovaTuttiIClienti()) {
	            if (clienteEsistente.getEmail().equalsIgnoreCase(email)) {
	                emailEsistente = true;
	                break;
	            }
	        }

	        if (emailEsistente) {
	            System.out.println(
	                "Questa email è già registrata. Inserisci un altro indirizzo email."
	            );
	        } else {
	            break;
	        }
	    }
	    
	    String password = leggiTestoNonVuoto(scanner, "Password: ");
	
	    LocalDate dataNascita;
	
	    while (true) {
	        System.out.print("Data di nascita (AAAA-MM-GG): ");
	
	        try {
	            dataNascita = LocalDate.parse(scanner.nextLine().trim());
	
	            if (!dataNascita.isBefore(LocalDate.now())) {
	                System.out.println("La data di nascita deve essere nel passato.");
	                continue;
	            }
	
	            break;
	
	        } catch (java.time.format.DateTimeParseException e) {
	            System.out.println("Data non valida. Usa il formato AAAA-MM-GG.");
	        }
	    }
	
	    Cliente cliente = new Cliente(
	        nome,
	        cognome,
	        email,
	        password,
	        dataNascita
	    );
	
	    try {
	        dbClienti.salvaCliente(cliente);
	
	        System.out.println("\nCliente registrato correttamente!");
	        System.out.println("ID cliente: " + cliente.getId());
	        System.out.println("Nome e cognome: " + cliente.getNome() + " " + cliente.getCognome());
	        System.out.println("Email: " + cliente.getEmail());
	
	    } catch (RuntimeException e) {
	        System.out.println("Errore durante la registrazione del cliente.");
	        logger.error("Errore durante la registrazione del cliente.", e);
	    }
	
	    System.out.println("===========================================\n");
	}
	
		

	private static void autenticaCliente(AuthService authService, DBClienti dbClienti, Scanner scanner) {
	
	    scanner.nextLine();
	
	    System.out.println("\n========== AUTENTICAZIONE CLIENTE ==========");
	    String email = leggiTestoNonVuoto(scanner, "Email: ");
	    String password = leggiTestoNonVuoto(scanner, "Password: ");
	
	    try {
	        Persona persona = authService.autentica(email, password);
	
	        if (persona == null) {
	            System.out.println("\nCredenziali non valide.");
	        } else if (persona instanceof Cliente) {
	            Cliente cliente = (Cliente) persona;
	
	            System.out.println("\nAutenticazione riuscita!");
	            areaCliente(cliente, dbClienti, scanner);
	
	        } else {
	            System.out.println("\nQueste credenziali appartengono a un dipendente, non a un cliente.");
	        }
	
	    } catch (RuntimeException e) {
	        System.out.println("Errore durante l'autenticazione.");
	        logger.error("Errore durante l'autenticazione del cliente.", e);
	    }
	
	    System.out.println("============================================\n");
	}
	
	
	
	private static void areaCliente(Cliente cliente, DBClienti dbClienti, Scanner scanner) {

		System.out.println("\n========== AREA CLIENTE ==========");
        System.out.println("Benvenuto/a, " + cliente.getNome() + " " + cliente.getCognome());
		
		String scelta;

	    do {	        
	        System.out.println("\n1. Visualizza punti fedeltà");
	        System.out.println("2. Visualizza storico spese");
	        System.out.println("3. Visualizza segnalazioni");
	        System.out.println("0. Esci");
	        System.out.print("Scelta: ");

	        scelta = scanner.nextLine().trim();

	        switch (scelta) {

	            case "1": System.out.println("\nPunti fedeltà disponibili: " + cliente.visualizzaPunti());
	                	  break;

	            case "2": visualizzaStoricoSpese(cliente, dbClienti);
	            		  break;
	            		  
	            case "3": visualizzaSegnalazioni(cliente, dbClienti);
	            		  break;

	            case "0": System.out.println("Uscita dall'area cliente.");
	                	  break;

	            default: System.out.println("Scelta non valida. Riprova.");
	        }

	    } while (!scelta.equals("0"));
	}	
	
	
	
	private static void visualizzaStoricoSpese(Cliente cliente, DBClienti dbClienti) {

	    List<AcquistoStorico> storico = dbClienti.trovaStoricoByCliente(cliente.getId());

	    System.out.println("\n========== STORICO DELLE SPESE ==========");

	    if (storico.isEmpty()) {
	        System.out.println("Non hai ancora effettuato acquisti.");
	    } else {

	        for (AcquistoStorico acquisto : storico) {

	            System.out.println("\nData: " + acquisto.getData().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));

	            System.out.printf(Locale.ITALY, "Totale pagato: € %.2f%n", acquisto.getTotale());
	        }
	    }

	    System.out.println("\n==========================================\n");
	}
	
	
	
	private static void visualizzaSegnalazioni(Cliente cliente, DBClienti dbClienti) {

	    List<Segnalazione> segnalazioni = dbClienti.trovaSegnalazioniByCliente(cliente.getId());

	    System.out.println("\n========== LE MIE SEGNALAZIONI ==========");

	    if (segnalazioni.isEmpty()) {
	        System.out.println("Non hai segnalazioni registrate.");
	    } else {

	        for (int i = 0; i < segnalazioni.size(); i++) {

	            Segnalazione segnalazione = segnalazioni.get(i);

	            String data = segnalazione.getDataOra().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));

	            System.out.println("\nSegnalazione " + (i + 1));
	            System.out.println("Data e ora: " + data);
	        }
	    }

	    System.out.println("\n==========================================");
	}
	
	
	

	private static void registraDipendente(DBDipendenti dbDipendenti, DBClienti dbClienti, Scanner scanner) {
	
	    scanner.nextLine();
	
	    System.out.println("\n========== REGISTRAZIONE DIPENDENTE ==========");
	
	    String ruolo;
	
	    while (true) {
	        System.out.println("Ruoli disponibili:");
	        System.out.println("1. Manager");
	        System.out.println("2. Cassiere");
	        System.out.println("3. Magazziniere");
	        System.out.println("4. Responsabile di reparto");
	        System.out.print("Seleziona il ruolo: ");
	
	        String sceltaRuolo = scanner.nextLine().trim();
	
	        switch (sceltaRuolo) {
	            case "1": ruolo = "Manager";
	                	  break;
	            case "2": ruolo = "Cassiere";
	                	  break;
	            case "3": ruolo = "Magazziniere";
	                	  break;
	            case "4": ruolo = "ResponsabileDiReparto";
	                	  break;
	            default: System.out.println("Scelta non valida. Riprova.");
	                     continue;
	        }
	
	        break;
	    }
	
	    String matricola;
	
	    while (true) {
	        matricola = leggiTestoNonVuoto(scanner, "Matricola: ");
	
	        if (dbDipendenti.trovaDipendenteByMatricola(matricola) != null) {
	            System.out.println("Matricola già utilizzata. Inseriscine un'altra.");
	            
	        } else {
	            break;
	        }
	    }
	
	    String nome = leggiTestoNonVuoto(scanner, "Nome: ");
	    String cognome = leggiTestoNonVuoto(scanner, "Cognome: ");
	
	    String email;
	
	    while (true) {
	        email = leggiTestoNonVuoto(scanner, "Email: ");
	
	        boolean emailEsistente = false;
	
	        for (Cliente cliente : dbClienti.trovaTuttiIClienti()) {
	            if (cliente.getEmail().equalsIgnoreCase(email)) {
	                emailEsistente = true;
	                break;
	            }
	        }
	
	        if (!emailEsistente) {
	            for (Dipendente dipendente : dbDipendenti.trovaTuttiDipendenti()) {
	                if (dipendente.getEmail().equalsIgnoreCase(email)) {
	                    emailEsistente = true;
	                    break;
	                }
	            }
	        }
	
	        if (emailEsistente) {
	            System.out.println("Email già utilizzata. Inseriscine un'altra.");
	            
	        } else {
	            break;
	        }
	    }
	
	    String password = leggiTestoNonVuoto(scanner, "Password: ");
	
	    LocalDate dataNascita;
	
	    while (true) {
	        System.out.print("Data di nascita (AAAA-MM-GG): ");
	
	        try {
	            dataNascita = LocalDate.parse(scanner.nextLine().trim());
	
	            if (!dataNascita.isBefore(LocalDate.now())) {
	                System.out.println("La data di nascita deve essere nel passato.");
	                continue;
	            }
	
	            break;
	
	        } catch (java.time.format.DateTimeParseException e) {
	            System.out.println("Data non valida. Usa il formato AAAA-MM-GG.");
	        }
	    }
	
	    Dipendente dipendente;
	
	    switch (ruolo) {
	        case "Manager": dipendente = new Manager(nome, cognome, email, password, dataNascita, dbDipendenti);
	            		    break;
	
	        case "Cassiere": dipendente = new Cassiere(nome, cognome, email, password, dataNascita, dbClienti, new ControlloSpesa());
	            			 break;
	
	        case "Magazziniere": dipendente = new Magazziniere(nome, cognome, email, password, dataNascita);
	        					 break;
	
	        default: dipendente = new ResponsabileDiReparto(nome, cognome, email, password, dataNascita);
	            	 break;
	    }
	
	    dipendente.setMatricola(matricola);
	
	    try {
	        dbDipendenti.salvaDipendente(dipendente);
	
	        System.out.println("\nDipendente registrato correttamente!");
	        System.out.println("Matricola: " + dipendente.getMatricola());
	        System.out.println("Nome e cognome: " + dipendente.getNome() + " " + dipendente.getCognome());
	        System.out.println("Email: " + dipendente.getEmail());
	        System.out.println("Ruolo: " + dipendente.getRuolo());
	
	    } catch (RuntimeException e) {
	        System.out.println("Errore durante la registrazione.");
	        logger.error("Errore durante la registrazione del dipendente.", e);
	    }
	
	    System.out.println("===============================================\n");
	}
	
	

	private static void autenticaDipendente(AuthService authService, Scanner scanner) {
	
	    scanner.nextLine();
	
	    System.out.println("\n========== AUTENTICAZIONE DIPENDENTE ==========");
	    String email = leggiTestoNonVuoto(scanner, "Email: ");
	    String password = leggiTestoNonVuoto(scanner, "Password: ");
	
	    try {
	        Persona persona = authService.autentica(email, password);
	
	        if (persona == null) {
	            System.out.println("\nCredenziali non valide.");
	        } else if (persona instanceof Dipendente) {
	            Dipendente dipendente = (Dipendente) persona;
	
	            System.out.println("\nAutenticazione riuscita!");
	            System.out.println("Benvenuto/a, " + dipendente.getNome() + " " + dipendente.getCognome());
	            System.out.println("Ruolo: " + dipendente.getRuolo());
	            System.out.println("Matricola: " + dipendente.getMatricola());
	            
	            visualizzaTurniDipendente(dipendente);
	
	        } else {
	            System.out.println("\nQueste credenziali appartengono a un cliente, non a un dipendente.");
	        }
	
	    } catch (RuntimeException e) {
	        System.out.println("Errore durante l'autenticazione.");
	        logger.error("Errore durante l'autenticazione del dipendente.", e);
	    }
	
	    System.out.println("================================================\n");
	}
	
	
	
	private static void visualizzaTurniDipendente(Dipendente dipendente) {

	    List<Turno> turni = dipendente.visualizzaTurni();

	    System.out.println("\n========== I MIEI TURNI ==========");

	    if (turni.isEmpty()) {
	        System.out.println("Non hai turni assegnati.");
	    } else {
	        for (Turno turno : turni) {

	            System.out.println("\nData: " + turno.getData());
	            System.out.println("Orario previsto: " + turno.getOrarioInizio() + " - " + turno.getOrarioFine());

	            System.out.println("Ingresso effettivo: " + (turno.getOraIngresso() != null ? turno.getOraIngresso() : "Non registrato"));

	            System.out.println("Uscita effettiva: " + (turno.getOraUscita() != null ? turno.getOraUscita() : "Non registrata"));

	            System.out.println("Completato: " + (turno.isCompletato() ? "Sì" : "No"));
	        }
	    }
	}
	
	
	
	private static void autenticaManager(AuthService authService, DBDipendenti dbDipendenti, Scanner scanner) {

	    scanner.nextLine();

	    System.out.println("\n========== AUTENTICAZIONE MANAGER ==========");

	    String email = leggiTestoNonVuoto(scanner, "Email: ");
	    String password = leggiTestoNonVuoto(scanner, "Password: ");

	    try {
	        Persona persona = authService.autentica(email, password);

	        if (persona instanceof Manager) {
	            Manager manager = (Manager) persona;

	            System.out.println("\nAutenticazione riuscita!");
	            System.out.println("Benvenuto/a, " + manager.getNome() + " " + manager.getCognome());

	            gestioneTurniManager(manager, dbDipendenti, scanner);

	        } else {
	            System.out.println("\nCredenziali non valide.");
	        }

	    } catch (RuntimeException e) {
	        System.out.println("Errore durante l'autenticazione.");
	        logger.error("Errore durante l'autenticazione del Manager.", e);
	    }

	    System.out.println("============================================\n");
	}
	
	
	
	private static void gestioneTurniManager(Manager manager, DBDipendenti dbDipendenti, Scanner scanner) {

	    String scelta;

	    do {
	        System.out.println("\n========== GESTIONE TURNI ==========");
	        System.out.println("1. Assegna un turno a un dipendente");
	        System.out.println("0. Torna indietro");
	        System.out.print("Scelta: ");

	        scelta = scanner.nextLine().trim();

	        switch (scelta) {

	            case "1":

	                List<Dipendente> dipendenti = dbDipendenti.trovaTuttiDipendenti();

	                if (dipendenti.isEmpty()) {
	                    System.out.println("Nessun dipendente registrato.");
	                    break;
	                }

	                System.out.println("\n--- DIPENDENTI REGISTRATI ---");

	                for (Dipendente d : dipendenti) {
	                    System.out.println(
	                            "Matricola: " + d.getMatricola()
	                            + " | Nome: " + d.getNome()
	                            + " " + d.getCognome()
	                            + " | Ruolo: " + d.getRuolo()
	                    );
	                }

	                String matricola;
	                Dipendente dipendente;

	                while (true) {

	                    matricola = leggiTestoNonVuoto(
	                            scanner, "\nMatricola del dipendente: ");

	                    dipendente = dbDipendenti.trovaDipendenteByMatricola(matricola);

	                    if (dipendente == null) {
	                        System.out.println(
	                                "Dipendente non trovato. Inserisci nuovamente la matricola.");
	                        continue;
	                    }

	                    break;
	                }

	                LocalDate data;

	                while (true) {
	                    System.out.print("Data del turno (AAAA-MM-GG): ");

	                    try {
	                        data = LocalDate.parse(scanner.nextLine().trim());

	                        if (data.isBefore(LocalDate.now())) {
	                            System.out.println(
	                                    "Non è possibile assegnare turni nel passato. Riprova.");
	                            continue;
	                        }

	                        break;

	                    } catch (java.time.format.DateTimeParseException e) {
	                        System.out.println(
	                                "Data non valida. Usa il formato AAAA-MM-GG.");
	                    }
	                }

	                LocalTime inizio;
	                LocalTime fine;

	                while (true) {
	                    try {
	                        System.out.print("Ora di inizio (HH:MM): ");
	                        inizio = LocalTime.parse(scanner.nextLine().trim());

	                        System.out.print("Ora di fine (HH:MM): ");
	                        fine = LocalTime.parse(scanner.nextLine().trim());

	                        if (!inizio.isBefore(fine)) {
	                            System.out.println(
	                                    "L'ora di inizio deve precedere quella di fine. Riprova.\n");
	                            continue;
	                        }

	                        break;

	                    } catch (java.time.format.DateTimeParseException e) {
	                        System.out.println(
	                                "Orario non valido. Usa il formato HH:MM. Riprova.");
	                    }
	                }

	                Turno turno = new Turno(data, inizio, fine);

	                List<Turno> turniPrima = dbDipendenti.trovaTurniByMatricola(matricola);

	                manager.assegnaTurno(dipendente, turno);

	                List<Turno> turniDopo = dbDipendenti.trovaTurniByMatricola(matricola);

	                if (turniDopo.size() > turniPrima.size()) {
	                    System.out.println("\nTurno assegnato correttamente!");
	                } else {
	                    System.out.println("\nImpossibile assegnare il turno: si sovrappone a un turno già esistente.");
	                }                
	                break;
	            
	            case "0":
	                System.out.println("Uscita dalla gestione turni.");
	                break;

	            default:
	                System.out.println("Scelta non valida.");
	        }

	    } while (!scelta.equals("0"));
	}
	
	

	private static void simulaSpesa(AuthService authService, IRepositoryClienti dbClienti, IRepositoryProdotti dbProdotti, Totem totem, CarrelloFisico carrelloFisico, Scanner scanner) {
	
	    scanner.nextLine();
	    
	    carrelloFisico.svuota();
	
	    System.out.println("\n========== NUOVA SPESA ==========");
	
	    System.out.println("Autenticazione cliente - Inserisci le credenziali");
	
	    String email = leggiTestoNonVuoto(scanner, "Email: ");
	    String password = leggiTestoNonVuoto(scanner, "Password: ");
	
	    LettoreOttico lettore = new LettoreOttico(authService, null);
	
	    Cliente cliente = lettore.autentica(email, password);
	
	    if (cliente == null) {
	        System.out.println("\nAutenticazione fallita. Accesso consentito solo ai clienti.");
	        return;
	    }
	
	    System.out.println("\nBenvenuto/a, " + cliente.getNome() + " " + cliente.getCognome());
	
	    SessioneSpesa sessione = new SessioneSpesa(cliente, authService);
	
	    sessione.setRepositoryClienti(dbClienti);
	    sessione.setRepositoryProdotti(dbProdotti);
	    sessione.setTotem(totem);
	
	    System.out.println("\nSessione di spesa avviata!");	    

		int scelta;
		
		do {
		    System.out.println("\n========== GESTIONE CARRELLO ==========");
		    System.out.println("1. Scansiona un prodotto");
		    System.out.println("2. Rimuovi un prodotto");
		    System.out.println("3. Visualizza carrello");
		    System.out.println("0. Termina inserimento prodotti");
		    System.out.print("Scelta: ");
		
		    while (!scanner.hasNextInt()) {
		        System.out.println("Inserisci un numero valido.");
		        scanner.next();
		        System.out.print("Scelta: ");
		    }
		
		    scelta = scanner.nextInt();
		    scanner.nextLine();
		
		    switch (scelta) {
		
		        case 1:
		            String codice = leggiTestoNonVuoto(scanner, "Codice a barre: ");
		
		            Prodotto prodotto = dbProdotti.trovaProdottoByCodiceABarre(codice);	

					if (prodotto != null) {
					    if (sessione.scansionaProdotto(prodotto)) {
					        System.out.println("Prodotto scansionato: " + prodotto.getNome());
					    } else {
					        System.out.println("Disponibilità sullo scaffale esaurita!");
					    }
					} else {
					    System.out.println("Prodotto non trovato.");
					}
		            break;
		
		        case 2:
		            String codiceRimozione = leggiTestoNonVuoto(scanner, "Codice a barre del prodotto da rimuovere: ");
		
		            Prodotto prodottoDaRimuovere = dbProdotti.trovaProdottoByCodiceABarre(codiceRimozione);
		
		            if (prodottoDaRimuovere != null) {
		                if (sessione.rimuoviProdotto(prodottoDaRimuovere)) {
		                    System.out.println("Prodotto rimosso correttamente.");
		                } else {
		                    System.out.println("Il prodotto non è presente nel carrello.");
		                }
		            } else {
		                System.out.println("Prodotto non trovato nel sistema.");
		            }
		            break;
		
		        case 3:
		            System.out.println("\n========== IL TUO CARRELLO ==========");
		
		            if (sessione.carrello.getElementi().isEmpty()) {
		                System.out.println("Il carrello è vuoto.");
		                
		            } else {
		                for (ElementoCarrello elemento : sessione.carrello.getElementi()) {
		                    System.out.println(
		                        elemento.prodotto.getNome()
		                        + " | Quantità: " + elemento.getQuantita()
		                        + " | Totale: " + elemento.calcolaTotaleRiga() + " euro"
		                    );
		                }
		
		                System.out.println("Totale provvisorio: " + sessione.carrello.calcolaTotale() + " euro");
		            }
		            break;
		
		        case 0:
		            if (sessione.carrello.getElementi().isEmpty()) {
		                System.out.println("Non puoi terminare una spesa con il carrello vuoto.");
		                scelta = -1;
		            }
		            break;
		
		        default:
		            System.out.println("Scelta non valida.");
		    }
		
		} while (scelta != 0);

		// Termine della spesa
		int puntiIniziali = sessione.getCliente().visualizzaPunti();
		boolean usaFedelta = false; 
		sessione.terminaSpesa();

		System.out.println("\n========== RIEPILOGO SPESA ==========");

		double totaleIniziale = sessione.getTotaleDaPagare();

		System.out.printf(Locale.ITALY, "Totale: € %.2f%n", totaleIniziale);

		// Programma fedeltà
		int puntiDisponibili = sessione.getCliente().visualizzaPunti();

		Promozione promozione = new Promozione();
		double percentualeSconto = promozione.calcolaSconto(puntiDisponibili);

		System.out.println("\n========== PROGRAMMA FEDELTÀ ==========");
		System.out.println("Punti disponibili: " + puntiDisponibili);

		if (percentualeSconto > 0) {

		    double valoreSconto = totaleIniziale * percentualeSconto / 100.0;

		    System.out.println("Sconto disponibile: " + percentualeSconto + "%");
		    System.out.printf(Locale.ITALY, "Risparmio: € %.2f%n", valoreSconto);

		    String risposta;

		    do {
		        System.out.print("\nVuoi utilizzare il premio fedeltà? (s/n): ");
		        risposta = scanner.nextLine().trim();

		        if (!risposta.equalsIgnoreCase("s") &&
		            !risposta.equalsIgnoreCase("n")) {
		            System.out.println("Risposta non valida. Inserisci s oppure n.");
		        }

		    } while (!risposta.equalsIgnoreCase("s") && !risposta.equalsIgnoreCase("n"));

		    if (risposta.equalsIgnoreCase("s")) {

		        if (sessione.usaPremioFedelta()) {
		        	usaFedelta = true;
		            System.out.println("Premio fedeltà applicato!");
		            System.out.printf(Locale.ITALY, "Sconto applicato: € %.2f%n", valoreSconto);
		        }

		    }

		} else {
		    System.out.println("Non hai ancora punti sufficienti per ottenere un premio.");
		}

		System.out.printf(Locale.ITALY, "Totale da pagare: € %.2f%n", sessione.getTotaleDaPagare());

		// Scelta modalità di pagamento
		System.out.println("\n========== MODALITÀ DI PAGAMENTO ==========");
		System.out.println("1. Contanti");
		System.out.println("2. Carta di credito");
		System.out.println("3. Carta di debito");

		int modalitaScelta;

		do {
		    System.out.print("Seleziona la modalità di pagamento: ");

		    while (!scanner.hasNextInt()) {
		        System.out.println("Inserisci un numero valido.");
		        scanner.next();
		        System.out.print("Seleziona la modalità di pagamento: ");
		    }

		    modalitaScelta = scanner.nextInt();
		    scanner.nextLine();

		} while (modalitaScelta < 1 || modalitaScelta > 3);

		EModalitaPagamento modalita;

		switch (modalitaScelta) {
		    case 1: modalita = EModalitaPagamento.CONTANTI;
		            break;
		    case 2: modalita = EModalitaPagamento.CARTA_DI_CREDITO;
		    		break;
		    default: modalita = EModalitaPagamento.CARTA_DI_DEBITO;
		    		 break;
		}

		boolean pagamentoRiuscito = sessione.effettuaPagamento(modalita);

		if (pagamentoRiuscito) {
		    System.out.println("\nPagamento accettato!");
		
		    if (sessione.getStato() == EStatoSessione.CONTROLLO) {
		
		    	System.out.println("\n========== CONTROLLO DELLA SPESA ==========");
		    	System.out.println("1. Controllo normale");
		    	System.out.println("2. Simula una discrepanza");

		    	int sceltaControllo;

		    	do {
		    	    System.out.print("Seleziona un'opzione: ");

		    	    while (!scanner.hasNextInt()) {
		    	        System.out.println("Inserisci un numero valido.");
		    	        scanner.next();
		    	        System.out.print("Seleziona un'opzione: ");
		    	    }

		    	    sceltaControllo = scanner.nextInt();
		    	    scanner.nextLine();

		    	} while (sceltaControllo < 1 || sceltaControllo > 2);

		    	boolean discrepanzaInserita = false;

		    	for (ElementoCarrello elemento : sessione.carrello.getElementi()) {

		    	    int quantitaFisica = elemento.getQuantita();

		    	    if (sceltaControllo == 2 && !discrepanzaInserita) {
		    	        quantitaFisica++;
		    	        discrepanzaInserita = true;
		    	    }

		    	    ElementoCarrello copia = new ElementoCarrello(
		    	        elemento.prodotto,
		    	        quantitaFisica,
		    	        elemento.calcolaTotaleRiga() / elemento.getQuantita()
		    	    );

		    	    carrelloFisico.aggiungiElemento(copia);
		    	}

		    	System.out.println("\nIl cassiere sta effettuando il controllo della spesa...");

		        boolean controlloSuperato = totem.effettuaControlloSpesa(sessione);
		
		        if (controlloSuperato) {
		            sessione.completaDopoControllo();		
		            System.out.println("\nControllo superato!");
		            
		            int puntiFinali = sessione.getCliente().visualizzaPunti();
		            int puntiGuadagnati = puntiFinali - puntiIniziali;

		            if (usaFedelta) {
		                puntiGuadagnati += promozione.calcolaPuntiDaScalare(puntiIniziali);
		            }

			        System.out.println("\n" + sessione.generaRicevuta());
			        System.out.println("Punti fedeltà guadagnati con questa spesa: " + puntiGuadagnati);
			        System.out.println("Punti fedeltà totali: " + puntiFinali);
		
		        } else {
		            sessione.segnala();
		            System.out.println("\nControllo non superato. La spesa è stata segnalata.");
		            
		        }		
		    } else if (sessione.getStato() == EStatoSessione.COMPLETATA) {		
		       		        
		        int puntiFinali = sessione.getCliente().visualizzaPunti();
		        int puntiGuadagnati = puntiFinali - puntiIniziali;

		        if (usaFedelta) {
		            puntiGuadagnati += promozione.calcolaPuntiDaScalare(puntiIniziali);
		        }

		        System.out.println("\n" + sessione.generaRicevuta());
		        System.out.println("Punti fedeltà guadagnati con questa spesa: " + puntiGuadagnati);
		        System.out.println("Punti fedeltà totali: " + puntiFinali);		        
		    }		
		} else {
		    System.out.println("\nPagamento non completato.");
		}
	}



	private static String leggiTestoNonVuoto(Scanner scanner, String messaggio) {
	
	    while (true) {
	        System.out.print(messaggio);
	        String valore = scanner.nextLine().trim();
	
	        if (!valore.isEmpty()) {
	            return valore;
	        }
	
	        System.out.println("Il campo non può essere vuoto. Riprova.");
	    }
	}
	
	private static double leggiPrezzo(Scanner scanner) {
	    while (true) {
	        System.out.print("Prezzo (€): ");
	
	        try {
	            double prezzo = Double.parseDouble(scanner.nextLine().trim().replace(',', '.'));
	
	            if (Double.isFinite(prezzo) && prezzo > 0) {
	                return prezzo;
	            }
	
	            System.out.println("Il prezzo deve essere maggiore di zero.");
	            
	        } catch (NumberFormatException e) {
	            System.out.println("Inserisci un prezzo numerico valido.");
	        }
	    }
	}
	
	private static double leggiPercentuale(Scanner scanner) {
	    while (true) {
	        System.out.print("Sconto (%): ");
	
	        try {
	            double sconto = Double.parseDouble(scanner.nextLine().trim().replace(',', '.'));
	
	            if (Double.isFinite(sconto) && sconto >= 0 && sconto <= 100) {
	                return sconto;
	            }
	
	            System.out.println("Lo sconto deve essere tra 0 e 100.");
	            
	        } catch (NumberFormatException e) {
	            System.out.println("Inserisci una percentuale valida.");
	        }
	    }
	}
	
	private static int leggiInteroNonNegativo(Scanner scanner, String messaggio) {
	
	    while (true) {
	        System.out.print(messaggio);
	
	        try {
	            int valore = Integer.parseInt(scanner.nextLine().trim());
	
	            if (valore >= 0) {
	                return valore;
	            }
	
	            System.out.println("Il valore non può essere negativo.");
	            
	        } catch (NumberFormatException e) {
	            System.out.println("Inserisci un numero intero valido.");
	        }
	    }
	}
	
	private static int leggiInteroPositivo(Scanner scanner, String messaggio) {
	
	    while (true) {
	        System.out.print(messaggio);
	
	        try {
	            int valore = Integer.parseInt(scanner.nextLine().trim());
	
	            if (valore > 0) {
	                return valore;
	            }
	
	            System.out.println("Il valore deve essere maggiore di zero.");
	            
	        } catch (NumberFormatException e) {
	            System.out.println("Inserisci un numero intero valido.");
	        }
	    }
	}
}
