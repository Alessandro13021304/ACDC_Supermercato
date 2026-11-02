package unibg.it.Supermercato.selfscanning;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.PreparedStatement;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import unibg.it.Supermercato.gestionemagazzino.Prodotto;
import unibg.it.Supermercato.sistemaaccesso.Cliente;
import unibg.it.Supermercato.sistemaaccesso.Cassiere;
import unibg.it.Supermercato.persistenzadati.DBClienti;
import unibg.it.Supermercato.persistenzadati.DBConnection;
import unibg.it.Supermercato.sistemaaccesso.IAutenticazione;
import unibg.it.Supermercato.sistemaaccesso.IRepositoryClienti;
import unibg.it.Supermercato.sistemaaccesso.Persona;
import unibg.it.Supermercato.pagamentiesterni.PaymentGateway;

class SessioneSpesaTest {

    private final IAutenticazione autenticazione = new IAutenticazione() {

        @Override
        public boolean registraUtente(Persona persona) {
            return false;
        }

        @Override
        public Persona autentica(String email, String password) {
            return null;
        }
    };

    @Test
    void scansionaProdottoAggiungeElementoAlCarrello() {

        Prodotto prodotto = new Prodotto(
                "123456",
                "Biscotti",
                2.50,
                "Biscotti al cioccolato",
                0.0,
                20,
                100,
                1
        );

        Cliente cliente = new Cliente(
                "Mario",
                "Rossi",
                "mario@email.it",
                "password",
                LocalDate.of(2000, 1, 1)
        );

        SessioneSpesa sessione = new SessioneSpesa(cliente, autenticazione);

        sessione.scansionaProdotto(prodotto);

        assertEquals(2.50, sessione.carrello.calcolaTotale(), 0.001);
    }

    @Test
    void scansionaDueVolteLoStessoProdottoIncrementaLaQuantita() {

        Prodotto prodotto = new Prodotto(
                "123456",
                "Biscotti",
                2.50,
                "Biscotti al cioccolato",
                0.0,
                20,
                100,
                1
        );

        Cliente cliente = new Cliente(
                "Mario",
                "Rossi",
                "mario@email.it",
                "password",
                LocalDate.of(2000, 1, 1)
        );

        SessioneSpesa sessione = new SessioneSpesa(cliente, autenticazione);

        sessione.scansionaProdotto(prodotto);
        sessione.scansionaProdotto(prodotto);

        assertEquals(5.00, sessione.carrello.calcolaTotale(), 0.001);
    }

    @Test
    void scansionaDueVolteLoStessoProdottoMantieneUnSoloElemento() {

        Prodotto prodotto = new Prodotto(
                "123456",
                "Biscotti",
                2.50,
                "Biscotti al cioccolato",
                0.0,
                20,
                100,
                1
        );

        Cliente cliente = new Cliente(
                "Mario",
                "Rossi",
                "mario@email.it",
                "password",
                LocalDate.of(2000, 1, 1)
        );

        SessioneSpesa sessione = new SessioneSpesa(cliente, autenticazione);

        sessione.scansionaProdotto(prodotto);
        sessione.scansionaProdotto(prodotto);

        assertEquals(1, sessione.carrello.numeroElementi());

        ElementoCarrello elemento = sessione.carrello.trovaElemento(prodotto);

        assertNotNull(elemento);
        assertEquals(2, elemento.getQuantita());
    }

    @Test
    void rimuoviProdottoDiminuisceLaQuantita() {

        Prodotto prodotto = new Prodotto(
                "123456",
                "Biscotti",
                2.50,
                "Biscotti al cioccolato",
                0.0,
                20,
                100,
                1
        );

        Cliente cliente = new Cliente(
                "Mario",
                "Rossi",
                "mario@email.it",
                "password",
                LocalDate.of(2000, 1, 1)
        );

        SessioneSpesa sessione = new SessioneSpesa(cliente, autenticazione);

        sessione.scansionaProdotto(prodotto);
        sessione.scansionaProdotto(prodotto);

        sessione.rimuoviProdotto(prodotto);

        assertEquals(2.50, sessione.carrello.calcolaTotale(), 0.001);
    }

