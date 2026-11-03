package unibg.it.Supermercato;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import unibg.it.Supermercato.sistemaaccesso.AuthService;
import unibg.it.Supermercato.sistemaaccesso.IRepositoryClienti;
import unibg.it.Supermercato.sistemaaccesso.IRepositoryDipendenti;
import unibg.it.Supermercato.sistemaaccesso.Magazziniere;

class RegistrazioneDipendenteTest {

    @Test
    void registraDipendenteConSuccesso() {

        IRepositoryClienti repositoryClienti = mock(IRepositoryClienti.class);

        IRepositoryDipendenti repositoryDipendenti = mock(IRepositoryDipendenti.class);

        AuthService authService = new AuthService(repositoryClienti, repositoryDipendenti);

        Magazziniere dipendente = new Magazziniere("Mario", "Rossi", "mario@email.it", "password123", LocalDate.of(1990, 1, 1));

        boolean risultato = authService.registraUtente(dipendente);

        assertTrue(risultato);

        verify(repositoryDipendenti).salvaDipendente(dipendente);
        verifyNoInteractions(repositoryClienti);
    }
}
