package io.sphere.sdk.payments.commands;

import io.sphere.sdk.messages.queries.MessageQuery;
import io.sphere.sdk.models.LocalizedString;
import io.sphere.sdk.payments.*;
import io.sphere.sdk.payments.commands.updateactions.*;
import io.sphere.sdk.payments.messages.PaymentInteractionAddedMessage;
import io.sphere.sdk.payments.messages.PaymentStatusStateTransitionMessage;
import io.sphere.sdk.payments.messages.PaymentTransactionAddedMessage;
import io.sphere.sdk.queries.PagedQueryResult;
import io.sphere.sdk.test.IntegrationTest;
import org.junit.Test;

import javax.money.MonetaryAmount;
import java.time.ZonedDateTime;
import java.util.Collections;

import static io.sphere.sdk.carts.CartFixtures.withCustomerAndFilledCart;
import static io.sphere.sdk.customers.CustomerFixtures.withCustomer;
import static io.sphere.sdk.payments.PaymentFixtures.withPayment;
import static io.sphere.sdk.states.StateFixtures.withStateByBuilder;
import static io.sphere.sdk.states.StateType.PAYMENT_STATE;
import static io.sphere.sdk.test.SphereTestUtils.*;
import static io.sphere.sdk.types.TypeFixtures.STRING_FIELD_NAME;
import static io.sphere.sdk.types.TypeFixtures.withUpdateableType;
import static java.util.Collections.singletonMap;
import static org.assertj.core.api.Assertions.assertThat;

public class PaymentUpdateCommandTest extends IntegrationTest {
    @Test
    public void setAuthorization() {
        withPayment(client(), payment -> {
            //set authorization
            final MonetaryAmount totalAmount = EURO_30;
            final ZonedDateTime until = ZonedDateTime.now().plusDays(7);
            final Payment updatedPayment = execute(PaymentUpdateCommand.of(payment, SetAuthorization.of(totalAmount, until)));

            assertThat(updatedPayment.getAmountAuthorized()).isEqualTo(totalAmount);
            assertThat(updatedPayment.getAuthorizedUntil()).isEqualTo(until);
            assertThat(updatedPayment.getAmountPaid()).isNull();

            //remove authorization, set amount paid
            final Payment updatedPayment2 = execute(PaymentUpdateCommand.of(updatedPayment, asList(SetAuthorization.ofRemove(), SetAmountPaid.of(totalAmount))));

            assertThat(updatedPayment2.getAmountAuthorized()).isNull();
            assertThat(updatedPayment2.getAuthorizedUntil()).isNull();
            assertThat(updatedPayment2.getAmountPaid()).isEqualTo(totalAmount);

            return updatedPayment2;
        });

    }

    @Test
    public void setExternalId() {
            withPayment(client(), payment -> {
                final String externalId = randomKey();

                final Payment updatedPayment = execute(PaymentUpdateCommand.of(payment, asList(SetExternalId.of(externalId))));

                assertThat(updatedPayment.getExternalId()).isEqualTo(externalId);

                return updatedPayment;
            });
    }

    @Test
    public void setCustomer() {
        withCustomer(client(), customer -> {
            withPayment(client(), payment -> {
                assertThat(payment.getCustomer()).isNotEqualTo(customer.toReference());

                final Payment updatedPayment = execute(PaymentUpdateCommand.of(payment, SetCustomer.of(customer)));

                assertThat(updatedPayment.getCustomer()).isEqualTo(customer.toReference());

                return updatedPayment;
            });
        });
    }

    @Test
    public void refunded() {
        withPayment(client(), payment -> {

            final MonetaryAmount refundedAmount = payment.getAmountPlanned().divide(2);
            final Payment updatedPayment = execute(PaymentUpdateCommand.of(payment, SetAmountRefunded.of(refundedAmount)));

            assertThat(updatedPayment.getAmountRefunded()).isEqualTo(refundedAmount);

            return updatedPayment;
        });
    }

