package io.sphere.sdk.search.model;

import io.sphere.sdk.search.SortExpression;

import static io.sphere.sdk.search.SearchSortDirection.*;

/**
 * A sort model to decide the direction of a model with multiple values per entity.
 * @see MultiValueSortSearchModelFactory to instantiate this class
 */
public class MultiValueSortSearchModel<T> extends SortSearchModelImpl<T> {

    MultiValueSortSearchModel(final SearchModel<T> searchModel) {
        super(searchModel);
    }

    /**
     * When the sort direction is ascending, the minimum value is used.
     * @return the ascending sort direction
     */
    @Override
    public SortExpression<T> byAsc() {
        return super.byAsc();
    }

    /**
     * When the direction is descending, the maximum value is used.
     * @return the descending sort direction
     */
    @Override
    public SortExpression<T> byDesc() {
        return super.byDesc();
    }

    /**
     * Changes the default behaviour of the ascending sort by using the maximum value instead.
     * @return the ascending sort direction using the maximum value
     */
    public SortExpression<T> byAscWithMax() {
        return by(ASC_MAX);
    }

    /**
     * Changes the default behaviour of the descending sort by using the minimum value instead.
     * @return the descending sort direction using the minimum value
     */
    public SortExpression<T> byDescWithMin() {
        return by(DESC_MIN);
    }

    /**
     * Creates an instance of the search model to generate multi-valued sort expressions.
     * @param attributePath the path of the attribute as expected by Commercetools Platform (e.g. "variants.attributes.color.key")
     * @param <T> type of the resource
     * @return new instance of MultiValueSortSearchModel
     */
    public static <T> MultiValueSortSearchModel<T> of(final String attributePath) {
        return new MultiValueSortSearchModel<>(new SearchModelImpl<>(attributePath));
    }
}
