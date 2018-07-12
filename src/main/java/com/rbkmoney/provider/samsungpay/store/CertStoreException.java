package com.rbkmoney.provider.samsungpay.store;

/**
 * Created by vpankrashkin on 10.04.18.
 */
public class CertStoreException extends RuntimeException {
    public CertStoreException() {
    }

    public CertStoreException(String message) {
        super(message);
    }

    public CertStoreException(String message, Throwable cause) {
        super(message, cause);
    }

    public CertStoreException(Throwable cause) {
        super(cause);
    }

    public CertStoreException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
