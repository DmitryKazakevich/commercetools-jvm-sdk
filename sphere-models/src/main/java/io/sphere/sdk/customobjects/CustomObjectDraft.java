package io.sphere.sdk.customobjects;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import io.sphere.sdk.json.SphereJsonUtils;
import io.sphere.sdk.json.TypeReferences;
import io.sphere.sdk.models.Base;

import javax.annotation.Nullable;

/**
 * A draft for creating or updating custom objects.
 * @param <T> The type of the value of the custom object.
 */
public class CustomObjectDraft<T> extends Base {

    private final T value;
    @Nullable
    private final Long version;
    private final JavaType javaType;//CustomObject<T>
    private final String container;
    private final String key;

    private CustomObjectDraft(final String container, final String key, final T value, @Nullable final Long version, final Class<T> valueClass) {
        this(container, key, value, version, SphereJsonUtils.convertToJavaType(valueClass));
    }

    private CustomObjectDraft(final String container, final String key, final T value, @Nullable final Long version, final TypeReference<T> valueTypeReference) {
        this(container, key, value, version, SphereJsonUtils.convertToJavaType(valueTypeReference));
    }

    private CustomObjectDraft(final String container, final String key, final T value, @Nullable final Long version, final JavaType valueJavaType) {
        this.container = CustomObject.validatedContainer(container);
        this.key = CustomObject.validatedKey(key);
        this.value = value;
        this.version = version;
        this.javaType = CustomObjectUtils.getCustomObjectJavaTypeForValue(valueJavaType);
    }

    public T getValue() {
        return value;
    }

    /**
     * Creates a draft for updating a custom object. Optimistic concurrency control is used.
     * @param customObject the custom object to override (provides key, container and version)
     * @param newValue the value which should be assigned to the custom object
     * @param valueTypeReference the type reference to deserialize the updated custom object from the SPHERE.IO response
     * @param <T>  The type of the value of the custom object.
     * @return the draft
     */
    public static <T> CustomObjectDraft<T> ofVersionedUpdate(final CustomObject<T> customObject, final T newValue, final TypeReference<T> valueTypeReference) {
        return new CustomObjectDraft<>(customObject.getContainer(), customObject.getKey(), newValue, customObject.getVersion(), valueTypeReference);
    }

    /**
     * Creates a draft for updating a custom object. Optimistic concurrency control is used.
     * @param customObject the custom object to override (provides key, container and version)
     * @param newValue the value which should be assigned to the custom object
     * @param valueClass the class of the value, if it not uses generics like lists, typically for POJOs
     * @param <T>  The type of the value of the custom object.
     * @return the draft
     */
    public static <T> CustomObjectDraft<T> ofVersionedUpdate(final CustomObject<T> customObject, final T newValue, final Class<T> valueClass) {
        return new CustomObjectDraft<>(customObject.getContainer(), customObject.getKey(), newValue, customObject.getVersion(), valueClass);
    }

    /**
     * Creates a draft for updating a custom object. Does not use optimistic concurrency control so the last update wins.
     * @param customObject the custom object to override (provides key, container and version)
     * @param newValue the value which should be assigned to the custom object
     * @param valueTypeReference the type reference to deserialize the updated custom object from the SPHERE.IO response
     * @param <T> The type of the value of the custom object.
     * @return the draft
     */
    public static <T> CustomObjectDraft<T> ofUnversionedUpdate(final CustomObject<T> customObject, final T newValue, final TypeReference<T> valueTypeReference) {
        return new CustomObjectDraft<>(customObject.getContainer(), customObject.getKey(), newValue, null, valueTypeReference);
    }

    /**
     * Creates a draft for updating a custom object. Does not use optimistic concurrency control so the last update wins.
     * @param customObject the custom object to override (provides key, container and version)
     * @param newValue the value which should be assigned to the custom object
     * @param valueClass the class of the value, if it not uses generics like lists, typically for POJOs
     * @param <T> The type of the value of the custom object.
     * @return the draft
     */
    public static <T> CustomObjectDraft<T> ofUnversionedUpdate(final CustomObject<T> customObject, final T newValue, final Class<T> valueClass) {
        return new CustomObjectDraft<>(customObject.getContainer(), customObject.getKey(), newValue, null, valueClass);
    }


