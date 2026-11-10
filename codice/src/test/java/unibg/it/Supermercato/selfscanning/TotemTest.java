package unibg.it.Supermercato.selfscanning;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

import unibg.it.Supermercato.pagamentiesterni.PaymentGateway;
import unibg.it.Supermercato.sistemaaccesso.Cliente;
import unibg.it.Supermercato.sistemaaccesso.Cassiere;

public class TotemTest {

    @Test
    void effettuaPagamentoConImportoValido() {
        Totem totem = new Totem(1, new PaymentGateway());

        assertTrue(totem.effettuaPagamento(50.0));
    }

    @Test
    void rifiutaPagamentoConImportoNonValido() {
        Totem totem = new Totem(1, new PaymentGateway());

        assertFalse(totem.effettuaPagamento(0.0));
    }
    
    @Test
    void effettuaControlloSpesa() {
        Totem totem = new Totem(1, new PaymentGateway());

        Cliente cliente = new Cliente("Mario", "Rossi", "mario@email.it", "password", java.time.LocalDate.of(2000, 1, 1));

        SessioneSpesa sessione = new SessioneSpesa(cliente, null);

        CarrelloFisico carrelloFisico = new CarrelloFisico();
        totem.setCarrelloFisico(carrelloFisico);
        
        totem.setCarrelloFisico(carrelloFisico);

        Cassiere cassiere = new Cassiere("Luigi", "Bianchi", "luigi@email.it", "password", java.time.LocalDate.of(1990, 1, 1), null, new ControlloSpesa());

        totem.setCassiere(cassiere);

        sessione.terminaSpesa();
        sessione.avviaControllo();

        assertTrue(totem.effettuaControlloSpesa(sessione));
    }
}