    @Test
    void rimuoviProdottoMantieneLoStessoElementoConQuantitaRidotta() {

        Prodotto prodotto = new Prodotto(
                "123456",
                "Biscotti",
                2.50,
                "Biscotti al cioccolato",
                0.0,
                20,
                100,
                1
        );

        Cliente cliente = new Cliente(
                "Mario",
                "Rossi",
                "mario@email.it",
                "password",
                LocalDate.of(2000, 1, 1)
        );

        SessioneSpesa sessione = new SessioneSpesa(cliente, autenticazione);

        sessione.scansionaProdotto(prodotto);
        sessione.scansionaProdotto(prodotto);

        sessione.rimuoviProdotto(prodotto);

        assertEquals(1, sessione.carrello.numeroElementi());

        ElementoCarrello elemento = sessione.carrello.trovaElemento(prodotto);

        assertNotNull(elemento);
        assertEquals(1, elemento.getQuantita());
    }

    @Test
    void rimuoviUltimaUnitaEliminaElementoDalCarrello() {

        Prodotto prodotto = new Prodotto(
                "123456",
                "Biscotti",
                2.50,
                "Biscotti al cioccolato",
                0.0,
                20,
                100,
                1
        );

        Cliente cliente = new Cliente(
                "Mario",
                "Rossi",
                "mario@email.it",
                "password",
                LocalDate.of(2000, 1, 1)
        );

        SessioneSpesa sessione = new SessioneSpesa(cliente, autenticazione);

        sessione.scansionaProdotto(prodotto);
        sessione.rimuoviProdotto(prodotto);

        assertEquals(0.0, sessione.carrello.calcolaTotale(), 0.001);
        assertEquals(0, sessione.carrello.numeroElementi());
    }

    @Test
    void dopoTerminaSpesaNonPermetteNuoveScansioni() {

        Prodotto prodotto = new Prodotto(
                "123456",
                "Biscotti",
                2.50,
                "Biscotti al cioccolato",
                0.0,
                20,
                100,
                1
        );

        Cliente cliente = new Cliente(
                "Mario",
                "Rossi",
                "mario@email.it",
                "password",
                LocalDate.of(2000, 1, 1)
        );

        SessioneSpesa sessione = new SessioneSpesa(cliente, autenticazione);

        sessione.scansionaProdotto(prodotto);
        sessione.terminaSpesa();

        sessione.scansionaProdotto(prodotto);

        assertEquals(2.50, sessione.carrello.calcolaTotale(), 0.001);
    }

    @Test
    void scansionaProdottoApplicaLoSconto() {
        Prodotto prodotto = new Prodotto(
                "123",
                "Pasta",
                10.0,
                "Pasta di semola",
                20.0,
                10,
                20,
                1
        );

        Cliente cliente = new Cliente(
                "Mario",
                "Rossi",
                "mario@email.it",
                "password",
                LocalDate.of(2000, 1, 1)
        );

        SessioneSpesa sessione = new SessioneSpesa(cliente, autenticazione);

        sessione.scansionaProdotto(prodotto);

        assertEquals(8.0, sessione.carrello.calcolaTotale(), 0.001);
    }

