package io.sphere.sdk.search.model;

import io.sphere.sdk.models.Base;
import io.sphere.sdk.search.SearchSortDirection;
import io.sphere.sdk.search.SortExpression;

import static io.sphere.sdk.utils.IterableUtils.toStream;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;

class SortExpressionImpl<T> extends Base implements SortExpression<T> {
    private final SearchModel<T> searchModel;
    private final SearchSortDirection direction;

    protected SortExpressionImpl(final SearchModel<T> searchModel, final SearchSortDirection direction) {
        this.searchModel = requireNonNull(searchModel);
        this.direction = requireNonNull(direction);
    }

    @Override
    public String value() {
        return direction.toString();
    }

    @Override
    public String expression() {
        return attributePath() + " " + value();
    }

    @Override
    public final String attributePath() {
        return toStream(searchModel.buildPath()).collect(joining("."));
    }

    @Override
    public final boolean equals(Object o) {
        return o != null && o instanceof SortExpression && expression().equals(((SortExpression) o).expression());
    }

    @Override
    public final int hashCode() {
        return expression().hashCode();
    }
}