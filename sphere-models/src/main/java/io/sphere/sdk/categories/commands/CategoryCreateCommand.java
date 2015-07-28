package io.sphere.sdk.categories.commands;

import io.sphere.sdk.categories.Category;
import io.sphere.sdk.categories.CategoryDraft;
import io.sphere.sdk.commands.CreateCommand;

/**
 * Command to create a category.
 *
 *
 * For construction of a {@link io.sphere.sdk.categories.CategoryDraft} (a draft for a new category) use a {@link io.sphere.sdk.categories.CategoryDraftBuilder}:
 *
 *
 * {@include.example io.sphere.sdk.categories.commands.CategoryCreateCommandTest#execution()}
 */
public interface CategoryCreateCommand extends CreateCommand<Category> {

    static CategoryCreateCommand of(final CategoryDraft categoryDraft) {
        return new CategoryCreateCommandImpl(categoryDraft);
    }
}
