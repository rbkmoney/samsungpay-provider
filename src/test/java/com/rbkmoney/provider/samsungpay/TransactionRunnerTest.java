package com.rbkmoney.provider.samsungpay;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {"samsung.endpoint=api-ops.stg.mpay.samsung.com"}
)

public class TransactionRunnerTest {

    @Value("http://localhost:${server.rest.port}/${server.rest.endpoint}/transaction")
    private String transactionUrl;

    @Value("classpath:transaction_req.json")
    private org.springframework.core.io.Resource resource;

    private RestTemplate restTemplate = new RestTemplate();

    @Test
    public void testRequestTransaction() throws IOException {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON_UTF8);

            //headers.add("X-Request-Id", System.currentTimeMillis()+"");

            HttpEntity<String> request = new HttpEntity<>(IOUtils.toString(resource.getInputStream(), StandardCharsets.UTF_8), headers);

            ResponseEntity<String> response = restTemplate.postForEntity(transactionUrl, request, String.class);
            assertEquals(HttpStatus.OK, response.getStatusCode());
        } catch (HttpClientErrorException e) {
            System.out.println(e.getResponseBodyAsString());
            throw e;
        }
    }
}
