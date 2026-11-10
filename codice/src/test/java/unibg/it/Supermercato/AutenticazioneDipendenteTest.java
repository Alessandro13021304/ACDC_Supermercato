package unibg.it.Supermercato;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import unibg.it.Supermercato.sistemaaccesso.AuthService;
import unibg.it.Supermercato.sistemaaccesso.IRepositoryClienti;
import unibg.it.Supermercato.sistemaaccesso.IRepositoryDipendenti;
import unibg.it.Supermercato.sistemaaccesso.Magazziniere;
import unibg.it.Supermercato.sistemaaccesso.Persona;

class AutenticazioneDipendenteTest {

    @Test
    void autenticaDipendenteConCredenzialiCorrette() {

        IRepositoryClienti repositoryClienti = mock(IRepositoryClienti.class);

        IRepositoryDipendenti repositoryDipendenti = mock(IRepositoryDipendenti.class);

        AuthService authService = new AuthService(repositoryClienti, repositoryDipendenti);

        Magazziniere dipendente = new Magazziniere("Mario", "Rossi", "mario.rossi@email.it", "password123", LocalDate.of(1990, 1, 1));

        when(repositoryClienti.verificaCredenziali("mario.rossi@email.it", "password123")).thenReturn(null);

        when(repositoryDipendenti.verificaCredenziali("mario.rossi@email.it", "password123")).thenReturn(dipendente);

        Persona risultato = authService.autentica("mario.rossi@email.it", "password123");

        assertNotNull(risultato);
        assertTrue(risultato instanceof Magazziniere);
        assertEquals("Mario", risultato.getNome());
        assertEquals("Rossi", risultato.getCognome());

        verify(repositoryDipendenti).verificaCredenziali("mario.rossi@email.it", "password123");
    }

    @Test
    void rifiutaAutenticazioneConPasswordErrata() {

        IRepositoryClienti repositoryClienti = mock(IRepositoryClienti.class);

        IRepositoryDipendenti repositoryDipendenti = mock(IRepositoryDipendenti.class);

        AuthService authService = new AuthService(repositoryClienti, repositoryDipendenti);

        Magazziniere dipendente = new Magazziniere("Mario", "Rossi", "mario.rossi@email.it", "password123", LocalDate.of(1990, 1, 1));

        when(repositoryClienti.verificaCredenziali("mario.rossi@email.it", "passwordErrata")).thenReturn(null);

        when(repositoryDipendenti.verificaCredenziali("mario.rossi@email.it", "passwordErrata")).thenReturn(null);

        Persona risultato = authService.autentica("mario.rossi@email.it", "passwordErrata");

        assertNull(risultato);

        verify(repositoryDipendenti).verificaCredenziali("mario.rossi@email.it", "passwordErrata");
    }
}

