package io.sphere.sdk.products.queries;

import io.sphere.sdk.products.attributes.AttributeAccess;
import io.sphere.sdk.products.attributes.NamedAttributeAccess;
import io.sphere.sdk.categories.Category;
import io.sphere.sdk.categories.commands.CategoryUpdateCommand;
import io.sphere.sdk.categories.commands.updateactions.ChangeParent;
import io.sphere.sdk.channels.ChannelFixtures;
import io.sphere.sdk.channels.ChannelRole;
import io.sphere.sdk.models.LocalizedString;
import io.sphere.sdk.models.MetaAttributes;
import io.sphere.sdk.models.Reference;
import io.sphere.sdk.products.*;
import io.sphere.sdk.products.commands.ProductUpdateCommand;
import io.sphere.sdk.products.commands.updateactions.*;
import io.sphere.sdk.queries.PagedQueryResult;
import io.sphere.sdk.queries.Query;
import io.sphere.sdk.queries.QueryPredicate;
import io.sphere.sdk.taxcategories.TaxCategoryFixtures;
import io.sphere.sdk.test.IntegrationTest;
import org.junit.Test;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static io.sphere.sdk.categories.CategoryFixtures.withCategory;
import static io.sphere.sdk.customergroups.CustomerGroupFixtures.withCustomerGroup;
import static io.sphere.sdk.products.ProductFixtures.*;
import static io.sphere.sdk.products.ProductProjectionType.CURRENT;
import static io.sphere.sdk.products.ProductProjectionType.STAGED;
import static io.sphere.sdk.queries.QuerySortDirection.DESC;
import static io.sphere.sdk.test.ReferenceAssert.assertThat;
import static io.sphere.sdk.test.SphereTestUtils.*;
import static java.util.Arrays.asList;
import static java.util.Locale.ENGLISH;
import static java.util.stream.Collectors.toSet;
import static org.assertj.core.api.Assertions.assertThat;

public class ProductProjectionQueryTest extends IntegrationTest {
    public static final int MASTER_VARIANT_ID = 1;

    @Test
    public void expandProductReferencesInProductAttributes() throws Exception {
        withProductWithProductReference(client(), (product, referencedProduct) -> {
            final Query<ProductProjection> query = ProductProjectionQuery.ofStaged()
                    .withPredicates(m -> m.id().is(product.getId()))
                    .withExpansionPaths(m -> m.masterVariant().attributes().value())
                    .toQuery();
            final ProductProjection productProjection = execute(query).head().get();
            final NamedAttributeAccess<Reference<Product>> namedAttributeAccess = AttributeAccess.ofProductReference().ofName("productreference");
            final Reference<Product> productReference = productProjection.getMasterVariant().findAttribute(namedAttributeAccess).get();
            final Product expandedReferencedProduct = productReference.getObj();
            assertThat(expandedReferencedProduct.getId()).isEqualTo(referencedProduct.getId());
        });
    }

    @Test
    public void variantIdentifierIsAvailable() throws Exception {
        withProduct(client(), product -> {
            final Query<ProductProjection> query = ProductProjectionQuery.of(STAGED)
                    .withPredicates(m -> m.id().is(product.getId()));
            final ProductProjection productProjection = execute(query).head().get();
            final VariantIdentifier identifier = productProjection.getMasterVariant().getIdentifier();
            assertThat(identifier).isEqualTo(VariantIdentifier.of(product.getId(), 1));
        });
    }

    @Test
    public void differentiateBetweenCurrentAndStaged() throws Exception {
        withUpdateableProduct(client(), product -> {
            final Product publishedProduct = execute(ProductUpdateCommand.of(product, Publish.of()));
            final Product mixedDataProduct = execute(ProductUpdateCommand.of(publishedProduct, ChangeName.of(randomSlug())));
            final LocalizedString nameInCurrent = mixedDataProduct.getMasterData().getCurrent().getName();
            final LocalizedString nameInStaged = mixedDataProduct.getMasterData().getStaged().getName();

            assertThat(execute(ProductProjectionQuery.of(STAGED).withPredicates(m -> m.id().is(product.getId()))).head().get().getName()).isEqualTo(nameInStaged);
            assertThat(execute(ProductProjectionQuery.of(CURRENT).withPredicates(m -> m.id().is(product.getId()))).head().get().getName()).isEqualTo(nameInCurrent);

            return mixedDataProduct;
        });
    }

