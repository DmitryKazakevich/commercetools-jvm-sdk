package io.sphere.sdk.carts.expansion;

import io.sphere.sdk.expansion.ExpansionModel;
import io.sphere.sdk.expansion.ExpansionPath;
import io.sphere.sdk.shippingmethods.expansion.ShippingMethodExpansionModel;

import javax.annotation.Nullable;

public class ShippingInfoExpansionModel<T> extends ExpansionModel<T> {
    public ShippingInfoExpansionModel(@Nullable final String parentPath, @Nullable final String path) {
        super(parentPath, path);
    }

    public ExpansionPath<T> taxCategory() {
        return expansionPath("taxCategory");
    }

    public ShippingMethodExpansionModel<T> shippingMethod() {
        return new ShippingMethodExpansionModel<>(buildPathExpression(), "shippingMethod");
    }
}
