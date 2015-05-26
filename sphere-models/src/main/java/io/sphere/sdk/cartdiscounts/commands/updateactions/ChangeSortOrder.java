package io.sphere.sdk.cartdiscounts.commands.updateactions;

import io.sphere.sdk.cartdiscounts.CartDiscount;
import io.sphere.sdk.commands.UpdateAction;

/**
 * {@include.example io.sphere.sdk.cartdiscounts.commands.CartDiscountUpdateCommandTest#changeSortOrder()}
 */
public class ChangeSortOrder extends UpdateAction<CartDiscount> {
    private final String sortOrder;

    private ChangeSortOrder(final String sortOrder) {
        super("changeSortOrder");
        this.sortOrder = sortOrder;
    }

    public static ChangeSortOrder of(final String sortOrder) {
        return new ChangeSortOrder(sortOrder);
    }

    public String getSortOrder() {
        return sortOrder;
    }
}