    @Test
    public void expandCustomerGroupInPrice() throws Exception {
        withCustomerGroup(client(), customerGroup ->
            withUpdateablePricedProduct(client(), PRICE.withCustomerGroup(customerGroup), product -> {
                final Query<ProductProjection> query = ProductProjectionQuery.of(STAGED)
                                .withPredicates(m -> m.id().is(product.getId()))
                                .withExpansionPaths(m -> m.masterVariant().prices().customerGroup());
                final List<Price> prices = execute(query).head().get().getMasterVariant().getPrices();
                assertThat(prices
                        .stream()
                        .anyMatch(price ->  Optional.ofNullable(price.getCustomerGroup()).map(customerGroupReference -> customerGroupReference.getObj() != null).orElse(false)))
                        .isTrue();
                return product;
            })
        );
    }

    @Test
    public void expandChannelInPrice() throws Exception {
        ChannelFixtures.withChannelOfRole(client(), ChannelRole.INVENTORY_SUPPLY, channel -> {
            withUpdateablePricedProduct(client(), PRICE.withChannel(channel), product -> {
                final Query<ProductProjection> query = ProductProjectionQuery.of(STAGED)
                        .withPredicates(m -> m.id().is(product.getId()))
                        .withExpansionPaths(m -> m.masterVariant().prices().channel());
                final List<Price> prices = execute(query).head().get().getMasterVariant().getPrices();
                assertThat(prices
                        .stream()
                        .anyMatch(price -> Optional.ofNullable(price.getChannel()).map(channelRef -> channelRef.getObj() != null).orElse(false)))
                        .isTrue();
                return product;
            });
        });
    }

    @Test
    public void queryByProductType() throws Exception {
        with2products("queryByProductType", (p1, p2) ->{
            final Query<ProductProjection> query =
                    ProductProjectionQuery.of(STAGED)
                            .byProductType(p1.getProductType())
                            .withExpansionPaths(m -> m.productType());
            final PagedQueryResult<ProductProjection> queryResult = execute(query);
            assertThat(queryResult.head().get().getProductType()).isExpanded();
            assertThat(ids(queryResult)).containsOnly(p1.getId());
        });
    }

    @Test
    public void queryById() throws Exception {
        with2products("queryById", (p1, p2) -> {
            final Query<ProductProjection> query1 = ProductProjectionQuery.of(STAGED).withPredicates(m -> m.id().isIn(asList(p1.getId(), p2.getId())));
            assertThat(ids(execute(query1))).containsOnly(p1.getId(), p2.getId());

            final Query<ProductProjection> query = ProductProjectionQuery.of(STAGED).withPredicates(m -> m.id().is(p1.getId()));
            assertThat(ids(execute(query))).containsOnly(p1.getId());
        });
    }

    @Test
    public void queryBySlug() throws Exception {
        with2products("queryBySlug", (p1, p2) ->{
            final Query<ProductProjection> query1 = ProductProjectionQuery.of(STAGED).bySlug(ENGLISH, p1.getMasterData().getStaged().getSlug().get(ENGLISH));
            assertThat(ids(execute(query1))).containsOnly(p1.getId());
        });
    }

    @Test
    public void queryByName() throws Exception {
        with2products("queryByName", (p1, p2) ->{
            final Query<ProductProjection> query1 = ProductProjectionQuery.of(STAGED)
                    .withPredicates(m -> m.name().lang(ENGLISH).is(en(p1.getMasterData().getStaged().getName())))
                    .withSort(m -> m.createdAt().sort().desc())
                    .withLimit(1);
            assertThat(ids(execute(query1))).containsOnly(p1.getId());
        });
    }

