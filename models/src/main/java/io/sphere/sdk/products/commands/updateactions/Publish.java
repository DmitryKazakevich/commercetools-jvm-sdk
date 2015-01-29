package io.sphere.sdk.products.commands.updateactions;

import io.sphere.sdk.products.Product;
import io.sphere.sdk.commands.UpdateAction;

/**
 * Publishes a product, which causes the staged projection of the product to override the current projection. If the product is published for the first time, the current projection is created.
 *
 * {@include.example io.sphere.sdk.products.commands.ProductUpdateCommandTest#publish()}
 */
public class Publish extends UpdateAction<Product> {
    private Publish() {
        super("publish");
    }

    public static Publish of() {
        return new Publish();
    }
}
