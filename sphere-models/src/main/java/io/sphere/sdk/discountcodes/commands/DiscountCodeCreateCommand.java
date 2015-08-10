package io.sphere.sdk.discountcodes.commands;

import io.sphere.sdk.commands.CreateCommand;
import io.sphere.sdk.discountcodes.DiscountCode;
import io.sphere.sdk.discountcodes.DiscountCodeDraft;
import io.sphere.sdk.discountcodes.expansion.DiscountCodeExpansionModel;
import io.sphere.sdk.expansion.MetaModelExpansionDsl;

/**
 * {@include.example io.sphere.sdk.discountcodes.commands.DiscountCodeCreateCommandTest#execution()}
 */
public interface DiscountCodeCreateCommand extends CreateCommand<DiscountCode>, MetaModelExpansionDsl<DiscountCode, DiscountCodeCreateCommand, DiscountCodeExpansionModel<DiscountCode>> {

    static DiscountCodeCreateCommand of(final DiscountCodeDraft draft) {
        return new DiscountCodeCreateCommandImpl(draft);
    }
}
