package io.sphere.sdk.discountcodes.queries;

import io.sphere.sdk.discountcodes.DiscountCode;
import io.sphere.sdk.test.IntegrationTest;
import org.junit.Test;

import static io.sphere.sdk.discountcodes.DiscountCodeFixtures.withPersistentDiscountCode;
import static org.assertj.core.api.Assertions.*;

public class DiscountCodeQueryTest extends IntegrationTest {
    @Test
    public void execution() throws Exception {
        withPersistentDiscountCode(client(), discountCode -> {
            final DiscountCode actual = execute(DiscountCodeQuery.of()
                    .withPredicate(DiscountCodeQueryModel.of().code().is(discountCode.getCode()))).head().get();
            assertThat(actual.getId()).isEqualTo(discountCode.getId());
        });
    }
}