    /**
     * Creates a draft for creating or updating a custom object. Does not use optimistic concurrency control so the last update wins.
     * @param container the container
     * @param key the key
     * @param value the value which should be assigned to the custom object
     * @param valueTypeReference the type reference to deserialize the updated custom object from the SPHERE.IO response
     * @param <T> The type of the value of the custom object.
     * @return the draft
     */
    public static <T> CustomObjectDraft<T> ofUnversionedUpsert(final String container, final String key, final T value, final TypeReference<T> valueTypeReference) {
        return new CustomObjectDraft<>(container, key, value, null, valueTypeReference);
    }

    /**
     * Creates a draft for creating or updating a custom object. Does not use optimistic concurrency control so the last update wins.
     * @param container the container
     * @param key the key
     * @param value the value which should be assigned to the custom object
     * @param valueClass the class of the value, if it not uses generics like lists, typically for POJOs
     * @param <T> The type of the value of the custom object.
     * @return the draft
     */
    public static <T> CustomObjectDraft<T> ofUnversionedUpsert(final String container, final String key, final T value, final Class<T> valueClass) {
        return new CustomObjectDraft<>(container, key, value, null, SphereJsonUtils.convertToJavaType(valueClass));
    }

    /**
     * Creates a draft for creating or updating a custom object. Optimistic concurrency control is used.
     * @param container the container
     * @param key the key
     * @param value the value which should be assigned to the custom object
     * @param version the version for optimistic locking
     * @param valueTypeReference the type reference to deserialize the updated custom object from the SPHERE.IO response
     * @param <T> The type of the value of the custom object.
     * @return the draft
     */
    public static <T> CustomObjectDraft<T> ofVersionedUpsert(final String container, final String key, final T value, final long version, final TypeReference<T> valueTypeReference) {
        return new CustomObjectDraft<>(container, key, value, version, valueTypeReference);
    }

    /**
     * Creates a draft for creating or updating a custom object. Optimistic concurrency control is used.
     * @param container the container
     * @param key the key
     * @param value the value which should be assigned to the custom object
     * @param version the version for optimistic locking
     * @param valueClass the class of the value, if it not uses generics like lists, typically for POJOs
     * @param <T> The type of the value of the custom object.
     * @return the draft
     */
    public static <T> CustomObjectDraft<T> ofVersionedUpsert(final String container, final String key, final T value, final long version, final Class<T> valueClass) {
        return new CustomObjectDraft<>(container, key, value, version, valueClass);
    }

    /**
     * Creates a draft for creating or updating a custom object. Does not use optimistic concurrency control so the last update wins.
     *
     * It is an alias of
     * {@link CustomObjectDraft#CustomObjectDraft(String, String, Object, Long, com.fasterxml.jackson.core.type.TypeReference)}
     * with pure JSON as value for the custom object.
     *
     * @param container the container
     * @param key the key
     * @param value the value which should be assigned to the custom object
     * @return the draft
     */
    public static CustomObjectDraft<JsonNode> ofUnversionedUpsert(final String container, final String key, final JsonNode value) {
        return ofUnversionedUpsert(container, key, value, TypeReferences.jsonNodeTypeReference());
    }

    @Nullable
    public Long getVersion() {
        return version;
    }

    /**
     * Copies this {@link io.sphere.sdk.customobjects.CustomObjectDraft} and sets the version to {@code version} to use optimistic locking.
     *
     * @param version the version of the current stored custom object in SPHERE.IO.
     * @return a draft which provides optimistic locking
     */
    public CustomObjectDraft<T> withVersion(final Long version) {
        return new CustomObjectDraft<>(getContainer(), getKey(), getValue(), version, javaType);
    }

    public String getContainer() {
        return container;
    }

    public String getKey() {
        return key;
    }

    public JavaType getJavaType() {
        return javaType;
    }
}
