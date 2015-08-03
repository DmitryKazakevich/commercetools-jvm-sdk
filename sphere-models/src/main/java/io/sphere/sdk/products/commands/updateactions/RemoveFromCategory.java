package io.sphere.sdk.products.commands.updateactions;

import io.sphere.sdk.categories.Category;
import io.sphere.sdk.commands.UpdateActionImpl;
import io.sphere.sdk.models.Reference;
import io.sphere.sdk.models.Referenceable;
import io.sphere.sdk.products.Product;

/**
 * Removes a product from a category.
 *
 * {@include.example io.sphere.sdk.products.commands.ProductUpdateCommandTest#addToCategory()}
 *
 * @see io.sphere.sdk.products.commands.updateactions.AddToCategory
 */
public class RemoveFromCategory extends UpdateActionImpl<Product> {
    private final Reference<Category> category;

    private RemoveFromCategory(final Reference<Category> category) {
        super("removeFromCategory");
        this.category = category;
    }

    public Reference<Category> getCategory() {
        return category;
    }

    public static RemoveFromCategory of(final Referenceable<Category> category) {
        return new RemoveFromCategory(category.toReference());
    }
}
