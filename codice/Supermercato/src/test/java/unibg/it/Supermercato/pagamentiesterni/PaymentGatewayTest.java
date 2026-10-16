package unibg.it.Supermercato.pagamentiesterni;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class PaymentGatewayTest {

    @Test
    void pagamentoConImportoPositivo() {
        PaymentGateway gateway = new PaymentGateway();

        assertTrue(gateway.paga(50.0));
    }

    @Test
    void pagamentoConImportoZero() {
        PaymentGateway gateway = new PaymentGateway();

        assertFalse(gateway.paga(0.0));
    }

    @Test
    void pagamentoConImportoNegativo() {
        PaymentGateway gateway = new PaymentGateway();

        assertFalse(gateway.paga(-10.0));
    }
}