    @Test
    public void multiRefund() {
        withPayment(client(), payBuilder -> payBuilder.amountPaid(payBuilder.getAmountPlanned()), payment -> {
            final MonetaryAmount totalAmount = payment.getAmountPlanned();
            assertThat(payment.getAmountPaid()).as("amount paid").isEqualTo(totalAmount);

            final MonetaryAmount firstRefundedAmount = totalAmount.scaleByPowerOfTen(-1);
            final Payment firstRefundPayment = execute(PaymentUpdateCommand.of(payment, asList(SetAmountRefunded.of(firstRefundedAmount))));

            assertThat(firstRefundPayment.getAmountRefunded()).as("first refunded").isEqualTo(firstRefundedAmount);

            final MonetaryAmount secondRefundedAmount = firstRefundedAmount.multiply(2);
            //important, because SetAmountRefunded sets the total value
            final MonetaryAmount totalRefundedAmount = firstRefundPayment.getAmountRefunded().add(secondRefundedAmount);

            final Payment secondRefundPayment = execute(PaymentUpdateCommand.of(firstRefundPayment, asList(SetAmountRefunded.of(totalRefundedAmount))));

            assertThat(secondRefundPayment.getAmountRefunded()).as("total refunded").isEqualTo(totalRefundedAmount);

            return secondRefundPayment;
        });
    }

    @Test
    public void transitionState() {
        withStateByBuilder(client(), stateBuilder -> stateBuilder.initial(true).type(PAYMENT_STATE), validNextStateForPaymentStatus -> {
            withPayment(client(), payment -> {
                final Payment updatedPayment = execute(PaymentUpdateCommand.of(payment, TransitionState.of(validNextStateForPaymentStatus)));

                assertThat(updatedPayment.getPaymentStatus().getState()).isEqualTo(validNextStateForPaymentStatus.toReference());

                final PagedQueryResult<PaymentStatusStateTransitionMessage> messageQueryResult = execute(MessageQuery.of()
                        .withPredicates(m -> m.resource().is(payment))
                        .forMessageType(PaymentStatusStateTransitionMessage.MESSAGE_HINT));
                assertThat(messageQueryResult.head().get().getState()).isEqualTo(validNextStateForPaymentStatus.toReference());

                return updatedPayment;
            });
        });
    }


    @Test
    public void setStatusInterfaceText() {
        withPayment(client(), payment -> {
            final String interfaceText = "Operation successful";
            final String interfaceCode = "20000";
            final Payment updatedPayment = execute(PaymentUpdateCommand.of(payment,
                    asList(
                            SetStatusInterfaceText.of(interfaceText),
                            SetStatusInterfaceCode.of(interfaceCode)
                    )));

            assertThat(updatedPayment.getPaymentStatus().getInterfaceText()).isEqualTo(interfaceText);
            assertThat(updatedPayment.getPaymentStatus().getInterfaceCode()).isEqualTo(interfaceCode);

            return updatedPayment;
        });
    }

    @Test
    public void setInterfaceId() {
        withPayment(client(), payment -> {
            final String interfaceId =randomKey();
            final Payment updatedPayment = execute(PaymentUpdateCommand.of(payment, SetInterfaceId.of(interfaceId)));

            assertThat(updatedPayment.getInterfaceId()).isEqualTo(interfaceId);

            return updatedPayment;
        });
    }

    @Test
    public void setMethodInfoName() {
        withPayment(client(), payment -> {
            final LocalizedString name = randomSlug();
            final Payment updatedPayment = execute(PaymentUpdateCommand.of(payment, SetMethodInfoName.of(name)));

            assertThat(updatedPayment.getPaymentMethodInfo().getName()).isEqualTo(name);

            return updatedPayment;
        });
    }

    @Test
    public void setMethodInfoMethod() {
        withPayment(client(), payment -> {
            final String method = "method";
            final Payment updatedPayment = execute(PaymentUpdateCommand.of(payment, SetMethodInfoMethod.of(method)));

            assertThat(updatedPayment.getPaymentMethodInfo().getMethod()).isEqualTo(method);

            return updatedPayment;
        });
    }

    @Test
    public void setMethodInfoInterface() {
        withPayment(client(), payment -> {
            final String methodInfoInterface = randomKey();
            final Payment updatedPayment = execute(PaymentUpdateCommand.of(payment, SetMethodInfoInterface.of(methodInfoInterface)));

            assertThat(updatedPayment.getPaymentMethodInfo().getPaymentInterface()).isEqualTo(methodInfoInterface);

            return updatedPayment;
        });
    }

    @Test
    public void setCustomType() {
        withUpdateableType(client(), type -> {
            withPayment(client(), payment -> {
                final SetCustomType updateAction = SetCustomType
                        .ofTypeIdAndObjects(type.getId(), singletonMap(STRING_FIELD_NAME, "foo"));
                final Payment updatedPayment = execute(PaymentUpdateCommand.of(payment, updateAction));

                assertThat(updatedPayment.getCustom().getFieldAsString(STRING_FIELD_NAME)).isEqualTo("foo");

                final Payment updatedPayment2 = execute(PaymentUpdateCommand.of(updatedPayment,
                        SetCustomField.ofObject(STRING_FIELD_NAME, "bar")));

                assertThat(updatedPayment2.getCustom().getFieldAsString(STRING_FIELD_NAME)).isEqualTo("bar");

                return updatedPayment2;
            });
            return type;
        });
    }

