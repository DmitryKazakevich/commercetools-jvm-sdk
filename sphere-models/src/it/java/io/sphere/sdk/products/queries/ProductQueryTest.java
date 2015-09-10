package io.sphere.sdk.products.queries;

import io.sphere.sdk.channels.ChannelFixtures;
import io.sphere.sdk.channels.ChannelRole;
import io.sphere.sdk.customergroups.CustomerGroupFixtures;
import io.sphere.sdk.expansion.ExpansionPath;
import io.sphere.sdk.productdiscounts.ProductDiscount;
import io.sphere.sdk.products.Price;
import io.sphere.sdk.products.Product;
import io.sphere.sdk.products.ProductFixtures;
import io.sphere.sdk.products.VariantIdentifier;
import io.sphere.sdk.products.commands.ProductUpdateCommand;
import io.sphere.sdk.products.commands.updateactions.Publish;
import io.sphere.sdk.products.commands.updateactions.Unpublish;
import io.sphere.sdk.products.expansion.ProductExpansionModel;
import io.sphere.sdk.queries.PagedQueryResult;
import io.sphere.sdk.queries.Query;
import io.sphere.sdk.suppliers.VariantsCottonTShirtProductDraftSupplier;
import io.sphere.sdk.test.IntegrationTest;
import org.junit.Test;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static io.sphere.sdk.customergroups.CustomerGroupFixtures.withCustomerGroup;
import static io.sphere.sdk.productdiscounts.ProductDiscountFixtures.withUpdateableProductDiscount;
import static io.sphere.sdk.products.ProductFixtures.*;
import static io.sphere.sdk.test.SphereTestUtils.assertEventually;
import static io.sphere.sdk.test.SphereTestUtils.randomString;
import static org.assertj.core.api.Assertions.assertThat;

public class ProductQueryTest extends IntegrationTest {

    @Test
    public void isPublished() throws Exception {
        withUpdateableProduct(client(), product -> {
            assertThat(product.getMasterData().isPublished()).isFalse();
            checkIsFoundByPublishedFlag(product, false);

            final Product publishedProduct = execute(ProductUpdateCommand.of(product, Publish.of()));
            assertThat(publishedProduct.getMasterData().isPublished()).isTrue();
            checkIsFoundByPublishedFlag(product, true);


            final Product unpublishedProduct = execute(ProductUpdateCommand.of(publishedProduct, Unpublish.of()));
            assertThat(unpublishedProduct.getMasterData().isPublished()).isFalse();
            return unpublishedProduct;
        });
    }

    private void checkIsFoundByPublishedFlag(final Product product, final boolean value) {
        final Optional<Product> productFromQuery = execute(ProductQuery.of()
                .withPredicates(m -> {
                    return m.masterData().isPublished().is(value);
                })
                .plusPredicates(m -> m.id().is(product.getId()))).head();
        assertThat(productFromQuery.get().getId()).isEqualTo(product.getId());
    }

    @Test
    public void variantIdentifierIsAvailable() throws Exception {
        withProduct(client(), product -> {
            final VariantIdentifier identifier = product.getMasterData().getStaged().getMasterVariant().getIdentifier();
            assertThat(identifier).isEqualTo(VariantIdentifier.of(product.getId(), 1));
        });
    }

    @Test
    public void canExpandItsCategories() throws Exception {
        withProductInCategory(client(), (product, category) -> {
            final Query<Product> query = query(product)
                    .withExpansionPaths(ProductExpansionModel.of().masterData().staged().categories());
            assertThat(execute(query).head().get().getMasterData().getStaged().getCategories().stream().anyMatch(reference -> reference.getObj() != null))
                    .isTrue();
        });
    }


    @Test
    public void canExpandCustomerGroupOfPrices() throws Exception {
        withCustomerGroup(client(), customerGroup ->
                        withUpdateablePricedProduct(client(), PRICE.withCustomerGroup(customerGroup), product -> {
                            final ExpansionPath<Product> expansionPath = ProductExpansionModel.of().masterData().staged().masterVariant().prices().customerGroup();
                            final Query<Product> query = query(product).withExpansionPaths(expansionPath);
                            final List<Price> prices = execute(query).head().get().getMasterData().getStaged().getMasterVariant().getPrices();
                            assertThat(prices
                                    .stream()
                                    .anyMatch(price -> Optional.ofNullable(price.getCustomerGroup()).map(customerGroupReference -> customerGroupReference.getObj() != null).orElse(false)))
                                    .isTrue();
                            return product;
                        })
        );
    }

    @Test
    public void canExpandChannelOfPrices() throws Exception {
        ChannelFixtures.withChannelOfRole(client(), ChannelRole.INVENTORY_SUPPLY, channel -> {
            withUpdateablePricedProduct(client(), PRICE.withChannel(channel), product -> {
                final ExpansionPath<Product> expansionPath = ProductExpansionModel.of().masterData().staged().masterVariant().prices().channel();
                final Query<Product> query = query(product).withExpansionPaths(expansionPath);
                final List<Price> prices = execute(query).head().get().getMasterData().getStaged().getMasterVariant().getPrices();
                assertThat(prices
                        .stream()
                        .anyMatch(price -> Optional.ofNullable(price.getChannel()).map(channelRef -> channelRef.getObj() != null).orElse(false)))
                        .isTrue();
                return product;
            });
        });
    }


    @Test
    public void queryProductsWithAnyDiscount() throws Exception {
        withUpdateableProductDiscount(client(), (ProductDiscount productDiscount, Product product) -> {
            final ProductQuery query = ProductQuery.of()
                    .withPredicates(m -> m.id().is(product.getId())
                            .and(m.masterData().staged().masterVariant().prices().discounted().isPresent()));
            final Duration maxWaitTime = Duration.ofMinutes(2);
            final Duration waitBeforeRetry = Duration.ofMillis(500);
            assertEventually(maxWaitTime, waitBeforeRetry, () -> {
                final Optional<Product> loadedProduct = execute(query).head();
                assertThat(loadedProduct.isPresent()).isTrue();
                assertThat(loadedProduct.get().getId()).isEqualTo(product.getId());
            });
            return productDiscount;
        });
    }

    @Test
    public void expandVariants() {
        CustomerGroupFixtures.withB2cCustomerGroup(client(), customerGroup ->
            ProductFixtures.withProductType(client(), randomString(), productType ->
                withProduct(client(), new VariantsCottonTShirtProductDraftSupplier(productType, randomString(), customerGroup), product -> {
                    final PagedQueryResult<Product> result = execute(ProductQuery.of()
                            .withPredicates(m -> m.id().is(product.getId()))
                            .withExpansionPaths(m -> m.masterData().staged().variants().prices().customerGroup())
                            .withLimit(1));
                    final Price priceWithCustomerGroup = result.head().get().getMasterData().getStaged().getVariants().get(0).getPrices().stream()
                            .filter(price -> Objects.equals(price.getCustomerGroup(), customerGroup.toReference()))
                            .findFirst().get();
                    assertThat(priceWithCustomerGroup.getCustomerGroup().getObj()).isNotNull().isEqualTo(customerGroup);
                })
            )
        );
    }

    private ProductQuery query(final Product product) {
        return ProductQuery.of().withPredicates(m -> m.id().is(product.getId()));
    }
}