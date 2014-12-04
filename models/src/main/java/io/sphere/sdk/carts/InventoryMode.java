package io.sphere.sdk.carts;

import com.fasterxml.jackson.annotation.JsonCreator;
import io.sphere.sdk.models.SphereEnumeration;

public enum InventoryMode implements SphereEnumeration {
    TRACK_ONLY, RESERVE_ON_ORDER, NONE;

    public static InventoryMode defaultValue() {
        return InventoryMode.NONE;
    }

    @JsonCreator
    public static InventoryMode ofSphereValue(final String value) {
        return SphereEnumeration.find(values(), value);
    }
}
