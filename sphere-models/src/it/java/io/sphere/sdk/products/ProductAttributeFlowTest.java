package io.sphere.sdk.products;

import io.sphere.sdk.attributes.*;
import io.sphere.sdk.json.JsonUtils;
import io.sphere.sdk.models.LocalizedEnumValue;
import io.sphere.sdk.models.Reference;
import io.sphere.sdk.products.commands.ProductCreateCommand;
import io.sphere.sdk.products.commands.ProductUpdateCommand;
import io.sphere.sdk.products.commands.updateactions.SetAttribute;
import io.sphere.sdk.producttypes.ProductType;
import io.sphere.sdk.producttypes.ProductTypeDraft;
import io.sphere.sdk.producttypes.commands.ProductTypeCreateCommand;
import io.sphere.sdk.producttypes.queries.ProductTypeQuery;
import io.sphere.sdk.test.IntegrationTest;
import org.junit.Test;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static io.sphere.sdk.test.SphereTestUtils.*;

public class ProductAttributeFlowTest extends IntegrationTest {

    public static final String LOCALIZED_ENUM_ATTR = "lenumattr";
    public static final LocalizedEnumValue A = LocalizedEnumValue.of("a", en("value a"));
    public static final LocalizedEnumValue B = LocalizedEnumValue.of("b", en("value b"));
    public static final LocalizedEnumValue C = LocalizedEnumValue.of("c", en("value c"));
    private static final Reference<Product> EXPANDED_PRODUCT_REFERENCE = JsonUtils.readObjectFromResource("product1.json", Product.typeReference()).toReference();

    @Test
    public void localizedEnumValue() throws Exception {
        final String productTypeName = ProductAttributeFlowTest.class.getSimpleName() + "lenum";
        final List<LocalizedEnumValue> localizedEnumValues = asList(A, B, C);
        final ProductTypeDraft draft = ProductTypeDraft.of(productTypeName, "", asList(AttributeDefinitionBuilder.of(LOCALIZED_ENUM_ATTR, randomSlug(), LocalizedEnumType.of(localizedEnumValues)).build()));
        final ProductTypeQuery query = ProductTypeQuery.of().byName(productTypeName);
        final ProductTypeCreateCommand createCommand = ProductTypeCreateCommand.of(draft);
        final ProductType productType = getOrCreate(createCommand, query);
        final NamedAttributeAccess<LocalizedEnumValue> namesAccess = AttributeAccess.ofLocalizedEnumValue().ofName(LOCALIZED_ENUM_ATTR);
        final ProductVariantDraft masterVariant = ProductVariantDraftBuilder.of().attributes(AttributeDraft.of(namesAccess, B)).build();
        final ProductDraft productDraft = ProductDraftBuilder.of(productType, randomSlug(), randomSlug(), masterVariant).build();
        final Product product = execute(ProductCreateCommand.of(productDraft));
        assertThat(extractAttribute(product, namesAccess)).contains(B);

        final Product updatedProduct = execute(ProductUpdateCommand.of(product, SetAttribute.of(1, namesAccess, C, ProductUpdateScope.ONLY_STAGED)));
        assertThat(extractAttribute(updatedProduct, namesAccess)).contains(C);


        final NamedAttributeAccess<LocalizedEnumValue> getterSetter = AttributeAccess.ofLocalizedEnumValue().ofName(LOCALIZED_ENUM_ATTR);
        final Attribute attribute = Attribute.of(getterSetter, C);

        final LocalizedEnumValue value = attribute.getValue(AttributeAccess.ofLocalizedEnumValue());
        assertThat(value).isEqualTo(C);
    }

    @Test
    public void attributeShouldKeepExpansions() throws Exception {
        final AttributeAccess<Reference<Product>> access = AttributeAccess.ofProductReference();
        final NamedAttributeAccess<Reference<Product>> namedAccess = access.ofName("foo");
        assertThat(EXPANDED_PRODUCT_REFERENCE.getObj()).overridingErrorMessage("product reference is expanded").isPresent();
        final Attribute attribute = Attribute.of(namedAccess, EXPANDED_PRODUCT_REFERENCE);
        assertThat(attribute.getValue(access).getObj()).isPresent();

        final String jsonFilledRef = JsonUtils.toJson(EXPANDED_PRODUCT_REFERENCE);
        final String jsonEmptyRef = JsonUtils.toJson(EXPANDED_PRODUCT_REFERENCE.filled(Optional.<Product>empty()));
        assertThat(jsonFilledRef)
                .overridingErrorMessage("references are not expanded if serialized")
                .doesNotContain(EXPANDED_PRODUCT_REFERENCE.getObj().get().getMasterData().getStaged().getName().get(Locale.ENGLISH).get())
                .isEqualTo(jsonEmptyRef);
    }

    @Test
    public void inAttributeDraftsNoExpansionIsSent() throws Exception {
        final AttributeDraft attributeDraft = AttributeDraft.of(AttributeAccess.ofProductReference().ofName("foo"), EXPANDED_PRODUCT_REFERENCE);
        assertThat(attributeDraft.getValue().toString()).isEqualTo("{\"typeId\":\"product\",\"id\":\"e7a7ca51-475b-4bc7-9c2a-254eafbb0d94\"}");
    }

    private Optional<LocalizedEnumValue> extractAttribute(final Product product, final NamedAttributeAccess<LocalizedEnumValue> namesAccess) {
        return product.getMasterData().getStaged().getMasterVariant().getAttribute(namesAccess);
    }
}