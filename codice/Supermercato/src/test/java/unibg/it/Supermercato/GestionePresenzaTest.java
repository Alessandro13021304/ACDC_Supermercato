package unibg.it.Supermercato;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import unibg.it.Supermercato.gestionedipendenti.GestorePresenze;
import unibg.it.Supermercato.gestionedipendenti.Turno;
import unibg.it.Supermercato.persistenzadati.IRepositoryDipendenti;
import unibg.it.Supermercato.sistemaaccesso.IAutenticazione;
import unibg.it.Supermercato.sistemaaccesso.Magazziniere;

class GestionePresenzaTest {

	@Test
	void dipendenteRegistraIngressoEUscita() {

	    IRepositoryDipendenti repositoryDipendenti =
	            mock(IRepositoryDipendenti.class);

	    IAutenticazione autenticazione =
	            mock(IAutenticazione.class);

	    GestorePresenze gestorePresenze =
	            new GestorePresenze(
	                    repositoryDipendenti,
	                    autenticazione
	            );

	    Magazziniere dipendente = new Magazziniere(
	            "Mario",
	            "Rossi",
	            "mario@email.it",
	            "password",
	            LocalDate.of(1990, 1, 1)
	    );

	    dipendente.setMatricola("M001");

	    Turno turno = new Turno(
	            LocalDate.now(),
	            LocalTime.of(8, 0),
	            LocalTime.of(14, 0)
	    );

	    when(autenticazione.autentica(
	            "mario@email.it",
	            "password"
	    )).thenReturn(dipendente);

	    when(repositoryDipendenti.trovaTurniByMatricola("M001"))
	            .thenReturn(List.of(turno));

	    gestorePresenze.registraIngresso(dipendente);

	    assertTrue(turno.presenzaIniziata());
	    assertFalse(turno.isCompletato());
	    assertNotNull(turno.getOraIngresso());

	    verify(repositoryDipendenti).aggiornaPresenza(
	            eq("M001"),
	            eq(turno.getData()),
	            eq(turno.getOrarioInizio()),
	            eq(turno.getOraIngresso()),
	            eq(turno.getOraUscita()),
	            eq(true),
	            eq(false)
	    );

	    gestorePresenze.registraUscita(dipendente);

	    assertTrue(turno.presenzaIniziata());
	    assertTrue(turno.isCompletato());
	    assertNotNull(turno.getOraUscita());

	    verify(repositoryDipendenti).aggiornaPresenza(
	            eq("M001"),
	            eq(turno.getData()),
	            eq(turno.getOrarioInizio()),
	            eq(turno.getOraIngresso()),
	            eq(turno.getOraUscita()),
	            eq(true),
	            eq(true)
	    );
	}
	
	@Test
	void calcolaCorrettamenteLeOreLavorate() {

	    IRepositoryDipendenti repositoryDipendenti =
	            mock(IRepositoryDipendenti.class);

	    IAutenticazione autenticazione =
	            mock(IAutenticazione.class);

	    GestorePresenze gestorePresenze =
	            new GestorePresenze(
	                    repositoryDipendenti,
	                    autenticazione
	            );

	    Magazziniere dipendente = new Magazziniere(
	            "Mario",
	            "Rossi",
	            "mario@email.it",
	            "password",
	            LocalDate.of(1990, 1, 1)
	    );

	    dipendente.setMatricola("M001");

	    Turno turno = new Turno(
	            LocalDate.of(2026, 10, 10),
	            LocalTime.of(8, 0),
	            LocalTime.of(14, 0)
	    );

	    turno.impostaOraIngresso(LocalTime.of(8, 0));
	    turno.impostaOraUscita(LocalTime.of(14, 0));

	    when(repositoryDipendenti.trovaTurniByMatricola("M001"))
	            .thenReturn(List.of(turno));

	    dipendente.setRepositoryDipendenti(repositoryDipendenti);

	    double oreLavorate =
	            gestorePresenze.calcolaOreLavorate(dipendente);

	    assertEquals(6.0, oreLavorate);
	}
	
	@Test
	void autenticazioneFallitaNonRegistraLaPresenza() {

	    IRepositoryDipendenti repositoryDipendenti =
	            mock(IRepositoryDipendenti.class);

	    IAutenticazione autenticazione =
	            mock(IAutenticazione.class);

	    GestorePresenze gestorePresenze =
	            new GestorePresenze(
	                    repositoryDipendenti,
	                    autenticazione
	            );

	    Magazziniere dipendente = new Magazziniere(
	            "Mario",
	            "Rossi",
	            "mario@email.it",
	            "password",
	            LocalDate.of(1990, 1, 1)
	    );

	    dipendente.setMatricola("M001");

	    Turno turno = new Turno(
	            LocalDate.now(),
	            LocalTime.of(8, 0),
	            LocalTime.of(14, 0)
	    );

	    when(autenticazione.autentica(
	            "mario@email.it",
	            "password"
	    )).thenReturn(null);

	    when(repositoryDipendenti.trovaTurniByMatricola("M001"))
	            .thenReturn(List.of(turno));

	    gestorePresenze.registraIngresso(dipendente);

	    assertFalse(turno.presenzaIniziata());
	    assertNull(turno.getOraIngresso());

	    verify(repositoryDipendenti, never()).aggiornaPresenza(
	            anyString(),
	            any(LocalDate.class),
	            any(LocalTime.class),
	            any(LocalTime.class),
	            any(),
	            anyBoolean(),
	            anyBoolean()
	    );
	}
}

