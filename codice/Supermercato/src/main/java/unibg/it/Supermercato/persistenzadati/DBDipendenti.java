/**
 * @author Chiara Dentella
 */

package unibg.it.Supermercato.persistenzadati;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.ArrayList;
import java.sql.ResultSet;

import unibg.it.Supermercato.sistemaaccesso.Cassiere;
import unibg.it.Supermercato.sistemaaccesso.Dipendente;
import unibg.it.Supermercato.sistemaaccesso.IRepositoryClienti;
import unibg.it.Supermercato.sistemaaccesso.IRepositoryDipendenti;
import unibg.it.Supermercato.sistemaaccesso.Magazziniere;
import unibg.it.Supermercato.sistemaaccesso.Manager;
import unibg.it.Supermercato.sistemaaccesso.ResponsabileDiReparto;
import unibg.it.Supermercato.sistemaaccesso.Turno;

public class DBDipendenti implements IRepositoryDipendenti {

    private boolean usaDatabaseTest;
    private IRepositoryClienti repositoryClienti;

    // non usa il database di test
    public DBDipendenti() {
        this(false, null);
    }

    // usa il database di test
    public DBDipendenti(boolean usaDatabaseTest) {
        this(usaDatabaseTest, null);
    }

    public DBDipendenti(boolean usaDatabaseTest, IRepositoryClienti repositoryClienti) {
        this.usaDatabaseTest = usaDatabaseTest;
        this.repositoryClienti = repositoryClienti;
    }

    public void setRepositoryClienti(IRepositoryClienti repositoryClienti) {
        this.repositoryClienti = repositoryClienti;
    }

    /** questo metodo si connette a DBConnection
	 * @return l'istanza della connessione trovata
	 */
    private Connection getConnection() throws SQLException {
        if (usaDatabaseTest) {
            return DBConnection.getInstance().getTestConnection("dipendenti");
        }

        return DBConnection.getInstance().getConnection("dipendenti");
    }

