package com.noqapp.common.utils;

import org.springframework.util.Assert;

import java.util.regex.Pattern;

import static com.noqapp.common.utils.CommonUtil.CODE_QR_PREFIX;

/**
 * User: hitender
 * Date: 11/24/16 3:37 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
public class Validate {

    private static Pattern objectIdPattern = Pattern.compile("^[0-9a-fA-F]{24}$");
    private static Pattern mailPattern = Pattern.compile("^[^@]+@[^@]+\\.[^@]+$");
    private static Pattern otherMailPattern = Pattern.compile("^[_a-z0-9-]+(\\.[_a-z0-9-]+)*@[a-z0-9-]+(\\.[a-z0-9-]+)*(\\.[a-z]{2,4})$");
    private static Pattern namePattern = Pattern.compile("^[\\p{L} .'-]+$");
    private static Pattern qidPattern = Pattern.compile("^1[0-9]{11}$");

    private Validate() {
    }

    public static boolean isValidObjectId(String text) {
        Assert.hasText(text, "Not a valid text");
        if (text.startsWith(CODE_QR_PREFIX)) {
            return objectIdPattern.matcher(text.replace(CODE_QR_PREFIX, "")).matches();
        }
        return objectIdPattern.matcher(text).matches();
    }

    public static boolean isValidMail(String text) {
        Assert.hasText(text, "Not a valid text");
        return mailPattern.matcher(text).matches();
    }

    public static boolean isValidName(String text) {
        Assert.hasText(text, "Not a valid text");
        return namePattern.matcher(text).matches();
    }

    public static boolean isValidQid(String text) {
        Assert.hasText(text, "Not a valid text");
        return qidPattern.matcher(text).matches();
    }

    public static boolean isValidPhoneWithInternationalCode(String text) {
        Assert.hasText(text, "Not a valid text");
        return text.startsWith("+");
    }
}
