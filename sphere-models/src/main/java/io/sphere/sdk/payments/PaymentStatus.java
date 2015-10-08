package io.sphere.sdk.payments;

import com.fasterxml.jackson.annotation.JsonCreator;
import io.sphere.sdk.models.Base;
import io.sphere.sdk.models.Reference;
import io.sphere.sdk.states.State;

import javax.annotation.Nullable;

/**
 @see PaymentStatusBuilder
 */
public final class PaymentStatus extends Base {
    @Nullable
    private final String interfaceCode;
    @Nullable
    private final String interfaceText;
    @Nullable
    private final Reference<State> state;

    @JsonCreator
    PaymentStatus(final String interfaceCode, final String interfaceText, final Reference<State> state) {
        this.interfaceCode = interfaceCode;
        this.interfaceText = interfaceText;
        this.state = state;
    }

    @Nullable
    public String getInterfaceCode() {
        return interfaceCode;
    }

    @Nullable
    public String getInterfaceText() {
        return interfaceText;
    }

    @Nullable
    public Reference<State> getState() {
        return state;
    }
}
