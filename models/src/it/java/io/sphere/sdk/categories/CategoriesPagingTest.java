package io.sphere.sdk.categories;

import io.sphere.sdk.categories.queries.CategoryQuery;
import io.sphere.sdk.queries.PagedQueryResult;
import io.sphere.sdk.test.IntegrationTest;
import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

public class CategoriesPagingTest extends IntegrationTest {
    @Test
    public void overPaging() throws Exception {
        final int offset = 100000;
        final PagedQueryResult<Category> result = execute(CategoryQuery.of().withOffset(offset));
        assertThat(result.getOffset()).isEqualTo(100000);
        assertThat(result.size()).isEqualTo(0);
        assertThat(result.getResults().size()).isEqualTo(0);
    }
}
