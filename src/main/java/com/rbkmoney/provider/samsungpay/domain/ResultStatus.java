package com.rbkmoney.provider.samsungpay.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by vpankrashkin on 05.07.18.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResultStatus {
    public int code;
    public String decription;

    @JsonCreator
    public ResultStatus(
            @JsonProperty(value = "resultCode", required = true) int code,
            @JsonProperty(value = "resultMessage", required = true) String decription) {
    }

    @Override
    public String toString() {
        return "ResultStatus{" +
                "code=" + code +
                ", decription='" + decription + '\'' +
                '}';
    }
}
