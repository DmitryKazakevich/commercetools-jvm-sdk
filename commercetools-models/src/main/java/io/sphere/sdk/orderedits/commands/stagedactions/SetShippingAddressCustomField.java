package io.sphere.sdk.orderedits.commands.stagedactions;

import com.fasterxml.jackson.databind.JsonNode;

import javax.annotation.Nullable;

/**
 * Updates a custom field in a custom line item.
 *
 * {@doc.gen intro}
 *
 * @see io.sphere.sdk.types.Custom
 */
public final class SetShippingAddressCustomField extends OrderEditSetCustomFieldBase {

    private SetShippingAddressCustomField(final String name, final JsonNode value) {
        super("setShippingAddressCustomField", name, value);
    }

    public static SetShippingAddressCustomField of(final String name, @Nullable JsonNode value) {
        return new SetShippingAddressCustomField(name, value);
    }

}
