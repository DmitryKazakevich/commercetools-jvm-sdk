package io.sphere.sdk.client

import com.fasterxml.jackson.core.`type`.TypeReference
import io.sphere.sdk.models.Versioned
import io.sphere.sdk.queries.FetchImpl
import io.sphere.sdk.http.{JsonEndpoint, HttpRequest, HttpMethod}
import org.scalatest._
import com.typesafe.config.ConfigFactory
import scala.util.Properties._
import scala.collection.JavaConversions._

class NingAsyncHttpClientIntegrationSpec extends WordSpec with ShouldMatchers {

  lazy val config = {
    for {
      core <- envOrNone("JVM_SDK_IT_SERVICE_URL")
      auth <- envOrNone("JVM_SDK_IT_AUTH_URL")
      key <- envOrNone("JVM_SDK_IT_PROJECT_KEY")
      id <- envOrNone("JVM_SDK_IT_CLIENT_ID")
      secret <- envOrNone("JVM_SDK_IT_CLIENT_SECRET")
    } yield {
      val map = Map(
        "sphere.core" -> core,
        "sphere.auth" -> auth,
        "sphere.project" -> key,
        "sphere.clientId" -> id,
        "sphere.clientSecret" -> secret
      )
      ConfigFactory.parseMap(map).withFallback(ConfigFactory.load())
    }
  }.get

  def withClient(client: NingAsyncHttpClient)(body: NingAsyncHttpClient => Unit) {
    try {
      body(client)
    } finally {
      client.close
    }
  }

  classOf[NingAsyncHttpClient].getName must {
    "authenticate" in {
      withClient(new NingAsyncHttpClient(config)) { client =>
        val httpResponse = client.execute(new FetchImpl[String](Versioned.of("not-present", 0), JsonEndpoint.of(new TypeReference[String] {}, "/categories")) {
        }).get()
        httpResponse.getStatusCode should be(404)
      }
    }
    "add the version in the user agent header" in {
      withClient(new NingAsyncHttpClient(config)) { client: NingAsyncHttpClient =>
        client.asNingRequest(HttpRequest.of(HttpMethod.GET, "/categories")).getHeaders.getFirstValue("User-Agent") should fullyMatch regex ("SPHERE.IO JVM SDK version 1\\.\\d{1,2}\\.\\d{1,2}(-M\\d{1,2})?(-SNAPSHOT)?")
      }
    }
  }
}
