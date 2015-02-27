package io.sphere.sdk.client;

import io.sphere.sdk.models.Base;

import java.util.concurrent.CompletableFuture;

public class SphereClientDecorator extends Base implements SphereClient {
    private final SphereClient delegate;

    protected SphereClientDecorator(final SphereClient delegate) {
        this.delegate = delegate;
    }

    @Override
    public void close() {
        delegate.close();
    }

    @Override
    public <T> CompletableFuture<T> execute(final SphereRequest<T> sphereRequest) {
        return delegate.execute(sphereRequest);
    }
}
