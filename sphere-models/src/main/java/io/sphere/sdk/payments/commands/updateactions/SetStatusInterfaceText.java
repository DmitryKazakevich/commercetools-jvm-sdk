package io.sphere.sdk.payments.commands.updateactions;

import io.sphere.sdk.commands.UpdateActionImpl;
import io.sphere.sdk.payments.Payment;

import javax.annotation.Nullable;

/**
 *
 * {@include.example io.sphere.sdk.payments.commands.PaymentUpdateCommandTest#setStatusInterfaceText()}
 */
public class SetStatusInterfaceText extends UpdateActionImpl<Payment> {

    @Nullable
    private final String interfaceText;

    private SetStatusInterfaceText(@Nullable final String interfaceText) {
        super("setStatusInterfaceText");
        this.interfaceText = interfaceText;
    }

    public static SetStatusInterfaceText of(@Nullable final String interfaceText) {
        return new SetStatusInterfaceText(interfaceText);
    }

    @Nullable
    public String getinterfaceText() {
        return interfaceText;
    }
}