    @Test
    void effettuaPagamentoValidoCompletaLaSessione() {

        Prodotto prodotto = new Prodotto(
                "123456",
                "Biscotti",
                2.50,
                "Biscotti al cioccolato",
                0.0,
                20,
                100,
                1
        );

        Cliente cliente = new Cliente(
                "Mario",
                "Rossi",
                "mario@email.it",
                "password",
                LocalDate.of(2000, 1, 1)
        );

        SessioneSpesa sessione = new SessioneSpesa(cliente, autenticazione) {
            @Override
            public boolean controlloCasuale() {
                return false;
            }
        };

        Totem totem = new Totem(1, new PaymentGateway());

        Cassiere cassiere = new Cassiere(
                "Luigi",
                "Bianchi",
                "luigi@email.it",
                "password",
                LocalDate.of(1990, 1, 1),
                null,
                new ControlloSpesa()
        );

        totem.setCassiere(cassiere);

        CarrelloFisico carrelloFisico = new CarrelloFisico();
        carrelloFisico.aggiungiElemento(
                new ElementoCarrello(prodotto, 1, 2.50)
        );

        totem.setCarrelloFisico(carrelloFisico);

        sessione.setTotem(totem);

        sessione.scansionaProdotto(prodotto);
        sessione.terminaSpesa();

        assertTrue(sessione.effettuaPagamento(EModalitaPagamento.CARTA_DI_CREDITO));
        assertTrue(sessione.getStato() == EStatoSessione.COMPLETATA || sessione.getStato() == EStatoSessione.CONTROLLO);
    }

    @Test
    void effettuaPagamentoPrimaDiTerminareLaSpesaFallisce() {

        Prodotto prodotto = new Prodotto(
                "123456",
                "Biscotti",
                2.50,
                "Biscotti al cioccolato",
                0.0,
                20,
                100,
                1
        );

        Cliente cliente = new Cliente(
                "Mario",
                "Rossi",
                "mario@email.it",
                "password",
                LocalDate.of(2000, 1, 1)
        );

        SessioneSpesa sessione = new SessioneSpesa(cliente, autenticazione);

        sessione.scansionaProdotto(prodotto);

        assertFalse(sessione.effettuaPagamento(EModalitaPagamento.CARTA_DI_CREDITO));
    }

    @Test
    void effettuaPagamentoConModalitaNullFallisce() {

        Prodotto prodotto = new Prodotto(
                "123456",
                "Biscotti",
                2.50,
                "Biscotti al cioccolato",
                0.0,
                20,
                100,
                1
        );

        Cliente cliente = new Cliente(
                "Mario",
                "Rossi",
                "mario@email.it",
                "password",
                LocalDate.of(2000, 1, 1)
        );

        SessioneSpesa sessione = new SessioneSpesa(cliente, autenticazione);

        sessione.scansionaProdotto(prodotto);
        sessione.terminaSpesa();

        assertFalse(sessione.effettuaPagamento(null));
    }

    @Test
    void generaRicevutaDopoPagamentoCompletato() {

        Prodotto prodotto = new Prodotto(
                "123456",
                "Biscotti",
                2.50,
                "Biscotti al cioccolato",
                0.0,
                20,
                100,
                1
        );

        Cliente cliente = new Cliente(
                "Mario",
                "Rossi",
                "mario@email.it",
                "password",
                LocalDate.of(2000, 1, 1)
        );

        SessioneSpesa sessione = new SessioneSpesa(cliente, autenticazione) {
            @Override
            public boolean controlloCasuale() {
                return false;
            }
        };

        Totem totem = new Totem(1, new PaymentGateway());

        sessione.setTotem(totem);

        sessione.scansionaProdotto(prodotto);
        sessione.terminaSpesa();

        assertTrue(sessione.effettuaPagamento(EModalitaPagamento.CARTA_DI_CREDITO));

        String ricevuta = sessione.generaRicevuta();

        assertNotNull(ricevuta);
        assertFalse(ricevuta.isEmpty());
        assertTrue(ricevuta.contains("RICEVUTA"));
        assertTrue(ricevuta.contains(String.format("%.2f", 2.50)));
        assertTrue(ricevuta.contains("CARTA_DI_CREDITO"));
    }

