package io.sphere.sdk.queries;

import org.junit.Test;

import java.util.Arrays;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static io.sphere.sdk.queries.Fixtures.emptyQueryModel;

public class IsInQueryPredicateTest {

    private final QueryPredicate<String> model = create("foo", "bar\"evil", "baz");

    private QueryPredicate<String> create(String ... values) {
        return new IsInQueryPredicate<String, String, String>(emptyQueryModel, Arrays.stream(values)
                .map(StringQuerySortingModelImpl::normalize)
                .collect(toList()));
    }

    @Test
    public void queryExpression() throws Exception {
        assertThat(model.toSphereQuery()).isEqualTo(" in (\"foo\", \"bar\\\"evil\", \"baz\")");
    }

    @Test
    public void testToString() throws Exception {
        assertThat(model.toString()).contains("foo");
    }

    @Test(expected = IllegalArgumentException.class)
    public void requireAtLeastOneArgument() throws Exception {
        create();
    }
}
