package com.github.meshuga;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import io.vertx.rxjava.core.RxHelper;
import io.vertx.rxjava.core.Vertx;
import io.vertx.rxjava.core.buffer.Buffer;
import io.vertx.rxjava.core.http.HttpClient;
import io.vertx.rxjava.core.http.HttpClientResponse;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import rx.Observable;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class RxHttpBugTest {
    @Rule
    public WireMockRule wireMockRule = new WireMockRule();

    Vertx vertx;

    @Before
    public void setUp() throws Exception {
        vertx = Vertx.vertx();
    }

    @Test
    public void onExampleUsingHelperShouldWorkProperly() throws Exception {
        // GIVEN
        String serverResponse = "Hello world!";
        stubFor(get(urlEqualTo("/"))
                .willReturn(aResponse()
                        .withBody(serverResponse)));

        HttpClient httpClient = vertx.createHttpClient();

        // WHEN
        Observable<HttpClientResponse> request = RxHelper.get(httpClient, 8080, "localhost", "/");

        Buffer bufferedResponse = request.flatMap(HttpClientResponse::toObservable)
                .reduce(Buffer.buffer(), Buffer::appendBuffer)
                .toBlocking().single();

        // THEN
        assertThat(bufferedResponse.toString()).isEqualTo(serverResponse);
    }
}
