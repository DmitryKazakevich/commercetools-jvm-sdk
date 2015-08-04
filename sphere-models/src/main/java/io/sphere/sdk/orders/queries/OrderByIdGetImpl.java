package io.sphere.sdk.orders.queries;

import io.sphere.sdk.orders.Order;
import io.sphere.sdk.orders.expansion.OrderExpansionModel;
import io.sphere.sdk.queries.MetaModelFetchDslBuilder;
import io.sphere.sdk.queries.MetaModelGetDslImpl;

/**
 Gets an order by ID.

 {@include.example io.sphere.sdk.orders.commands.OrderFromCartCreateCommandTest#execution()}
 */
final class OrderByIdGetImpl extends MetaModelGetDslImpl<Order, Order, OrderByIdGet, OrderExpansionModel<Order>> implements OrderByIdGet {
    OrderByIdGetImpl(final String id) {
        super(id, OrderEndpoint.ENDPOINT, OrderExpansionModel.of(), OrderByIdGetImpl::new);
    }

    public OrderByIdGetImpl(MetaModelFetchDslBuilder<Order, Order, OrderByIdGet, OrderExpansionModel<Order>> builder) {
        super(builder);
    }
}
