package com.noqapp.domain.common;

import com.noqapp.domain.types.PaymentModeEnum;
import com.noqapp.domain.types.cashfree.PaymentModeCFEnum;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User: hitender
 * Date: 2019-04-09 15:14
 */
public class DomainCommonUtil {
    private static final Logger LOG = LoggerFactory.getLogger(DomainCommonUtil.class);

    public static PaymentModeEnum derivePaymentMode(String paymentModeOfCashfree) {
        PaymentModeEnum paymentMode;
        switch (PaymentModeCFEnum.valueOf(paymentModeOfCashfree)) {
            case DEBIT_CARD:
                paymentMode = PaymentModeEnum.DC;
                break;
            case CREDIT_CARD:
                paymentMode = PaymentModeEnum.CC;
                break;
            case CREDIT_CARD_EMI:
                paymentMode = PaymentModeEnum.CCE;
                break;
            case NET_BANKING:
                paymentMode = PaymentModeEnum.NTB;
                break;
            case UPI:
                paymentMode = PaymentModeEnum.UPI;
                break;
            case Paypal:
                paymentMode = PaymentModeEnum.PAL;
                break;
            case PhonePe:
                paymentMode = PaymentModeEnum.PPE;
                break;
            case Paytm:
                paymentMode = PaymentModeEnum.PTM;
                break;
            case AmazonPay:
                paymentMode = PaymentModeEnum.AMZ;
                break;
            case AIRTEL_MONEY:
                paymentMode = PaymentModeEnum.AIR;
                break;
            case FreeCharge:
                paymentMode = PaymentModeEnum.FCH;
                break;
            case MobiKwik:
                paymentMode = PaymentModeEnum.MKK;
                break;
            case OLA:
                paymentMode = PaymentModeEnum.OLA;
                break;
            case JioMoney:
                paymentMode = PaymentModeEnum.JIO;
                break;
            case ZestMoney:
                paymentMode = PaymentModeEnum.ZST;
                break;
            case Instacred:
                paymentMode = PaymentModeEnum.INS;
                break;
            case LazyPay:
                paymentMode = PaymentModeEnum.LPY;
                break;
            default:
                LOG.error("Unknown field {}", paymentModeOfCashfree);
                throw new UnsupportedOperationException("Reached unsupported payment mode");
        }
        return paymentMode;
    }
}
