package unibg.it.Supermercato.sistemaaccesso;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

class CassiereControlloSpesaTest {

    private Cassiere creaCassiere(IRepositoryClienti repositoryClienti, IControlloSpesa controlloSpesa) {
        return new Cassiere("Luigi", "Verdi", "luigi@email.it", "password", LocalDate.of(1990, 1, 1), repositoryClienti, controlloSpesa);
    }

    @Test
    void senzaControlloSpesaIlControlloNonParteENonSegnalaIlCliente() {
        IRepositoryClienti repositoryClienti = mock(IRepositoryClienti.class);
        Cassiere cassiere = creaCassiere(repositoryClienti, null);

        ISessioneControllabile sessione = mock(ISessioneControllabile.class);
        when(sessione.isInControllo()).thenReturn(true);

        assertFalse(cassiere.effettuaControllo(sessione, mock(ICarrelloFisico.class)));

        verify(sessione, never()).segnala();
        verify(sessione, never()).completaDopoControllo();
        verify(repositoryClienti, never()).registraSegnalazione(anyInt(), any(LocalDateTime.class));
    }

    @Test
    void conControlloSpesaImpostatoDopoLaCreazioneIlControlloFunziona() {
        Cassiere cassiere = creaCassiere(mock(IRepositoryClienti.class), null);

        IControlloSpesa controlloSpesa = mock(IControlloSpesa.class);
        cassiere.setControlloSpesa(controlloSpesa);

        ISessioneControllabile sessione = mock(ISessioneControllabile.class);
        ICarrelloFisico carrelloFisico = mock(ICarrelloFisico.class);

        when(sessione.isInControllo()).thenReturn(true);
        when(controlloSpesa.verificaCorrispondenza(sessione, carrelloFisico)).thenReturn(true);

        assertTrue(cassiere.effettuaControllo(sessione, carrelloFisico));
        verify(sessione).completaDopoControllo();
    }
}

