package com.noqapp.common.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.Random;

/**
 * User: hitender
 * Date: 11/18/16 6:36 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
public final class RandomString {

    private static final int CHARACTER_SIZE = 32;
    private static final char[] SYMBOLS = new char[36];
    private static final String QID_SHORTEN = "^10+(?!$)";
    private static final String LAST_THREE_DIGITS = "(\\d+)(?=\\d{3}(?:,|$))";

    static {
        for (int idx = 0; idx < 10; ++idx) {
            SYMBOLS[idx] = (char) ('0' + idx);
        }

        for (int idx = 10; idx < 36; ++idx) {
            SYMBOLS[idx] = (char) ('a' + idx - 10);
        }
    }

    private final Random random = new Random();
    private final char[] buf;

    private RandomString(int length) {
        if (length < 1) {
            throw new IllegalArgumentException("length < 1: " + length);
        }
        buf = new char[length];
    }

    public static RandomString newInstance() {
        return new RandomString(CHARACTER_SIZE);
    }

    public static RandomString newInstance(int sizeOfString) {
        return new RandomString(sizeOfString);
    }

    public String nextString() {
        for (int idx = 0; idx < buf.length; ++idx) {
            buf[idx] = SYMBOLS[random.nextInt(SYMBOLS.length)];
        }
        return new String(buf);
    }

    private static String generateEmailAddress(ScrubbedInput firstName, ScrubbedInput lastName, String qid) {
        String shortenedQid = qid.replaceFirst(QID_SHORTEN, "");

        if (StringUtils.isNotBlank(firstName.getText()) && StringUtils.isNotBlank(lastName.getText())) {
            return StringUtils.lowerCase(firstName.getText()) + "." + StringUtils.lowerCase(lastName.getText()) + "." + shortenedQid;
        } else if (StringUtils.isNotBlank(firstName.getText())) {
            return StringUtils.lowerCase(firstName.getText()) + "." + shortenedQid;
        } else if (StringUtils.isNotBlank(lastName.getText())) {
            return StringUtils.lowerCase(lastName.getText()) + "." + shortenedQid;
        } else {
            return StringUtils.lowerCase(RandomString.newInstance(6).nextString()) + "." + shortenedQid;
        }
    }

    public static String generateEmailAddressWithDomain(ScrubbedInput firstName, ScrubbedInput lastName, String qid) {
        return generateEmailAddress(firstName, lastName, qid) + "@mail.noqapp.com";
    }

    public static String generateInviteCode(String firstName, String lastName, String qid) {
        if (StringUtils.isBlank(lastName)) {
            return StringUtils.lowerCase(firstName + qid.replaceFirst(LAST_THREE_DIGITS, "") + newInstance(1).nextString());
        }
        return StringUtils.lowerCase(firstName + lastName + qid.replaceFirst(LAST_THREE_DIGITS, "") + newInstance(1).nextString());
    }
}
