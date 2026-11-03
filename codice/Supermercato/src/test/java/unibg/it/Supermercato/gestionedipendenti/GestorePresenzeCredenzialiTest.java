package unibg.it.Supermercato.gestionedipendenti;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import unibg.it.Supermercato.sistemaaccesso.IAutenticazione;
import unibg.it.Supermercato.sistemaaccesso.IRepositoryDipendenti;
import unibg.it.Supermercato.sistemaaccesso.Magazziniere;
import unibg.it.Supermercato.sistemaaccesso.Presenza;
import unibg.it.Supermercato.sistemaaccesso.Turno;

class GestorePresenzeCredenzialiTest {

    private static final LocalDate OGGI = LocalDate.of(2026, 11, 2);

    private IRepositoryDipendenti repository;
    private IAutenticazione autenticazione;
    private Magazziniere dipendente;
    private Magazziniere altroDipendente;
    private Turno turno;

    private Clock orologio(int ora, int minuti) {
        ZoneId zona = ZoneId.systemDefault();

        return Clock.fixed(LocalDateTime.of(OGGI, LocalTime.of(ora, minuti)).atZone(zona).toInstant(), zona);
    }

    private void verificaNessunaPresenzaSalvata() {
        verify(repository, never()).aggiornaPresenza(any(), any(), any(), any(), any(), anyBoolean(), anyBoolean());
    }

    @BeforeEach
    void preparaDati() {
        repository = mock(IRepositoryDipendenti.class);
        autenticazione = mock(IAutenticazione.class);

        dipendente = new Magazziniere("Mario", "Rossi", "mario@email.it", "password", LocalDate.of(1990, 1, 1));
        dipendente.setMatricola("M001");

        altroDipendente = new Magazziniere("Anna", "Verdi", "anna@email.it", "password2", LocalDate.of(1992, 2, 2));
        altroDipendente.setMatricola("M002");

        turno = new Turno(OGGI, LocalTime.of(8, 0), LocalTime.of(16, 0));

        when(autenticazione.autentica("mario@email.it", "password")).thenReturn(dipendente);
        when(autenticazione.autentica("anna@email.it", "password2")).thenReturn(altroDipendente);
        when(repository.trovaTurniByMatricola("M001")).thenReturn(List.of(turno));
    }

    @Test
    void conCredenzialiCorretteRegistraIngressoEUscitaConLOrologioIniettato() {
        GestorePresenze mattina = new GestorePresenze(repository, autenticazione, orologio(9, 30));
        mattina.registraIngresso(dipendente, "mario@email.it", "password");

        assertTrue(turno.presenzaIniziata());
        assertEquals(LocalTime.of(9, 30), turno.getOraIngresso());

        GestorePresenze pomeriggio = new GestorePresenze(repository, autenticazione, orologio(15, 0));
        pomeriggio.registraUscita(dipendente, "mario@email.it", "password");

        assertTrue(turno.isCompletato());
        assertEquals(LocalTime.of(15, 0), turno.getOraUscita());
        assertEquals(5.5, new Presenza(turno).calcolaOreLavorate(), 0.001);
    }

    @Test
    void conPasswordErrataLaPresenzaNonVieneRegistrata() {
        GestorePresenze gestore = new GestorePresenze(repository, autenticazione, orologio(9, 30));

        gestore.registraIngresso(dipendente, "mario@email.it", "sbagliata");

        assertFalse(turno.presenzaIniziata());
        assertNull(turno.getOraIngresso());
        verificaNessunaPresenzaSalvata();
    }

    @Test
    void leCredenzialiDiUnAltroDipendenteNonRegistranoLaPresenza() {
        GestorePresenze gestore = new GestorePresenze(repository, autenticazione, orologio(9, 30));

        gestore.registraIngresso(dipendente, "anna@email.it", "password2");

        assertFalse(turno.presenzaIniziata());
        verificaNessunaPresenzaSalvata();
    }

    @Test
    void senzaUnTurnoDiOggiNonRegistraLIngresso() {
        Turno turnoDiDomani = new Turno(OGGI.plusDays(1), LocalTime.of(8, 0), LocalTime.of(16, 0));
        when(repository.trovaTurniByMatricola("M001")).thenReturn(List.of(turnoDiDomani));

        GestorePresenze gestore = new GestorePresenze(repository, autenticazione, orologio(9, 30));

        gestore.registraIngresso(dipendente, "mario@email.it", "password");

        assertFalse(turnoDiDomani.presenzaIniziata());
        verificaNessunaPresenzaSalvata();
    }

    @Test
    void credenzialiNulleNonRegistranoLaPresenzaENonInterrogaranoLAutenticazione() {
        GestorePresenze gestore = new GestorePresenze(repository, autenticazione, orologio(9, 30));

        gestore.registraIngresso(dipendente, null, null);

        assertFalse(turno.presenzaIniziata());
        verify(autenticazione, never()).autentica(any(), any());
        verificaNessunaPresenzaSalvata();
    }
}
