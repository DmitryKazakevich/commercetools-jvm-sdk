package io.sphere.sdk.search;

import javax.annotation.Nullable;

public class StringSearchModel<T, S extends SearchSortDirection> extends TermModelImpl<T, String> implements SearchSortingModel<T, S> {

    public StringSearchModel(@Nullable final SearchModel<T> parent, final String pathSegment) {
        super(parent, pathSegment);
    }

    @Override
    public FilterSearchModel<T, String> filtered() {
        return new FilterSearchModel<>(this, null, TypeSerializer.ofString());
    }

    @Override
    public FacetSearchModel<T, String> faceted() {
        return new FacetSearchModel<>(this, null, TypeSerializer.ofString());
    }

    @Override
    public SearchSort<T> sorted(S sortDirection) {
        return new SphereSearchSort<>(this, sortDirection);
    }
}