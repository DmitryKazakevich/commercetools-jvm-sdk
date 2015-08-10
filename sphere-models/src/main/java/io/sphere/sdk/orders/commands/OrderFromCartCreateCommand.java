package io.sphere.sdk.orders.commands;

import io.sphere.sdk.carts.Cart;
import io.sphere.sdk.commands.CreateCommand;
import io.sphere.sdk.expansion.MetaModelExpansionDsl;
import io.sphere.sdk.models.Versioned;
import io.sphere.sdk.orders.Order;
import io.sphere.sdk.orders.OrderFromCartDraft;
import io.sphere.sdk.orders.expansion.OrderExpansionModel;

public interface OrderFromCartCreateCommand extends CreateCommand<Order>, MetaModelExpansionDsl<Order, OrderFromCartCreateCommand, OrderExpansionModel<Order>> {
    static OrderFromCartCreateCommand of(final OrderFromCartDraft draft) {
        return new OrderFromCartCreateCommandImpl(draft);
    }

    static OrderFromCartCreateCommand of(final Versioned<Cart> cart) {
        return new OrderFromCartCreateCommandImpl(OrderFromCartDraft.of(cart));
    }
}
