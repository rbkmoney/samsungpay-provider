package com.rbkmoney.provider.samsungpay.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rbkmoney.provider.samsungpay.domain.CredentialsResponse;
import com.rbkmoney.provider.samsungpay.domain.ResultStatus;
import com.rbkmoney.provider.samsungpay.store.SPKeyStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.security.spec.PKCS8EncodedKeySpec;

/**
 * Created by vpankrashkin on 03.07.18.
 */
public class TransactionService {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final TransactionClient transactionClient;
    private final SPKeyStore keyStore;
    private final ObjectMapper mapper = new ObjectMapper();

    public TransactionService(TransactionClient transactionClient, SPKeyStore keyStore) {
        this.transactionClient = transactionClient;
        this.keyStore = keyStore;
    }

    public String createTransaction(String reqBody) throws SPException {
        try {
            String respBody = transactionClient.requestTransaction(reqBody);
            ResultStatus status = mapper.readValue(respBody, ResultStatus.class);
            if (status.code != 0) {
                log.warn("Unsuccessful SP response code:"+status, respBody);
            }
            return respBody;
        } catch (IOException e) {
            throw new SPException(e);
        }
    }

    public String getCredentials(String serviceId, String refId) throws SPException {
        try {
            PKCS8EncodedKeySpec keySpec = keyStore.getKey(serviceId);
            if (keySpec == null) {
                log.error("Unknown service id: {}", serviceId);
                throw new SPException("Not found key for service: " + serviceId);
            }
            String respBody = transactionClient.requestCredentials(serviceId, refId);
            ResultStatus status = mapper.readValue(respBody, ResultStatus.class);
            if (status.code != 0) {
                log.error("Unsuccessful SP response code:"+status, respBody);
                throw new SPException("Unsuccessful SP response code", respBody);
            }
            CredentialsResponse credResp = mapper.readValue(respBody, CredentialsResponse.class);
            String credentials = Decryptor.getDecryptedData(credResp.data3DS.data, keySpec);
            return credentials;
        } catch (Exception e) {
            throw new SPException("Failed to get payment credentials", e);
        }
    }

}
