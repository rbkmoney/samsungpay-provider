package com.rbkmoney.provider.samsungpay;

import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

import static com.rbkmoney.provider.samsungpay.config.ApplicationConfig.HEALTH;
import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {"server.rest_port=65434"}
)
public class ExternalPortRestrictingTest {

    private static final String FAKE_REST_PATH = "/you-not-found";
    private static final String MAPPED_REST_ENDPATH = "transaction";

    @Value("http://localhost:${server.rest_port}")
    private String restUrl;

    @Value("/${server.rest_path_prefix}/")
    private String restPath;

    @Test
    public void test() throws IOException {
        HttpGet httpGetTransaction = new HttpGet(restUrl + restPath + MAPPED_REST_ENDPATH);
        HttpGet httpGetHealth = new HttpGet(restUrl + HEALTH);
        HttpGet httpGetWrongAddress = new HttpGet(restUrl + FAKE_REST_PATH);

        assertEquals(HttpStatus.SC_METHOD_NOT_ALLOWED, getHttpClient().execute(httpGetTransaction).getStatusLine().getStatusCode());
        assertEquals(HttpStatus.SC_OK, getHttpClient().execute(httpGetHealth).getStatusLine().getStatusCode());
        assertEquals(HttpStatus.SC_NOT_FOUND, getHttpClient().execute(httpGetWrongAddress).getStatusLine().getStatusCode());
    }

    private CloseableHttpClient getHttpClient() {
        return HttpClientBuilder.create().build();
    }
}
