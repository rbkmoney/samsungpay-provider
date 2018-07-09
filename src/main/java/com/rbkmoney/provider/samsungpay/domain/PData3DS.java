package com.rbkmoney.provider.samsungpay.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

/**
 * Created by vpankrashkin on 05.07.18.
 */
public class PData3DS {
    public long amount;
    public String currencyCode;
    public String created;
    public String eci;
    public String dpan;
    public LocalDate expirationDate;
    public String cryptogram;
    public String cardholder;

    public PData3DS(
            @JsonProperty(value = "amount", required = true) long amount,
            @JsonProperty(value = "currency_code", required = true) String currencyCode,
            @JsonProperty(value = "utc", required = true) String created,
            @JsonProperty(value = "eci_indicator") String eci,
            @JsonProperty(value = "tokenPAN", required = true) String dpan,
            @JsonProperty(value = "tokenPanExpiration", required = true)
            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyMMdd")
                    LocalDate expirationDate,
            @JsonProperty(value = "cryptogram", required = true) String cryptogram,
            @JsonProperty(value = "cardholder_name") String cardholder) {
        this.amount = amount;
        this.currencyCode = currencyCode;
        this.created = created;
        this.eci = eci;
        this.dpan = dpan;
        this.expirationDate = expirationDate;
        this.cryptogram = cryptogram;
        this.cardholder = cardholder;
    }
}
