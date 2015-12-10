package io.sphere.sdk.types;

import io.sphere.sdk.categories.Category;
import io.sphere.sdk.client.TestClient;
import io.sphere.sdk.models.EnumValue;
import io.sphere.sdk.models.LocalizedString;
import io.sphere.sdk.models.TextInputHint;
import io.sphere.sdk.types.commands.TypeCreateCommand;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static io.sphere.sdk.test.SphereTestUtils.asList;
import static io.sphere.sdk.test.SphereTestUtils.en;

public final class CreateTypeDemo {

    public static Type createType(final TestClient client) {
        final LocalizedString name = en("type for standard categories");
        final String key = "category-customtype-key";
        final String categoryResourceTypeId = Category.resourceTypeId();
        //this enables the type only to be used for categories
        final Set<String> resourceTypeIds = Collections.singleton(categoryResourceTypeId);

        final List<FieldDefinition> fieldDefinitions =
                asList(stateFieldDefinition(), imageUrlFieldDefinition(), relatedCategoriesFieldDefinition());
        final TypeDraft typeDraft = TypeDraftBuilder.of(key, name, resourceTypeIds)
                .fieldDefinitions(fieldDefinitions)
                .build();

        return client.executeBlocking(TypeCreateCommand.of(typeDraft));
    }

    private static FieldDefinition stateFieldDefinition() {
        final List<EnumValue> values = asList(
                EnumValue.of("published", "the category is publicly visible"),
                EnumValue.of("draft", "the category should not be displayed in the frontend")
        );
        final boolean required = false;
        final LocalizedString label = en("state of the category concerning to show it publicly");
        final String fieldName = "state";
        return FieldDefinition
                .of(EnumType.of(values), fieldName, label, required, TextInputHint.SINGLE_LINE);
    }

    private static FieldDefinition imageUrlFieldDefinition() {
        final LocalizedString imageUrlLabel =
                en("absolute url to an image to display for the category");
        return FieldDefinition
                .of(StringType.of(), "imageUrl", imageUrlLabel, false, TextInputHint.SINGLE_LINE);
    }

    private static FieldDefinition relatedCategoriesFieldDefinition() {
        final LocalizedString relatedCategoriesLabel =
                en("categories to suggest products similar to the current category");
        //referenceTypeId is required to refere to categories
        final String referenceTypeId = Category.referenceTypeId();
        final SetType setType = SetType.of(ReferenceType.of(referenceTypeId));
        return FieldDefinition
                .of(setType, "relatedCategories", relatedCategoriesLabel,
                        false, TextInputHint.SINGLE_LINE);
    }
}
