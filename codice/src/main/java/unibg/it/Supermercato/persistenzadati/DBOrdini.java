/**
 * @author Chiara Dentella
 */

package unibg.it.Supermercato.persistenzadati;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import unibg.it.Supermercato.gestionemagazzino.*;

public class DBOrdini implements IRepositoryOrdini {

    private boolean usaDatabaseTest;

    // non usa il database di test
    public DBOrdini() {
        this.usaDatabaseTest = false;
    }

    // usa il database di test
    public DBOrdini(boolean usaDatabaseTest) {
        this.usaDatabaseTest = usaDatabaseTest;
    }

    /** questo metodo si connette a DBConnection
	 * @return l'istanza della connessione trovata
	 */
    private Connection getConnection() throws SQLException {
        if (usaDatabaseTest) {
            return DBConnection.getInstance().getTestConnection("prodotti");
        }

        return DBConnection.getInstance().getConnection("prodotti");
    }

    /** questo metodo salva un ordine in persistenza
     * @param l'ordine da salvare
	 */
    @Override
    public void salvaOrdine(OrdineFornitore ordine) {

        if (ordine == null) {
            return;
        }

        String sqlOrdine = "INSERT INTO ordini_fornitore (fornitore_nome, data_ordine, stato, totale) "
                         + "VALUES (?, ?, ?, ?)";

        String sqlRiga = "INSERT INTO righe_ordine (ordine_id, codice_prodotto, quantita, prezzo) "
                       + "VALUES (?, ?, ?, ?)";

        try (Connection connection = getConnection(); PreparedStatement statementOrdine = connection.prepareStatement(sqlOrdine, java.sql.Statement.RETURN_GENERATED_KEYS);
             PreparedStatement statementRiga = connection.prepareStatement(sqlRiga)) {

            statementOrdine.setString(1, ordine.getFornitore().getNome());
            statementOrdine.setDate(2, java.sql.Date.valueOf(ordine.getDataOrdine()));
            statementOrdine.setString(3, ordine.getStato());
            statementOrdine.setDouble(4, ordine.getTotale());

            statementOrdine.executeUpdate();

            try (ResultSet generatedKeys = statementOrdine.getGeneratedKeys()) {

                if (!generatedKeys.next()) {
                    throw new SQLException("Impossibile recuperare l'id dell'ordine.");
                }

                int ordineId = generatedKeys.getInt(1);

                ordine.setId(ordineId);

                for (var riga : ordine.getRighe()) {

                    statementRiga.setInt(1, ordineId);
                    statementRiga.setString(2, riga.getProdotto().getCodiceABarre());
                    statementRiga.setInt(3, riga.getQuantita());
                    statementRiga.setDouble(4, riga.getProdotto().getPrezzo());

                    statementRiga.executeUpdate();
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Errore nel salvataggio dell'ordine.", e);
        }
    }

    /** questo metodo permette di visualizzare tutti gli ordini
     * @return la lista degli ordini 
	 */
    @Override
    public List<OrdineFornitore> trovaTuttiOrdini() {

        List<OrdineFornitore> ordini = new ArrayList<>();

        String sql = "SELECT id, fornitore_nome, data_ordine, stato, totale "
                   + "FROM ordini_fornitore";

        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql); java.sql.ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {

                int ordineId = resultSet.getInt("id");

                Fornitore fornitore = new Fornitore(resultSet.getString("fornitore_nome"));

                OrdineFornitore ordine = new OrdineFornitore(
                                fornitore,
                                resultSet.getDate("data_ordine").toLocalDate(),
                                resultSet.getString("stato"),
                                resultSet.getDouble("totale")
                        );

                ordine.setId(ordineId);

                caricaRigheOrdine(connection, ordine, ordineId);

                ordini.add(ordine);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Errore nel recupero degli ordini", e);
        }

        return ordini;
    }

    /** questo metodo aggiorna lo stato di un ordine (creato/ricevuto)
     * @param l'ordine di cui aggiornare lo stato
	 */
    @Override
    public void aggiornaStato(OrdineFornitore ordine) {

        if (ordine == null || ordine.getId() <= 0) {
            return;
        }

        String sql = "UPDATE ordini_fornitore "
                   + "SET stato = ? "
                   + "WHERE id = ?";

        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, ordine.getStato());
            statement.setInt(2, ordine.getId());

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Errore nell'aggiornamento dello stato dell'ordine.", e);
        }
    }

    /** questo metodo salva le righe di un ordine in persistenza
     * @param la connessione, l'ordine in cui sono contenute le righe, l'id dell'ordine
	 */
    private void caricaRigheOrdine(Connection connection, OrdineFornitore ordine, int ordineId) {

        String sql = "SELECT codice_prodotto, quantita, prezzo "
                   + "FROM righe_ordine "
                   + "WHERE ordine_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, ordineId);

            try (ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {

                    String codiceProdotto = resultSet.getString("codice_prodotto");

                    int quantita = resultSet.getInt("quantita");

                    DBProdotti dbProdotti = new DBProdotti(usaDatabaseTest);

                    Prodotto prodotto = dbProdotti.trovaProdottoByCodiceABarre(codiceProdotto);

                    if (prodotto != null) {
                        RigaOrdine riga = new RigaOrdine(prodotto, quantita);
                        ordine.aggiungiRiga(riga);
                    }
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Errore nel recupero delle righe dell'ordine.", e);
        }
    }
}

