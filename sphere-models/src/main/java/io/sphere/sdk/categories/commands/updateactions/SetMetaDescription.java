package io.sphere.sdk.categories.commands.updateactions;

import io.sphere.sdk.categories.Category;
import io.sphere.sdk.commands.UpdateActionImpl;
import io.sphere.sdk.models.LocalizedString;

import javax.annotation.Nullable;

/**
 * Sets the SEO attribute description.
 *
 * {@include.example io.sphere.sdk.categories.commands.CategoryUpdateCommandTest#setMetaDescription()}
 */
public class SetMetaDescription extends UpdateActionImpl<Category> {
    @Nullable
    private final LocalizedString metaDescription;

    private SetMetaDescription(final LocalizedString metaDescription) {
        super("setMetaDescription");
        this.metaDescription = metaDescription;
    }

    public static SetMetaDescription of(@Nullable final LocalizedString metaDescription) {
        return new SetMetaDescription(metaDescription);
    }

    @Nullable
    public LocalizedString getMetaDescription() {
        return metaDescription;
    }
}
