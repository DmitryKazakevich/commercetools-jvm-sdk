package io.sphere.sdk.commands;

import io.sphere.sdk.client.JsonEndpoint;
import io.sphere.sdk.expansion.ExpansionPath;
import io.sphere.sdk.models.Base;
import io.sphere.sdk.models.Builder;
import io.sphere.sdk.models.ResourceView;
import io.sphere.sdk.models.Versioned;

import java.util.List;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;

/**
 Internal builder.
 @param <T> the type of the result of the command
 @param <E> the type of the expansion model
 */
public class MetaModelByIdDeleteCommandBuilder<T extends ResourceView<T>, C, E> extends Base implements Builder<C> {
    final Versioned<T> versioned;
    final JsonEndpoint<T> endpoint;
    final E expansionModel;
    List<ExpansionPath<T>> expansionPaths;
    final Function<MetaModelByIdDeleteCommandBuilder<T, C, E>, C> creationFunction;

    MetaModelByIdDeleteCommandBuilder(final MetaModelByIdDeleteCommandImpl<T, C, E> template) {
        this.creationFunction = requireNonNull(template.creationFunction);
        this.expansionModel = requireNonNull(template.expansionModel);
        this.expansionPaths = requireNonNull(template.expansionPaths);
        this.versioned = requireNonNull(template.versioned);
        this.endpoint = requireNonNull(template.endpoint);
    }

    @Override
    public C build() {
        return creationFunction.apply(this);
    }


    MetaModelByIdDeleteCommandBuilder<T, C, E> expansionPaths(final List<ExpansionPath<T>> expansionPaths) {
        this.expansionPaths = expansionPaths;
        return this;
    }
}
