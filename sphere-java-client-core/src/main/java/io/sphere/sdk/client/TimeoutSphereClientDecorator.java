package io.sphere.sdk.client;

import io.sphere.sdk.utils.CompletableFutureUtils;

import java.util.concurrent.*;

/**
 * Wraps a {@link SphereClient} to add timeouts. There are no guarantees that the timeout will be after the exact duration.
 */
public class TimeoutSphereClientDecorator extends SphereClientDecorator implements SphereClient {
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final long delay;
    private final TimeUnit timeUnit;

    private TimeoutSphereClientDecorator(final SphereClient delegate, final long delay, final TimeUnit timeUnit) {
        super(delegate);
        this.delay = delay;
        this.timeUnit = timeUnit;
    }

    public static SphereClient of(final SphereClient delegate, final long delay, final TimeUnit timeUnit) {
        return new TimeoutSphereClientDecorator(delegate, delay, timeUnit);
    }

    @Override
    public <T> CompletionStage<T> execute(final SphereRequest<T> sphereRequest) {
        final CompletableFuture<T> result = new CompletableFuture<>();
        final CompletionStage<T> prevResult = super.execute(sphereRequest);
        CompletableFutureUtils.transferResult(prevResult, result);
        scheduler.schedule(() -> result.completeExceptionally(new TimeoutException()), delay, timeUnit);
        return result;
    }

    @Override
    public void close() {
        scheduler.shutdown();
        super.close();
    }
}
