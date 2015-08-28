package io.sphere.sdk.commands;

import com.fasterxml.jackson.core.type.TypeReference;
import io.sphere.sdk.client.HttpRequestIntent;
import io.sphere.sdk.client.JsonEndpoint;
import io.sphere.sdk.expansion.ExpansionDslUtil;
import io.sphere.sdk.expansion.ExpansionPath;
import io.sphere.sdk.expansion.MetaModelExpansionDslExpansionModelRead;
import io.sphere.sdk.http.HttpMethod;
import io.sphere.sdk.http.UrlQueryBuilder;
import io.sphere.sdk.models.ResourceView;
import io.sphere.sdk.models.Versioned;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import static io.sphere.sdk.json.SphereJsonUtils.toJsonString;
import static io.sphere.sdk.utils.ListUtils.listOf;
import static java.util.Objects.requireNonNull;

/**
 Internal base class to implement commands that change one entity in SPHERE.IO.
 @param <T> the type of the result of the command
 @param <C> class which will serialized as JSON command body, most likely a template
 @param <E> type of the expansion model */
public class MetaModelUpdateCommandDslImpl<T extends ResourceView<T, T>, C extends UpdateCommandDsl<T, C>, E> extends CommandImpl<T> implements UpdateCommandDsl<T, C>, MetaModelExpansionDslExpansionModelRead<T, C, E> {
    final Versioned<T> versioned;
    final List<? extends UpdateAction<T>> updateActions;
    final TypeReference<T> typeReference;
    final String baseEndpointWithoutId;
    final Function<MetaModelUpdateCommandDslBuilder<T, C, E>, C> creationFunction;
    final E expansionModel;
    final List<ExpansionPath<T>> expansionPaths;

    private MetaModelUpdateCommandDslImpl(final Versioned<T> versioned,
                                          final List<? extends UpdateAction<T>> updateActions,
                                          final TypeReference<T> typeReference,
                                          final String baseEndpointWithoutId,
                                          final Function<MetaModelUpdateCommandDslBuilder<T, C, E>, C> creationFunction,
                                          final E expansionModel,
                                          final List<ExpansionPath<T>> expansionPaths) {
        this.expansionModel = requireNonNull(expansionModel);
        this.expansionPaths = requireNonNull(expansionPaths);
        this.creationFunction = requireNonNull(creationFunction);
        this.versioned = requireNonNull(versioned);
        this.updateActions = requireNonNull(updateActions);
        this.typeReference = requireNonNull(typeReference);
        this.baseEndpointWithoutId = requireNonNull(baseEndpointWithoutId);
    }

    protected MetaModelUpdateCommandDslImpl(final Versioned<T> versioned,
                                            final List<? extends UpdateAction<T>> updateActions,
                                            final JsonEndpoint<T> endpoint,
                                            final Function<MetaModelUpdateCommandDslBuilder<T, C, E>, C> creationFunction,
                                            final E expansionModel) {
        this(versioned, updateActions, endpoint.typeReference(), endpoint.endpoint(), creationFunction, expansionModel, Collections.<ExpansionPath<T>>emptyList());
    }

    protected MetaModelUpdateCommandDslImpl(final MetaModelUpdateCommandDslBuilder<T, C, E> builder) {
        this(builder.getVersioned(), builder.getUpdateActions(), builder.getTypeReference(), builder.getBaseEndpointWithoutId(), builder.getCreationFunction(), builder.expansionModel, builder.expansionPaths);
    }

    @Override
    protected TypeReference<T> typeReference() {
        return typeReference;
    }

    @Override
    public HttpRequestIntent httpRequestIntent() {
        if (!baseEndpointWithoutId.startsWith("/")) {
            throw new RuntimeException("By convention the paths start with a slash, see baseEndpointWithoutId()");
        }
        final String additions = queryParametersToString(true);
        final String path = baseEndpointWithoutId + "/" + getVersioned().getId() + (additions.length() > 1 ? additions : "");
        return HttpRequestIntent.of(HttpMethod.POST, path, toJsonString(new UpdateCommandBody<>(getVersioned().getVersion(), getUpdateActions())));
    }

    private String queryParametersToString(final boolean urlEncoded) {
        final UrlQueryBuilder builder = UrlQueryBuilder.of();
        expansionPaths().forEach(path -> builder.add("expand", path.toSphereExpand(), urlEncoded));
        return builder.toStringWithOptionalQuestionMark();
    }

    @Override
    public C withVersion(final Versioned<T> newVersioned) {
        return copyBuilder().versioned(newVersioned).build();
    }

    public Versioned<T> getVersioned() {
        return versioned;
    }

    public List<? extends UpdateAction<T>> getUpdateActions() {
        return updateActions;
    }

    protected MetaModelUpdateCommandDslBuilder<T, C, E> copyBuilder() {
        return new MetaModelUpdateCommandDslBuilder<>(this);
    }

    String getBaseEndpointWithoutId() {
        return baseEndpointWithoutId;
    }

    Function<MetaModelUpdateCommandDslBuilder<T, C, E>, C> getCreationFunction() {
        return creationFunction;
    }

    TypeReference<T> getTypeReference() {
        return typeReference;
    }

    @Override
    public List<ExpansionPath<T>> expansionPaths() {
        return expansionPaths;
    }

    @Override
    public final C withExpansionPaths(final List<ExpansionPath<T>> expansionPaths) {
        return copyBuilder().expansionPaths(expansionPaths).build();
    }

    @Override
    public C withExpansionPaths(final ExpansionPath<T> expansionPath) {
        return ExpansionDslUtil.withExpansionPaths(this, expansionPath);
    }

    @Override
    public C withExpansionPaths(final Function<E, ExpansionPath<T>> m) {
        return ExpansionDslUtil.withExpansionPaths(this, m);
    }

    @Override
    public C plusExpansionPaths(final List<ExpansionPath<T>> expansionPaths) {
        return withExpansionPaths(listOf(expansionPaths(), expansionPaths));
    }

    @Override
    public C plusExpansionPaths(final ExpansionPath<T> expansionPath) {
        return ExpansionDslUtil.plusExpansionPaths(this, expansionPath);
    }

    @Override
    public C plusExpansionPaths(final Function<E, ExpansionPath<T>> m) {
        return ExpansionDslUtil.plusExpansionPaths(this, m);
    }

    @Override
    public E expansionModel() {
        return expansionModel;
    }
}
