package unibg.it.Supermercato.persistenzadati;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import unibg.it.Supermercato.gestionemagazzino.Prodotto;
import unibg.it.Supermercato.sistemaaccesso.Dipendente;

public class DBProdotti implements IRepositoryProdotti {

    private boolean usaDatabaseTest;

    public DBProdotti() {
        this.usaDatabaseTest = false;
    }

    public DBProdotti(boolean usaDatabaseTest) {
        this.usaDatabaseTest = usaDatabaseTest;
    }

    private Connection getConnection() throws SQLException {
        if (usaDatabaseTest) {
            return DBConnection.getInstance().getTestConnection("prodotti");
        }

        return DBConnection.getInstance().getConnection("prodotti");
    }

    @Override
    public void salvaProdotto(Prodotto prodotto) {

        if (prodotto == null) {
            return;
        }

        String sql = "INSERT INTO prodotti (codice_a_barre, nome, descrizione, prezzo, sconto, quantita_scaffale, quantita_magazzino, numero_reparto)"
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, prodotto.getCodiceABarre());
            statement.setString(2, prodotto.getNome());
            statement.setString(3, prodotto.getDescrizione());
            statement.setDouble(4, prodotto.getPrezzo());
            statement.setDouble(5, prodotto.getSconto());
            statement.setInt(6, prodotto.getQuantitaScaffale());
            statement.setInt(7, prodotto.getQuantitaMagazzino());
            statement.setInt(8, prodotto.getNumeroReparto());

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Errore nel salvataggio del prodotto.", e);
        }
    }
    
    @Override
    public void rimuoviProdotto(String codiceABarre) {

        if (codiceABarre == null) {
            return;
        }

        String sql = "DELETE FROM prodotti"
                   + "WHERE codice_a_barre = ?";

        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, codiceABarre);
            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Errore nella rimozione del prodotto.", e);
        }
    }

    @Override
    public void modificaProdotto(Prodotto prodotto) {

        if (prodotto == null) {
            return;
        }

        String sql = "UPDATE prodotti"
                   + "SET nome = ?, descrizione = ?, prezzo = ?, sconto = ?, quantita_scaffale = ?, quantita_magazzino = ?, numero_reparto = ?"
                   + "WHERE codice_a_barre = ?";

        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, prodotto.getNome());
            statement.setString(2, prodotto.getDescrizione());
            statement.setDouble(3, prodotto.getPrezzo());
            statement.setDouble(4, prodotto.getSconto());
            statement.setInt(5, prodotto.getQuantitaScaffale());
            statement.setInt(6, prodotto.getQuantitaMagazzino());
            statement.setInt(7, prodotto.getNumeroReparto());
            statement.setString(8, prodotto.getCodiceABarre());

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Errore nella modifica del prodotto.", e);
        }
    }

    @Override
    public Prodotto trovaProdottoByCodiceABarre(String codiceABarre) {

        if (codiceABarre == null) {
            return null;
        }

        String sql = "SELECT codice_a_barre, nome, descrizione, prezzo, sconto, quantita_scaffale, quantita_magazzino, numero_reparto"
                   + "FROM prodotti"
                   + "WHERE codice_a_barre = ?";

        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, codiceABarre);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                	return creaProdottoDaRisultato(resultSet);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Errore nel recupero del prodotto.", e);
        }

        return null;
    }

    @Override
    public List<Prodotto> trovaTuttiProdotti() {

    	java.util.List<Prodotto> prodotti = new java.util.ArrayList<>();

        String sql = "SELECT codice_a_barre, nome, descrizione, prezzo, sconto, quantita_scaffale, quantita_magazzino, numero_reparto"
                   + "FROM prodotti";

        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql); java.sql.ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {           	
            	prodotti.add(creaProdottoDaRisultato(resultSet));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Errore nel recupero dei prodotti.", e);
        }

        return prodotti;
    }
    
    private Prodotto creaProdottoDaRisultato(ResultSet resultSet) throws SQLException {

        return new Prodotto(
                resultSet.getString("codice_a_barre"),
                resultSet.getString("nome"),
                resultSet.getDouble("prezzo"),
                resultSet.getString("descrizione"),
                resultSet.getDouble("sconto"),
                resultSet.getInt("quantita_scaffale"),
                resultSet.getInt("quantita_magazzino"),
                resultSet.getInt("numero_reparto")
        );
    }
}

