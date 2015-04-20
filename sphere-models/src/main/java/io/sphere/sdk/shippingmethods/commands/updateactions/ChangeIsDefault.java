package io.sphere.sdk.shippingmethods.commands.updateactions;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.sphere.sdk.commands.UpdateAction;
import io.sphere.sdk.shippingmethods.ShippingMethod;

/**
 *
 * {@include.example io.sphere.sdk.shippingmethods.commands.ShippingMethodUpdateCommandTest#changeIsDefault()}
 */
public class ChangeIsDefault extends UpdateAction<ShippingMethod> {
    private final boolean isDefault;

    private ChangeIsDefault(final boolean isDefault) {
        super("changeIsDefault");
        this.isDefault = isDefault;
    }

    @JsonProperty("isDefault")
    public boolean isDefault() {
        return isDefault;
    }

    public static ChangeIsDefault of(final boolean isDefault) {
        return new ChangeIsDefault(isDefault);
    }

    public static ChangeIsDefault toTrue() {
        return of(true);
    }

    public static ChangeIsDefault toFalse() {
        return of(false);
    }
}
