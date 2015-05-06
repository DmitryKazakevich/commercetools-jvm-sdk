package io.sphere.sdk.carts.queries;

import io.sphere.sdk.carts.Cart;
import io.sphere.sdk.test.IntegrationTest;
import org.junit.Test;

import static io.sphere.sdk.customers.CustomerFixtures.withCustomerAndCart;
import static org.assertj.core.api.Assertions.assertThat;

public class CartByCustomerIdFetchTest extends IntegrationTest {
    @Test
    public void execution() throws Exception {
        withCustomerAndCart(client(), (customer, cart) -> {
            final Cart fetchedCart = execute(CartByCustomerIdFetch.of(customer.getId())).get();
            assertThat(fetchedCart.getId()).isEqualTo(cart.getId());
        });
    }
}