    @Test
    void generaRicevutaPrimaDelPagamentoRestituisceStringaVuota() {

        Cliente cliente = new Cliente(
                "Mario",
                "Rossi",
                "mario@email.it",
                "password",
                LocalDate.of(2000, 1, 1)
        );

        SessioneSpesa sessione = new SessioneSpesa(cliente, autenticazione);

        assertEquals("", sessione.generaRicevuta());
    }

    @Test
    void pagamentoRiuscitoAccumulaIPunti() throws Exception {

        Prodotto prodotto = new Prodotto(
                "123456",
                "Biscotti",
                10.0,
                "Biscotti al cioccolato",
                0.0,
                20,
                100,
                1
        );

        Cliente cliente = new Cliente(
                "Mario",
                "Rossi",
                "mario@email.it",
                "password",
                LocalDate.of(2000, 1, 1)
        );

        DBConnection.getInstance().resetTestDatabase("clienti");

        DBClienti dbClienti = new DBClienti(true);

        try (Connection connection = DBConnection.getInstance().getTestConnection("clienti");
             PreparedStatement statement = connection.prepareStatement(
                     "INSERT INTO clienti "
                             + "(id, nome, cognome, email, password, data_nascita, punti_fedelta, segnalato) "
                             + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)")) {

            statement.setInt(1, 1);
            statement.setString(2, "Mario");
            statement.setString(3, "Rossi");
            statement.setString(4, "mario@email.it");
            statement.setString(5, "password");
            statement.setDate(6, java.sql.Date.valueOf(LocalDate.of(2000, 1, 1)));
            statement.setInt(7, 0);
            statement.setBoolean(8, false);
            statement.executeUpdate();
        }

        IRepositoryClienti repository = dbClienti;
        cliente.setId(1);

        SessioneSpesa sessione = new SessioneSpesa(cliente, autenticazione) {
            @Override
            public boolean controlloCasuale() {
                return false;
            }
        };

        sessione.setRepositoryClienti(repository);

        Totem totem = new Totem(1, new PaymentGateway());

        Cassiere cassiere = new Cassiere(
                "Luigi",
                "Bianchi",
                "luigi@email.it",
                "password",
                LocalDate.of(1990, 1, 1),
                dbClienti,
                new ControlloSpesa()
        );

        totem.setCassiere(cassiere);

        CarrelloFisico carrelloFisico = new CarrelloFisico();
        carrelloFisico.aggiungiElemento(new ElementoCarrello(prodotto, 1, 10.0));

        totem.setCarrelloFisico(carrelloFisico);

        sessione.setTotem(totem);

        sessione.scansionaProdotto(prodotto);
        sessione.terminaSpesa();

        assertTrue(sessione.effettuaPagamento(EModalitaPagamento.CARTA_DI_CREDITO));

        if (sessione.isInControllo()) {
            sessione.completaDopoControllo();
        }

        assertEquals(5, repository.trovaPuntiByCliente(cliente.getId()));
    }

    @Test
    void usaPremioFedeltaApplicaLoScontoSenzaScalareSubitoIPunti() throws Exception {

        Prodotto prodotto = new Prodotto(
                "123456",
                "Biscotti",
                20.0,
                "Biscotti al cioccolato",
                0.0,
                20,
                100,
                1
        );

        Cliente cliente = new Cliente(
                "Mario",
                "Rossi",
                "mario@email.it",
                "password",
                LocalDate.of(2000, 1, 1)
        );

        cliente.setPuntiFedelta(200);

        DBConnection.getInstance().resetTestDatabase("clienti");

        DBClienti dbClienti = new DBClienti(true);

        try (Connection connection = DBConnection.getInstance().getTestConnection("clienti");
             PreparedStatement statement = connection.prepareStatement(
                     "INSERT INTO clienti "
                             + "(id, nome, cognome, email, password, data_nascita, punti_fedelta, segnalato) "
                             + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)")) {

            statement.setInt(1, 1);
            statement.setString(2, "Mario");
            statement.setString(3, "Rossi");
            statement.setString(4, "mario@email.it");
            statement.setString(5, "password");
            statement.setDate(6, java.sql.Date.valueOf(LocalDate.of(2000, 1, 1)));
            statement.setInt(7, 200);
            statement.setBoolean(8, false);
            statement.executeUpdate();
        }

        IRepositoryClienti repository = dbClienti;
        cliente.setId(1);

        SessioneSpesa sessione = new SessioneSpesa(cliente, autenticazione);
        sessione.setRepositoryClienti(repository);

        sessione.scansionaProdotto(prodotto);
        sessione.terminaSpesa();

        assertEquals(20.0, sessione.getTotaleDaPagare(), 0.001);

        assertTrue(sessione.usaPremioFedelta());

        assertEquals(18.0, sessione.getTotaleDaPagare(), 0.001);

        // I punti vengono scalati solo al completamento dell'acquisto
        assertEquals(200, repository.trovaPuntiByCliente(cliente.getId()));
    }

