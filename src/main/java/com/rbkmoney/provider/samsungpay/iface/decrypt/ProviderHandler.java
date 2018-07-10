package com.rbkmoney.provider.samsungpay.iface.decrypt;

import com.rbkmoney.damsel.base.InvalidRequest;
import com.rbkmoney.damsel.payment_tool_provider.PaymentToolProviderSrv;
import com.rbkmoney.damsel.payment_tool_provider.UnwrappedPaymentTool;
import com.rbkmoney.damsel.payment_tool_provider.WrappedPaymentTool;
import com.rbkmoney.provider.samsungpay.domain.PData3DS;
import com.rbkmoney.provider.samsungpay.service.SPException;
import com.rbkmoney.provider.samsungpay.service.SPayService;
import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;

/**
 * Created by vpankrashkin on 04.07.18.
 */
public class ProviderHandler implements PaymentToolProviderSrv.Iface {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final SPayService service;

    public ProviderHandler(SPayService service) {
        this.service = service;
    }

    @Override
    public UnwrappedPaymentTool unwrap(WrappedPaymentTool payment_tool) throws InvalidRequest, TException {
        log.info("New unwrap request: {}", payment_tool);
        if (!payment_tool.getRequest().isSetSamsung()) {
            throw new InvalidRequest(Arrays.asList("Received request type is not SamsungPay"));
        }
        String refId = payment_tool.getRequest().getSamsung().getReferenceId();
        String srvId = payment_tool.getRequest().getSamsung().getServiceId();
        try {
            PData3DS pData = service.getCredentials(srvId, refId);
            log.info("Payment data decrypted: {}", pData);

            UnwrappedPaymentTool result = new UnwrappedPaymentTool();
            /*result.setCardInfo(extractCardInfo(paymentData.getCardInfo()));
            result.setPaymentData(extractPaymentData(decryptedMessage));
            result.setDetails(extractPaymentDetails(decryptedMessage));*/
            return result;
        } catch (IOException e) {
            //log.error("Failed to read json data: {}", filterPan(e.getMessage()));
            throw new InvalidRequest(Arrays.asList("Failed to read json data"));
        } catch (Exception e) {
            log.error("Failed to get credentials", e);
            throw new InvalidRequest(Arrays.asList(e.getMessage()));
        }
    }
}
