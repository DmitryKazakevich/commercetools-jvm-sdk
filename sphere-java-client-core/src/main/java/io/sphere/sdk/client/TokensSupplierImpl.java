package io.sphere.sdk.client;

import com.fasterxml.jackson.databind.JsonNode;
import io.sphere.sdk.http.*;
import io.sphere.sdk.json.JsonException;
import io.sphere.sdk.json.JsonUtils;
import io.sphere.sdk.models.SphereException;
import io.sphere.sdk.utils.MapUtils;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

import static io.sphere.sdk.client.SphereAuth.AUTH_LOGGER;
import static io.sphere.sdk.http.HttpMethod.POST;
import static java.lang.String.format;

/**
 * Component that can fetch SPHERE.IO access tokens.
 * Does not refresh them,
 */
final class TokensSupplierImpl extends AutoCloseableService implements TokensSupplier {

    private final SphereAuthConfig config;
    private final HttpClient httpClient;
    private final boolean closeHttpClient;
    private boolean isClosed = false;

    private TokensSupplierImpl(final SphereAuthConfig config, final HttpClient httpClient, final boolean closeHttpClient) {
        this.config = config;
        this.httpClient = httpClient;
        this.closeHttpClient = closeHttpClient;
    }

    static TokensSupplier of(final SphereAuthConfig config, final HttpClient httpClient, final boolean closeHttpClient) {
        return new TokensSupplierImpl(config, httpClient, closeHttpClient);
    }

    /**
     * Executes a http auth sphere request and fetches a new access token.
     * @return future of a token
     */
    @Override
    public CompletionStage<Tokens> get() {
        AUTH_LOGGER.debug(() -> "Fetching new token.");
        final HttpRequest httpRequest = newRequest();
        final CompletionStage<HttpResponse> httpResponseStage = httpClient.execute(httpRequest);
        final CompletionStage<Tokens> result = httpResponseStage.thenApply((response) -> parseResponse(response, httpRequest));
        result.whenCompleteAsync(this::logTokenResult);
        return result;
    }

    private void logTokenResult(final Tokens nullableTokens, final Throwable nullableThrowable) {
        if (nullableTokens != null) {
            AUTH_LOGGER.debug(() -> "Successfully fetched token that expires in " + nullableTokens.getExpiresIn().map(x -> x.toString()).orElse("an unknown time") + ".");
        } else {
            AUTH_LOGGER.error(() -> "Failed to fetch token.", nullableThrowable);
        }
    }

    @Override
    protected synchronized void internalClose() {
        if (!isClosed) {
            if (closeHttpClient) {
                closeQuietly(httpClient);
            }
            isClosed = true;
        }
    }

    private HttpRequest newRequest() {
        final String usernamePassword = format("%s:%s", config.getClientId(), config.getClientSecret());
        final String encodedString = Base64.getEncoder().encodeToString(usernamePassword.getBytes(StandardCharsets.UTF_8));
        final HttpHeaders httpHeaders = HttpHeaders
                .of(HttpHeaders.AUTHORIZATION, "Basic " + encodedString)
                .plus(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded");
        final FormUrlEncodedHttpRequestBody body = FormUrlEncodedHttpRequestBody.of(MapUtils.mapOf("grant_type", "client_credentials", "scope", format("manage_project:%s", config.getProjectKey())));
        return HttpRequest.of(POST, config.getAuthUrl() + "/oauth/token", httpHeaders, Optional.of(body));
    }

    /** Parses Tokens from a response from the backend authorization service.
     * @param httpResponse Response from the authorization service.
     * @param httpRequest the request which belongs to the response
     */
    private Tokens parseResponse(final HttpResponse httpResponse, final HttpRequest httpRequest) {
        try {
            if (httpResponse.getStatusCode() == 401 && httpResponse.getResponseBody().isPresent()) {
                ClientErrorException exception = new UnauthorizedException(httpResponse.toString());
                try {
                    final JsonNode jsonNode = JsonUtils.readTree(httpResponse.getResponseBody().get());
                    final String error = jsonNode.get("error").asText();
                    if (error.equals("invalid_client")) {
                        exception = new InvalidClientCredentialsException(config);
                    }
                } catch (final JsonException e) {
                    exception = new UnauthorizedException(httpResponse.toString(), e);
                }
                throw exception;
            }
            return JsonUtils.readObject(Tokens.typeReference(), httpResponse.getResponseBody().get());
        } catch (final SphereException exception) {
            exception.setProjectKey(config.getProjectKey());
            exception.setUnderlyingHttpResponse(httpResponse);
            exception.setHttpRequest(httpRequest.toString());
            throw exception;
        }
    }
}
