package io.sphere.sdk.products.queries;

import com.fasterxml.jackson.core.type.TypeReference;
import io.sphere.sdk.expansion.ExpansionPath;
import io.sphere.sdk.models.Referenceable;
import io.sphere.sdk.products.ProductProjection;
import io.sphere.sdk.products.ProductProjectionType;
import io.sphere.sdk.products.expansion.ProductProjectionExpansionModel;
import io.sphere.sdk.producttypes.ProductType;
import io.sphere.sdk.queries.*;

import java.util.List;
import java.util.Locale;
import java.util.function.Function;

/**
 {@doc.gen summary product projections}
 */
public interface ProductProjectionQuery extends MetaModelQueryDsl<ProductProjection, ProductProjectionQuery, ProductProjectionQueryModel, ProductProjectionExpansionModel<ProductProjection>> {

    static TypeReference<PagedQueryResult<ProductProjection>> resultTypeReference() {
        return new TypeReference<PagedQueryResult<ProductProjection>>(){
            @Override
            public String toString() {
                return "TypeReference<PagedQueryResult<ProductProjection>>";
            }
        };
    }

    default ProductProjectionQuery byProductType(final Referenceable<ProductType> productType) {
        return withPredicates(m -> m.productType().is(productType));
    }

    default ProductProjectionQuery bySlug(final Locale locale, final String slug) {
        return withPredicates(m -> m.slug().lang(locale).is(slug));
    }

    static ProductProjectionQuery ofStaged() {
        return of(ProductProjectionType.STAGED);
    }

    static ProductProjectionQuery ofCurrent() {
        return of(ProductProjectionType.CURRENT);
    }

    static ProductProjectionQuery of(final ProductProjectionType productProjectionType) {
        return new ProductProjectionQueryImpl(productProjectionType);
    }

    @Override
    ProductProjectionQuery withLimit(final long limit);

    @Override
    ProductProjectionQuery withOffset(final long offset);

    @Override
    ProductProjectionQuery withPredicates(final List<QueryPredicate<ProductProjection>> queryPredicates);

    @Override
    ProductProjectionQuery withPredicates(final QueryPredicate<ProductProjection> queryPredicate);

    @Override
    ProductProjectionQuery withPredicates(final Function<ProductProjectionQueryModel, QueryPredicate<ProductProjection>> m);

    @Override
    ProductProjectionQuery plusPredicates(final List<QueryPredicate<ProductProjection>> queryPredicates);

    @Override
    ProductProjectionQuery plusPredicates(final QueryPredicate<ProductProjection> queryPredicate);

    @Override
    ProductProjectionQuery plusPredicates(final Function<ProductProjectionQueryModel, QueryPredicate<ProductProjection>> m);

    @Override
    ProductProjectionQuery withSort(final Function<ProductProjectionQueryModel, QuerySort<ProductProjection>> m);

    @Override
    ProductProjectionQuery withSort(final List<QuerySort<ProductProjection>> sort);

    @Override
    ProductProjectionQuery withSort(final QuerySort<ProductProjection> sort);

    @Override
    ProductProjectionQuery withSortMulti(final Function<ProductProjectionQueryModel, List<QuerySort<ProductProjection>>> m);

    @Override
    ProductProjectionQuery plusExpansionPaths(final Function<ProductProjectionExpansionModel<ProductProjection>, ExpansionPath<ProductProjection>> m);

    @Override
    ProductProjectionQuery withExpansionPaths(final Function<ProductProjectionExpansionModel<ProductProjection>, ExpansionPath<ProductProjection>> m);
}