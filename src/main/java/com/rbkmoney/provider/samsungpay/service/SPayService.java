package com.rbkmoney.provider.samsungpay.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rbkmoney.provider.samsungpay.domain.CredentialsResponse;
import com.rbkmoney.provider.samsungpay.domain.PData3DS;
import com.rbkmoney.provider.samsungpay.domain.ResultStatus;
import com.rbkmoney.provider.samsungpay.store.SPKeyStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.spec.PKCS8EncodedKeySpec;

/**
 * Created by vpankrashkin on 03.07.18.
 */
public class SPayService {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final SPayClient sPayClient;
    private final SPKeyStore keyStore;
    private final ObjectMapper mapper = new ObjectMapper();

    public SPayService(SPayClient SPayClient, SPKeyStore keyStore) {
        this.sPayClient = SPayClient;
        this.keyStore = keyStore;
    }

    public String createTransaction(String reqBody) throws Exception {
        String respBody = sPayClient.requestTransaction(reqBody);
        ResultStatus status = mapper.readValue(respBody, ResultStatus.class);
        if (!"0".equals(status.code)) {
            log.warn("Unsuccessful SP response code:{}", status);
        }
        return respBody;
    }

    public PData3DS getCredentials(String serviceId, String refId) throws Exception {
        log.info("Get key for service: {}", serviceId);
        PKCS8EncodedKeySpec keySpec = keyStore.getKey(serviceId);
        if (keySpec == null) {
            log.error("Unknown service id: {}", serviceId);
            throw new SPException("Not found key for service: " + serviceId);
        }
        String respBody = sPayClient.requestCredentials(serviceId, refId);
        ResultStatus status = mapper.readValue(respBody, ResultStatus.class);
        if (!"0".equals(status.code)) {
            log.error("Unsuccessful SP response code:" + status, respBody);
            throw new SPException("Unsuccessful SP response code", respBody);
        }
        CredentialsResponse credResp = mapper.readValue(respBody, CredentialsResponse.class);
        //optionally, add response validation
        String credentials = Decryptor.getDecryptedData(credResp.data3DS.data, keySpec);
        log.info("Payment credentials decrypted");
        return mapper.readValue(credentials, PData3DS.class);

    }

}
