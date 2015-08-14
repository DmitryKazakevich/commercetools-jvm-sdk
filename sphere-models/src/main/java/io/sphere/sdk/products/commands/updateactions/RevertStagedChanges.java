package io.sphere.sdk.products.commands.updateactions;

import io.sphere.sdk.commands.UpdateActionImpl;
import io.sphere.sdk.products.Product;

/**
 * Revert all changes, which were made to the staged version of a product and reset to the current version.
 *
 * {@doc.gen intro}
 *
 * {@include.example io.sphere.sdk.products.commands.ProductUpdateCommandTest#revertStagedChanges()}
 */
public class RevertStagedChanges extends UpdateActionImpl<Product> {
    private RevertStagedChanges() {
        super("revertStagedChanges");
    }

    public static RevertStagedChanges of() {
        return new RevertStagedChanges();
    }
}
