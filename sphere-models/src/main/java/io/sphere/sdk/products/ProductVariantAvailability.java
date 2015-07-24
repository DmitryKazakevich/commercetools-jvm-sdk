package io.sphere.sdk.products;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import javax.annotation.Nullable;

@JsonDeserialize(as = ProductVariantAvailabilityImpl.class)
public interface ProductVariantAvailability {
    boolean isOnStock();

    @Nullable
    Integer getRestockableInDays();
}
