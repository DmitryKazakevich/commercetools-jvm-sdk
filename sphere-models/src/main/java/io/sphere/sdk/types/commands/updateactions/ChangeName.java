package io.sphere.sdk.types.commands.updateactions;

import io.sphere.sdk.commands.UpdateActionImpl;
import io.sphere.sdk.models.LocalizedString;
import io.sphere.sdk.types.Type;

public class ChangeName extends UpdateActionImpl<Type> {
    private final LocalizedString name;

    private ChangeName(final LocalizedString name) {
        super("changeName");
        this.name = name;
    }

    public static ChangeName of(final LocalizedString name) {
        return new ChangeName(name);
    }

    public LocalizedString getName() {
        return name;
    }
}