    @Test
    public void transActions() {
        withCustomerAndFilledCart(client(), (customer, cart) -> {
            final MonetaryAmount totalAmount = cart.getTotalPrice();
            final PaymentMethodInfo paymentMethodInfo = PaymentMethodInfoBuilder.of()
                    .paymentInterface(randomKey())
                    .method("CREDIT_CARD")
                    .build();
            final TransactionDraft chargeTransaction = TransactionDraftBuilder
                    .of(TransactionType.CHARGE, totalAmount, ZonedDateTime.now())
                    .build();
            final PaymentDraftBuilder paymentDraftBuilder = PaymentDraftBuilder.of(totalAmount)
                    .customer(customer)
                    .paymentMethodInfo(paymentMethodInfo)
                    .amountPaid(totalAmount)
                    .transactions(Collections.singletonList(chargeTransaction));
            final Payment payment = execute(PaymentCreateCommand.of(paymentDraftBuilder.build()));

            assertThat(payment.getCustomer()).isEqualTo(payment.getCustomer());
            assertThat(payment.getPaymentMethodInfo()).isEqualTo(paymentMethodInfo);
            assertThat(payment.getAmountPlanned()).isEqualTo(totalAmount);

            final MonetaryAmount firstRefundAmount = EURO_10;
            final TransactionDraft firstRefundTransaction = TransactionDraftBuilder.of(TransactionType.REFUND, firstRefundAmount, ZonedDateTime.now()).build();
            final Payment paymentWithFirstRefund = execute(PaymentUpdateCommand.of(payment, asList(SetAmountRefunded.of(firstRefundAmount), AddTransaction.of(firstRefundTransaction))));

            assertThat(paymentWithFirstRefund.getTransactions()).hasSize(2);
            assertThat(paymentWithFirstRefund.getTransactions().get(0).getId()).isNotEmpty();

            final PagedQueryResult<PaymentTransactionAddedMessage> messageQueryResult = execute(MessageQuery.of().withPredicates(m -> m.resource().is(payment))
                    .forMessageType(PaymentTransactionAddedMessage.MESSAGE_HINT));
            assertThat(messageQueryResult.head().get().getTransaction().getTimestamp()).isEqualTo(firstRefundTransaction.getTimestamp());


            final MonetaryAmount secondRefundAmount = EURO_5;
            final TransactionDraft secondRefundTransaction = TransactionDraftBuilder.of(TransactionType.REFUND, secondRefundAmount, ZonedDateTime.now()).build();
            final MonetaryAmount totalRefundAmount = secondRefundAmount.add(paymentWithFirstRefund.getAmountRefunded());
            final Payment paymentWithSecondRefund = execute(PaymentUpdateCommand.of(paymentWithFirstRefund, asList(SetAmountRefunded.of(totalRefundAmount), AddTransaction.of(secondRefundTransaction))));

            assertThat(paymentWithSecondRefund.getTransactions()).hasSize(3);
            assertThat(paymentWithSecondRefund.getAmountRefunded()).isEqualTo(totalRefundAmount);
        });
    }

    @Test
    public void addInterfaceInteraction() {
        withUpdateableType(client(), type -> {
            withPayment(client(), payment -> {
                final AddInterfaceInteraction addInterfaceInteraction = AddInterfaceInteraction.ofTypeIdAndObjects(type.getId(), singletonMap(STRING_FIELD_NAME, "some id"));
                final Payment updatedPayment = execute(PaymentUpdateCommand.of(payment, addInterfaceInteraction));

                assertThat(updatedPayment.getInterfaceInteractions().get(0).getFieldAsString(STRING_FIELD_NAME)).isEqualTo("some id");

                final PagedQueryResult<PaymentInteractionAddedMessage> pagedQueryResult = execute(MessageQuery.of()
                        .withPredicates(m -> m.resource().is(payment))
                        .forMessageType(PaymentInteractionAddedMessage.MESSAGE_HINT));
                assertThat(pagedQueryResult.head().get().getInteraction().getFieldAsString(STRING_FIELD_NAME)).isEqualTo("some id");

                return updatedPayment;
            });
            return type;
        });
    }
}