package com.rbkmoney.provider.samsungpay.iface.decrypt;

import com.rbkmoney.damsel.base.InvalidRequest;
import com.rbkmoney.damsel.payment_tool_provider.PaymentToolProviderSrv;
import com.rbkmoney.damsel.payment_tool_provider.UnwrappedPaymentTool;
import com.rbkmoney.damsel.payment_tool_provider.WrappedPaymentTool;
import org.apache.thrift.TException;

/**
 * Created by vpankrashkin on 04.07.18.
 */
public class ProviderHandler implements PaymentToolProviderSrv.Iface {
    @Override
    public UnwrappedPaymentTool unwrap(WrappedPaymentTool payment_tool) throws InvalidRequest, TException {
        return null;
    }
}
