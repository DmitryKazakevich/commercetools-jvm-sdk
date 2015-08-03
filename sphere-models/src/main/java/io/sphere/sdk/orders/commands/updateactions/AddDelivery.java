package io.sphere.sdk.orders.commands.updateactions;

import io.sphere.sdk.commands.UpdateActionImpl;
import io.sphere.sdk.orders.DeliveryItem;
import io.sphere.sdk.orders.Order;

import java.util.Collections;
import java.util.List;

/**

 {@include.example io.sphere.sdk.orders.commands.OrderUpdateCommandTest#addDelivery()}
 */
public class AddDelivery extends UpdateActionImpl<Order> {
    private final List<DeliveryItem> items;
    private final List<ParcelDraft> parcels;


    private AddDelivery(final List<DeliveryItem> items, final List<ParcelDraft> parcels) {
        super("addDelivery");
        this.items = items;
        this.parcels = parcels;
    }

    public static AddDelivery of(final List<DeliveryItem> items, final List<ParcelDraft> parcels) {
        return new AddDelivery(items, parcels);
    }

    public static AddDelivery of(final List<DeliveryItem> items) {
        return of(items, Collections.<ParcelDraft>emptyList());
    }

    public List<DeliveryItem> getItems() {
        return items;
    }

    public List<ParcelDraft> getParcels() {
        return parcels;
    }
}
