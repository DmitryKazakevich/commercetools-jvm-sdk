package io.sphere.sdk.customobjects.commands;

import io.sphere.sdk.client.HttpRequestIntent;
import io.sphere.sdk.commands.CreateCommand;
import io.sphere.sdk.customobjects.CustomObject;
import io.sphere.sdk.client.SphereRequestBase;
import io.sphere.sdk.http.HttpMethod;
import io.sphere.sdk.http.HttpResponse;

/**
 Command for creating or updating a custom object using a custom JSON mapper.

 <p>Example using Google GSON (multiple code snippets):</p>

 <p>The execution of the command:</p>
 {@include.example io.sphere.sdk.customobjects.commands.CustomObjectCustomJsonMappingUpsertCommandTest#execution()}

 <p>The command class:</p>
 {@include.example io.sphere.sdk.customobjects.demo.GsonFooCustomObjectUpsertCommand}

 <p>The draft containing container, key and value:</p>
 {@include.example io.sphere.sdk.customobjects.demo.GsonFooCustomObjectDraft}

 <p>The resulting custom object class:</p>
 {@include.example io.sphere.sdk.customobjects.demo.GsonFooCustomObject}

 <p>The class of the value:</p>
 {@include.example io.sphere.sdk.customobjects.demo.GsonFoo}


 @param <T> The type of the value of this custom object.
 @see CustomObject
 */
public abstract class CustomObjectCustomJsonMappingUpsertCommand<T> extends SphereRequestBase implements CreateCommand<CustomObject<T>> {

    @Override
    public abstract CustomObject<T> deserialize(final HttpResponse httpResponse);

    @Override
    public final HttpRequestIntent httpRequestIntent() {
        return HttpRequestIntent.of(HttpMethod.POST, "/custom-objects", bodyAsJsonString());
    }

    /**
     * Produces JSON to create or update a custom object.
     * It must have the fields of a {@link io.sphere.sdk.customobjects.CustomObject}:
     *
     * Example:
     *
     * <pre>{@code
       {
            "container": "myNamespace",
            "key": "myKey",
            "value": {
                "baz": 3,
                "bar": "a String"
            }
        }
     * }</pre>
     *
     * @return JSON as String
     */
    protected abstract String bodyAsJsonString();
}
