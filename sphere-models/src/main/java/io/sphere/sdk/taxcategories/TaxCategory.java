package io.sphere.sdk.taxcategories;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.sphere.sdk.models.Resource;
import io.sphere.sdk.models.Reference;

import javax.annotation.Nullable;
import java.util.List;

/** Tax Categories define how products are to be taxed in different countries.

@see io.sphere.sdk.taxcategories.queries.TaxCategoryByIdGet
@see io.sphere.sdk.taxcategories.queries.TaxCategoryQuery
@see io.sphere.sdk.taxcategories.commands.TaxCategoryCreateCommand
@see io.sphere.sdk.taxcategories.commands.TaxCategoryUpdateCommand
@see io.sphere.sdk.taxcategories.commands.TaxCategoryDeleteCommand
 */
@JsonDeserialize(as=TaxCategoryImpl.class)
public interface TaxCategory extends Resource<TaxCategory> {
    String getName();

    @Nullable
    String getDescription();

    List<TaxRate> getTaxRates();

    static TypeReference<TaxCategory> typeReference(){
        return new TypeReference<TaxCategory>() {
            @Override
            public String toString() {
                return "TypeReference<TaxCategory>";
            }
        };
    }

    @Override
    default Reference<TaxCategory> toReference() {
        return Reference.of(referenceTypeId(), getId(), this);
    }

    static String referenceTypeId() {
        return "tax-category";
    }

    /**
     *
     * @deprecated use {@link #referenceTypeId()} instead
     * @return referenceTypeId
     */
    @Deprecated
    static String typeId(){
        return "tax-category";
    }

    static Reference<TaxCategory> referenceOfId(final String id) {
        return Reference.of(referenceTypeId(), id);
    }
}