    @Test
    void usaPremioFedeltaConMenoDi100PuntiNonApplicaLoSconto() throws Exception {

        Prodotto prodotto = new Prodotto(
                "123456",
                "Biscotti",
                20.0,
                "Biscotti al cioccolato",
                0.0,
                20,
                100,
                1
        );

        Cliente cliente = new Cliente(
                "Mario",
                "Rossi",
                "mario@email.it",
                "password",
                LocalDate.of(2000, 1, 1)
        );

        cliente.setPuntiFedelta(99);

        DBConnection.getInstance().resetTestDatabase("clienti");

        DBClienti dbClienti = new DBClienti(true);

        try (Connection connection = DBConnection.getInstance().getTestConnection("clienti");
             PreparedStatement statement = connection.prepareStatement(
                     "INSERT INTO clienti "
                             + "(id, nome, cognome, email, password, data_nascita, punti_fedelta, segnalato) "
                             + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)")) {

            statement.setInt(1, 1);
            statement.setString(2, "Mario");
            statement.setString(3, "Rossi");
            statement.setString(4, "mario@email.it");
            statement.setString(5, "password");
            statement.setDate(6, java.sql.Date.valueOf(LocalDate.of(2000, 1, 1)));
            statement.setInt(7, 99);
            statement.setBoolean(8, false);
            statement.executeUpdate();
        }

        IRepositoryClienti repository = dbClienti;
        cliente.setId(1);

        SessioneSpesa sessione = new SessioneSpesa(cliente, autenticazione);
        sessione.setRepositoryClienti(repository);

        sessione.scansionaProdotto(prodotto);
        sessione.terminaSpesa();

        assertEquals(20.0, sessione.getTotaleDaPagare(), 0.001);

        assertFalse(sessione.usaPremioFedelta());

        assertEquals(20.0, sessione.getTotaleDaPagare(), 0.001);
        assertEquals(99, repository.trovaPuntiByCliente(cliente.getId()));
    }

