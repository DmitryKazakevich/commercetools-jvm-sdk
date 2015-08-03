package io.sphere.sdk.reviews.commands.updateactions;

import io.sphere.sdk.commands.UpdateActionImpl;
import io.sphere.sdk.reviews.Review;

import javax.annotation.Nullable;

/**
 * Updates the text of a review.
 *
 * {@include.example io.sphere.sdk.reviews.commands.ReviewUpdateCommandTest#setText()}
 */
public class SetText extends UpdateActionImpl<Review> {
    @Nullable
    private final String text;

    private SetText(@Nullable final String text) {
        super("setText");
        this.text = text;
    }

    public static SetText of(@Nullable final String text) {
        return new SetText(text);
    }

    @Nullable
    public String getText() {
        return text;
    }
}
