package io.sphere.sdk.payments.commands;

import io.sphere.sdk.payments.Payment;
import io.sphere.sdk.payments.PaymentDraft;
import io.sphere.sdk.payments.PaymentDraftBuilder;
import io.sphere.sdk.payments.queries.PaymentByIdGet;
import io.sphere.sdk.test.IntegrationTest;
import org.junit.Test;

import static io.sphere.sdk.test.SphereTestUtils.EURO_20;
import static org.assertj.core.api.Assertions.assertThat;

public class PaymentDeleteCommandTest extends IntegrationTest {
    @Test
    public void execution() {
        final PaymentDraft paymentDraft = PaymentDraftBuilder.of(EURO_20).build();
        final Payment payment = execute(PaymentCreateCommand.of(paymentDraft));
        execute(PaymentDeleteCommand.of(payment));

        final Payment loadedPayment = execute(PaymentByIdGet.of(payment));

        assertThat(loadedPayment).isNull();
    }
}