    @Test
    void pagamentoConPremioFedeltaAccumulaIPuntiSulTotaleScontato() throws Exception {

        Prodotto prodotto = new Prodotto(
                "123456",
                "Biscotti",
                20.0,
                "Biscotti al cioccolato",
                0.0,
                20,
                100,
                1
        );

        Cliente cliente = new Cliente(
                "Mario",
                "Rossi",
                "mario@email.it",
                "password",
                LocalDate.of(2000, 1, 1)
        );

        cliente.setPuntiFedelta(200);

        DBConnection.getInstance().resetTestDatabase("clienti");

        DBClienti dbClienti = new DBClienti(true);

        try (Connection connection = DBConnection.getInstance().getTestConnection("clienti");
             PreparedStatement statement = connection.prepareStatement(
                     "INSERT INTO clienti "
                             + "(id, nome, cognome, email, password, data_nascita, punti_fedelta, segnalato) "
                             + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)")) {

            statement.setInt(1, 1);
            statement.setString(2, "Mario");
            statement.setString(3, "Rossi");
            statement.setString(4, "mario@email.it");
            statement.setString(5, "password");
            statement.setDate(6, java.sql.Date.valueOf(LocalDate.of(2000, 1, 1)));
            statement.setInt(7, 200);
            statement.setBoolean(8, false);
            statement.executeUpdate();
        }

        IRepositoryClienti repository = dbClienti;
        cliente.setId(1);

        SessioneSpesa sessione = new SessioneSpesa(cliente, autenticazione) {
            @Override
            public boolean controlloCasuale() {
                return false;
            }
        };

        sessione.setRepositoryClienti(repository);

        Totem totem = new Totem(1, new PaymentGateway());

        sessione.setTotem(totem);

        sessione.scansionaProdotto(prodotto);
        sessione.terminaSpesa();

        assertTrue(sessione.usaPremioFedelta());
        assertEquals(18.0, sessione.getTotaleDaPagare(), 0.001);

        assertTrue(sessione.effettuaPagamento(EModalitaPagamento.CARTA_DI_CREDITO));

        if (sessione.isInControllo()) {
            sessione.completaDopoControllo();
        }

        assertEquals(EStatoSessione.COMPLETATA, sessione.getStato());
        assertEquals(9, repository.trovaPuntiByCliente(cliente.getId()));
    }

    @Test
    void usaPremioFedeltaNonPuoEssereApplicatoDueVolte() throws Exception {

        Prodotto prodotto = new Prodotto(
                "123456",
                "Biscotti",
                20.0,
                "Biscotti al cioccolato",
                0.0,
                20,
                100,
                1
        );

        Cliente cliente = new Cliente(
                "Mario",
                "Rossi",
                "mario@email.it",
                "password",
                LocalDate.of(2000, 1, 1)
        );

        cliente.setPuntiFedelta(200);

        DBConnection.getInstance().resetTestDatabase("clienti");

        DBClienti dbClienti = new DBClienti(true);

        try (Connection connection = DBConnection.getInstance().getTestConnection("clienti");
             PreparedStatement statement = connection.prepareStatement(
                     "INSERT INTO clienti "
                             + "(id, nome, cognome, email, password, data_nascita, punti_fedelta, segnalato) "
                             + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)")) {

            statement.setInt(1, 1);
            statement.setString(2, "Mario");
            statement.setString(3, "Rossi");
            statement.setString(4, "mario@email.it");
            statement.setString(5, "password");
            statement.setDate(6, java.sql.Date.valueOf(LocalDate.of(2000, 1, 1)));
            statement.setInt(7, 200);
            statement.setBoolean(8, false);
            statement.executeUpdate();
        }

        IRepositoryClienti repository = dbClienti;
        cliente.setId(1);

        SessioneSpesa sessione = new SessioneSpesa(cliente, autenticazione);
        sessione.setRepositoryClienti(repository);

        sessione.scansionaProdotto(prodotto);
        sessione.terminaSpesa();

        assertTrue(sessione.usaPremioFedelta());

        assertEquals(18.0, sessione.getTotaleDaPagare(), 0.001);
        assertEquals(200, repository.trovaPuntiByCliente(cliente.getId()));

        assertFalse(sessione.usaPremioFedelta());

        assertEquals(18.0, sessione.getTotaleDaPagare(), 0.001);
        assertEquals(200, repository.trovaPuntiByCliente(cliente.getId()));
    }

    @Test
    void segnalaCambiaStato() {

        Cliente cliente = new Cliente(
                "Mario",
                "Rossi",
                "mario@email.it",
                "password",
                LocalDate.of(2000, 1, 1)
        );

        SessioneSpesa sessione = new SessioneSpesa(cliente, autenticazione);

        sessione.segnala();

        assertEquals(EStatoSessione.SEGNALATA, sessione.getStato());
    }
}