    @Test
    public void queryByCategory() throws Exception {
        withCategory(client(), cat3 ->
                        withCategory(client(), cat1 ->
                                        withCategory(client(), cat2 ->
                                                        with2products("queryByCategory", (p1, p2) -> {
                                                            final Category cat1WithParent = execute(CategoryUpdateCommand.of(cat1, asList(ChangeParent.of(cat3))));
                                                            final Product productWithCat1 = execute(ProductUpdateCommand.of(p1, AddToCategory.of(cat1WithParent)));
                                                            final Query<ProductProjection> query = ProductProjectionQuery.of(STAGED)
                                                                    .withPredicates(m -> m.categories().isIn(asList(cat1, cat2)))
                                                                    .withExpansionPaths(m -> m.categories().parent());
                                                            final PagedQueryResult<ProductProjection> queryResult = execute(query);
                                                            assertThat(ids(queryResult)).containsOnly(productWithCat1.getId());
                                                            final Reference<Category> cat1Loaded = queryResult.head().get().getCategories().stream().findAny().get();
                                                            assertThat(cat1Loaded).overridingErrorMessage("cat of product is expanded").isExpanded();
                                                            final Reference<Category> parent = cat1Loaded.getObj().getParent();
                                                            assertThat(parent).overridingErrorMessage("parent of cat is expanded").isExpanded();
                                                        })
                                        )
                        )
        );
    }

    @Test
    public void queryByHasStagedChanges() throws Exception {
        withProduct(client(), product -> {
            final Product updated = execute(ProductUpdateCommand.of(product, ChangeName.of(randomSlug())));
            final PagedQueryResult<ProductProjection> pagedQueryResult = execute(ProductProjectionQuery.of(STAGED)
                    .withPredicates(m -> m.hasStagedChanges().is(true))
                    .withSort(m -> m.createdAt().sort().desc()));
            assertThat(ids(pagedQueryResult)).contains(updated.getId());
        });
    }

    private ProductProjectionQueryModel model() {
        return ProductProjectionQueryModel.of();
    }

    @Test
    public void queryBySku() throws Exception {
        withProduct(client(), product -> {
            final String sku = "sku-" + randomString();
            final Product productWithSku = execute(ProductUpdateCommand.of(product, SetSku.of(MASTER_VARIANT_ID, sku)));
            final QueryPredicate<ProductProjection> predicate = model().masterVariant().sku().is(sku);
            checkOneResult(productWithSku, predicate);
        });
    }

    @Test
    public void queryByMetaAttributes() throws Exception {
        withProduct(client(), product -> {
            final MetaAttributes metaAttributes = randomMetaAttributes();
            final Product productWithMetaAttributes = execute(ProductUpdateCommand.of(product, MetaAttributesUpdateActions.of(metaAttributes)));
            checkOneResult(productWithMetaAttributes, model().metaDescription().lang(ENGLISH).is(en(metaAttributes.getMetaDescription())));
            checkOneResult(productWithMetaAttributes, model().metaTitle().lang(ENGLISH).is(en(metaAttributes.getMetaTitle())));
            checkOneResult(productWithMetaAttributes, model().metaKeywords().lang(ENGLISH).is(en(metaAttributes.getMetaKeywords())));
        });
    }

    @Test
    public void expandTaxCategory() throws Exception {
        TaxCategoryFixtures.withTransientTaxCategory(client(), taxCategory ->
                        withProduct(client(), product -> {
                            final Product productWithTaxCategory = execute(ProductUpdateCommand.of(product, SetTaxCategory.of(taxCategory)));
                            final PagedQueryResult<ProductProjection> pagedQueryResult =
                                    execute(ProductProjectionQuery.of(STAGED)
                                            .withPredicates(m -> m.id().is(productWithTaxCategory.getId()))
                                            .withExpansionPaths(m -> m.taxCategory()));
                            assertThat(pagedQueryResult.head().get().getTaxCategory()).isExpanded();
                        })
        );
    }

    private void checkOneResult(final Product product, final QueryPredicate<ProductProjection> predicate) {
        final PagedQueryResult<ProductProjection> queryResult = execute(ProductProjectionQuery.of(STAGED).withPredicates(predicate));
        assertThat(ids(queryResult)).containsOnly(product.getId());
    }

    private Set<String> ids(final PagedQueryResult<ProductProjection> res) {
        return res.getResults().stream().map(p -> p.getId()).collect(toSet());
    }

    private void with2products(final String testName, final BiConsumer<Product, Product> consumer) {
        final Consumer<Product> user1 = product1 -> {
            final Consumer<Product> user = product2 -> {
                consumer.accept(product1, product2);
            };
            withProduct(client(), testName + "2", user);
        };
        withProduct(client(), testName + "1", user1);
    }
}