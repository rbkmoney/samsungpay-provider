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

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {"server.rest_port=65432"}
)
public class PortResolverTest {

    @Value("http://localhost:${server.rest_port}")
    private String restUrl;

    @Value("/${server.rest_path_prefix}")
    private String restPathPrefix;

    @Test
    public void portValidTest() throws IOException {
        HttpGet httpGetTransaction = new HttpGet(restUrl + restPathPrefix + "/transaction");
        HttpGet httpGetHealth = new HttpGet(restUrl + "/actuator/health");
        HttpGet httpGetWrongAddress = new HttpGet(restUrl + "/wrong/address");

        assertEquals(HttpStatus.SC_METHOD_NOT_ALLOWED, getHttpClient().execute(httpGetTransaction).getStatusLine().getStatusCode());
        assertEquals(HttpStatus.SC_OK, getHttpClient().execute(httpGetHealth).getStatusLine().getStatusCode());
        assertEquals(HttpStatus.SC_NOT_FOUND, getHttpClient().execute(httpGetWrongAddress).getStatusLine().getStatusCode());
    }

    private CloseableHttpClient getHttpClient() {
        return HttpClientBuilder.create().build();
    }
}
