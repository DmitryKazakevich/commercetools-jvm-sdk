package io.sphere.sdk.productdiscounts.commands;

import io.sphere.sdk.commands.CreateCommand;
import io.sphere.sdk.expansion.MetaModelExpansionDsl;
import io.sphere.sdk.productdiscounts.ProductDiscount;
import io.sphere.sdk.productdiscounts.ProductDiscountDraft;
import io.sphere.sdk.productdiscounts.expansion.ProductDiscountExpansionModel;

/**
 * {@include.example io.sphere.sdk.productdiscounts.commands.ProductDiscountCreateCommandTest#execution()}
 */
public interface ProductDiscountCreateCommand extends CreateCommand<ProductDiscount>, MetaModelExpansionDsl<ProductDiscount, ProductDiscountCreateCommand, ProductDiscountExpansionModel<ProductDiscount>> {
    static ProductDiscountCreateCommand of(final ProductDiscountDraft draft) {
        return new ProductDiscountCreateCommandImpl(draft);
    }
}
