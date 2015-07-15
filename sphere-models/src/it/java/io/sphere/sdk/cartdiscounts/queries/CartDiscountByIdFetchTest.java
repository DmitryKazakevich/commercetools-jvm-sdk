package io.sphere.sdk.cartdiscounts.queries;

import io.sphere.sdk.cartdiscounts.CartDiscount;
import io.sphere.sdk.test.IntegrationTest;
import org.junit.Test;

import static io.sphere.sdk.cartdiscounts.CartDiscountFixtures.withPersistentCartDiscount;
import static org.assertj.core.api.Assertions.*;

public class CartDiscountByIdFetchTest extends IntegrationTest {
    @Test
    public void execution() throws Exception {
        withPersistentCartDiscount(client(), cartDiscount -> {
            final CartDiscount fetchedDiscount = execute(CartDiscountByIdFetch.of(cartDiscount));
            assertThat(fetchedDiscount.getId()).isEqualTo(cartDiscount.getId());
        });
    }
}