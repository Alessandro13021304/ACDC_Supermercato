package unibg.it.Supermercato.persistenzadati;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import unibg.it.Supermercato.sistemaaccesso.Manager;
import unibg.it.Supermercato.sistemaaccesso.Cassiere;
import unibg.it.Supermercato.sistemaaccesso.Magazziniere;
import unibg.it.Supermercato.sistemaaccesso.ResponsabileDiReparto;
import unibg.it.Supermercato.gestionedipendenti.Turno;

import unibg.it.Supermercato.sistemaaccesso.Dipendente;

public class DBDipendenti implements IRepositoryDipendenti {
	
	private boolean usaDatabaseTest;

	public DBDipendenti() {
	    this.usaDatabaseTest = false;
	}

	public DBDipendenti(boolean usaDatabaseTest) {
	    this.usaDatabaseTest = usaDatabaseTest;
	}

	private Connection getConnection() throws SQLException {
	    if (usaDatabaseTest) {
	        return DBConnection.getInstance().getTestConnection("dipendenti");
	    }

	    return DBConnection.getInstance().getConnection("dipendenti");
	}

    @Override
    public void salvaDipendente(Dipendente dipendente) {

        if (dipendente == null) {
            return;
        }

        String sql = "INSERT INTO dipendenti (matricola, nome, cognome, email, password, data_nascita, ruolo)"
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

    @Override
    public Dipendente verificaCredenziali(String email, String password) {

        if (email == null || password == null) {
            return null;
        }

        String sql = "SELECT matricola, nome, cognome, email, password, data_nascita, ruolo"
                   + "FROM dipendenti"
                   + "WHERE email = ? AND password = ?";

        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, email);
            statement.setString(2, password);

            try (java.sql.ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return creaDipendenteDaRisultato(resultSet);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Errore nella verifica delle credenziali del dipendente.", e);
        }

        return null;
    }
    
    @Override
    public Dipendente trovaDipendenteByMatricola(String matricola) {

        if (matricola == null) {
            return null;
        }

        String sql = "SELECT matricola, nome, cognome, email, password, data_nascita, ruolo"
                   + "FROM dipendenti"
                   + "WHERE matricola = ?";

        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, matricola);

            try (java.sql.ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return creaDipendenteDaRisultato(resultSet);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Errore nel recupero del dipendente.", e);
        }

        return null;
    }
    
    @Override
    public java.util.List<Dipendente> trovaTuttiDipendenti() {

        java.util.List<Dipendente> dipendenti = new java.util.ArrayList<>();

        String sql = "SELECT matricola, nome, cognome, email, password, data_nascita, ruolo"
                   + "FROM dipendenti";

        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql); java.sql.ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                dipendenti.add(creaDipendenteDaRisultato(resultSet));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Errore nel recupero dei dipendenti.", e);
        }

        return dipendenti;
    }
    
    @Override
    public void salvaTurno(String matricola, Turno turno) {

        if (matricola == null || turno == null) {
            return;
        }

        String sql = "INSERT INTO turni (matricola, data_turno, orario_inizio, orario_fine)"
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
    
    @Override
    public List<Turno> trovaTurniByMatricola(String matricola) {

        List<Turno> turni = new java.util.ArrayList<>();

        if (matricola == null) {
            return turni;
        }

        String sql = "SELECT data_turno, orario_inizio, orario_fine, ora_ingresso, ora_uscita, presenza_registrata, completato"
                   + "FROM turni"
                   + "WHERE matricola = ?"
                   + "ORDER BY data_turno, orario_inizio";

        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, matricola);

            try (java.sql.ResultSet resultSet = statement.executeQuery()) {

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
    
    @Override
    public void aggiornaPresenza(String matricola, java.time.LocalDate dataTurno, java.time.LocalTime orarioInizio,
                                 java.time.LocalTime oraIngresso, java.time.LocalTime oraUscita, boolean presenzaRegistrata, boolean completato) {

        if (matricola == null || dataTurno == null || orarioInizio == null) {
            return;
        }

        String sql = "UPDATE turni"
                   + "SET ora_ingresso = ?, ora_uscita = ?, presenza_registrata = ?, completato = ?"
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
    
    private Dipendente creaDipendenteDaRisultato(java.sql.ResultSet resultSet) throws SQLException {

        String matricola = resultSet.getString("matricola");
        String nome = resultSet.getString("nome");
        String cognome = resultSet.getString("cognome");
        String email = resultSet.getString("email");
        String password = resultSet.getString("password");
        java.time.LocalDate dataNascita = resultSet.getDate("data_nascita").toLocalDate();
        String ruolo = resultSet.getString("ruolo");

        Dipendente dipendente;

        switch (ruolo) {

            case "Manager":
                dipendente = new Manager(nome, cognome, email, password, dataNascita, this);
                break;

            case "Cassiere":
                dipendente = new Cassiere(nome, cognome, email, password, dataNascita, new DBClienti(), null);
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

        return dipendente;
    }
}