    /** questo metodo salva un dipendente in persistenza
     * @param il dipendente da salvare
	 */
    @Override
    public void salvaDipendente(Dipendente dipendente) {

        if (dipendente == null) {
            return;
        }

        String sql = "INSERT INTO dipendenti (matricola, nome, cognome, email, password, data_nascita, ruolo) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, dipendente.getMatricola());
            statement.setString(2, dipendente.getNome());
            statement.setString(3, dipendente.getCognome());
            statement.setString(4, dipendente.getEmail());
            statement.setString(5, dipendente.getPassword());
            statement.setDate(6, java.sql.Date.valueOf(dipendente.getDataNascita()));
            statement.setString(7, dipendente.getRuolo());

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Errore nel salvataggio del dipendente.", e);
        }
    }

    /** questo metodo verifica le credenziali di un dipendente
     * @param l'email e la password del dipendente che si sta autenticando
     * @return il dipendente autenticato
	 */
    @Override
    public Dipendente verificaCredenziali(String email, String password) {

        if (email == null || password == null) {
            return null;
        }

        String sql = "SELECT matricola, nome, cognome, email, password, data_nascita, ruolo "
                   + "FROM dipendenti "
                   + "WHERE email = ? AND password = ?";

        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, email);
            statement.setString(2, password);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return creaDipendenteDaRisultato(resultSet);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Errore nella verifica delle credenziali del dipendente.", e);
        }

        return null;
    }

    /** questo metodo trova un dipendente tramite la sua matricola
     * @param la matricola del dipendente da trovare
     * @return il dipendente trovato
	 */
    @Override
    public Dipendente trovaDipendenteByMatricola(String matricola) {

        if (matricola == null) {
            return null;
        }

        String sql = "SELECT matricola, nome, cognome, email, password, data_nascita, ruolo "
                   + "FROM dipendenti "
                   + "WHERE matricola = ?";

        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, matricola);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return creaDipendenteDaRisultato(resultSet);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Errore nel recupero del dipendente.", e);
        }

        return null;
    }

    /** questo metodo permette di visualizzare tutti i dipendenti
     * @return la lista dei dipendenti 
	 */
    @Override
    public List<Dipendente> trovaTuttiDipendenti() {

        List<Dipendente> dipendenti = new ArrayList<>();

        String sql = "SELECT matricola, nome, cognome, email, password, data_nascita, ruolo "
                   + "FROM dipendenti";

        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql); ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                dipendenti.add(creaDipendenteDaRisultato(resultSet));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Errore nel recupero dei dipendenti.", e);
        }

        return dipendenti;
    }

    /** questo metodo salva un turno di un dipendente in persistenza
     * @param la matricola del dipendente e il turno a lui assegnato 
	 */
    @Override
    public void salvaTurno(String matricola, Turno turno) {

        if (matricola == null || turno == null) {
            return;
        }

        String sql = "INSERT INTO turni (matricola, data_turno, orario_inizio, orario_fine) "
                   + "VALUES (?, ?, ?, ?)";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, matricola);
            statement.setDate(2, java.sql.Date.valueOf(turno.getData()));
            statement.setTime(3, java.sql.Time.valueOf(turno.getOrarioInizio()));
            statement.setTime(4, java.sql.Time.valueOf(turno.getOrarioFine()));

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Errore nel salvataggio del turno.", e);
        }
    }

    /** questo metodo trova tutti i turni di un dipendente
     * @param la matricola del dipendente di cui trovare i turni
     * @return la lista dei turni
	 */
    @Override
    public List<Turno> trovaTurniByMatricola(String matricola) {

        List<Turno> turni = new ArrayList<>();

        if (matricola == null) {
            return turni;
        }

        String sql = "SELECT data_turno, orario_inizio, orario_fine, ora_ingresso, ora_uscita, presenza_registrata, completato "
                   + "FROM turni "
                   + "WHERE matricola = ? "
                   + "ORDER BY data_turno, orario_inizio";

        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, matricola);

            try (ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {

                    LocalDate data = resultSet.getDate("data_turno").toLocalDate();

                    LocalTime orarioInizio = resultSet.getTime("orario_inizio").toLocalTime();

                    LocalTime orarioFine = resultSet.getTime("orario_fine").toLocalTime();

                    Turno turno = new Turno(data, orarioInizio, orarioFine);

                    java.sql.Time oraIngresso = resultSet.getTime("ora_ingresso");

                    if (oraIngresso != null) {
                        turno.impostaOraIngresso(oraIngresso.toLocalTime());
                    }

                    java.sql.Time oraUscita = resultSet.getTime("ora_uscita");

                    if (oraUscita != null) {
                        turno.impostaOraUscita(oraUscita.toLocalTime());
                    }

                    turni.add(turno);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Errore nel recupero dei turni del dipendente.", e);
        }

        return turni;
    }

    /** questo metodo aggiorna la presenza di un dipendente in un turno
     * @param la matricola del dipendente, la data del turno, l'orario di inizio del turno, l'orario di ingresso del dipendente, 
     * l'orario di uscita del dipendente, true se la presenza è stata registrata (altrimenti false) e true se il turno è stato completato (altrimenti false)
	 */
    @Override
    public void aggiornaPresenza(String matricola, LocalDate dataTurno, LocalTime orarioInizio,
                                 LocalTime oraIngresso, LocalTime oraUscita, boolean presenzaRegistrata, boolean completato) {

        if (matricola == null || dataTurno == null || orarioInizio == null) {
            return;
        }

        String sql = "UPDATE turni "
                   + "SET ora_ingresso = ?, ora_uscita = ?, presenza_registrata = ?, completato = ? "
                   + "WHERE matricola = ? AND data_turno = ? AND orario_inizio = ?";

        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {

            if (oraIngresso != null) {
                statement.setTime(1, java.sql.Time.valueOf(oraIngresso));
            } else {
                statement.setNull(1, java.sql.Types.TIME);
            }

            if (oraUscita != null) {
                statement.setTime(2, java.sql.Time.valueOf(oraUscita));
            } else {
                statement.setNull(2, java.sql.Types.TIME);
            }

            statement.setBoolean(3, presenzaRegistrata);
            statement.setBoolean(4, completato);
            statement.setString(5, matricola);
            statement.setDate(6, java.sql.Date.valueOf(dataTurno));
            statement.setTime(7, java.sql.Time.valueOf(orarioInizio));

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Errore nell'aggiornamento della presenza del dipendente.", e);
        }
    }

    /** questo metodo crea un dipendente
     * @param i parametri del cliente da creare
     * @return il cliente creato
	 */
    private Dipendente creaDipendenteDaRisultato(ResultSet resultSet) throws SQLException {

        String matricola = resultSet.getString("matricola");
        String nome = resultSet.getString("nome");
        String cognome = resultSet.getString("cognome");
        String email = resultSet.getString("email");
        String password = resultSet.getString("password");
        LocalDate dataNascita = resultSet.getDate("data_nascita").toLocalDate();
        String ruolo = resultSet.getString("ruolo");

        Dipendente dipendente;

        switch (ruolo) {

            case "Manager":
                dipendente = new Manager(nome, cognome, email, password, dataNascita, this);
                break;

            case "Cassiere":
                dipendente = new Cassiere(nome, cognome, email, password, dataNascita, repositoryClienti, null);
                break;

            case "Magazziniere":
                dipendente = new Magazziniere(nome, cognome, email, password, dataNascita);
                break;

            case "ResponsabileDiReparto":
                dipendente = new ResponsabileDiReparto(nome, cognome, email, password, dataNascita);
                break;

            default:
                throw new IllegalArgumentException("Ruolo non riconosciuto: " + ruolo);
        }

        dipendente.setMatricola(matricola);
        dipendente.setRepositoryDipendenti(this);

        return dipendente;
    }
}