package io.sphere.sdk.inventory;

import com.fasterxml.jackson.annotation.JsonCreator;
import io.sphere.sdk.channels.Channel;
import io.sphere.sdk.models.ResourceImpl;
import io.sphere.sdk.models.Reference;

import javax.annotation.Nullable;
import java.time.ZonedDateTime;

final class InventoryEntryImpl extends ResourceImpl<InventoryEntry> implements InventoryEntry {
    private final String sku;
    @Nullable
    private final Reference<Channel> supplyChannel;
    private final Long quantityOnStock;
    private final Long availableQuantity;
    @Nullable
    private final Integer restockableInDays;
    @Nullable
    private final ZonedDateTime expectedDelivery;

    @JsonCreator
    public InventoryEntryImpl(final String id, final Long version, final ZonedDateTime createdAt, final ZonedDateTime lastModifiedAt, final Long availableQuantity, final String sku, final Reference<Channel> supplyChannel, final Long quantityOnStock, final Integer restockableInDays, final ZonedDateTime expectedDelivery) {
        super(id, version, createdAt, lastModifiedAt);
        this.availableQuantity = availableQuantity;
        this.sku = sku;
        this.supplyChannel = supplyChannel;
        this.quantityOnStock = quantityOnStock;
        this.restockableInDays = restockableInDays;
        this.expectedDelivery = expectedDelivery;
    }

    @Override
    public Long getAvailableQuantity() {
        return availableQuantity;
    }

    @Override
    @Nullable
    public ZonedDateTime getExpectedDelivery() {
        return expectedDelivery;
    }

    @Override
    public Long getQuantityOnStock() {
        return quantityOnStock;
    }

    @Override
    @Nullable
    public Integer getRestockableInDays() {
        return restockableInDays;
    }

    @Override
    public String getSku() {
        return sku;
    }

    @Override
    @Nullable
    public Reference<Channel> getSupplyChannel() {
        return supplyChannel;
    }
}
