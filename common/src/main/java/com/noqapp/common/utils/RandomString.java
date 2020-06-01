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
    private static final String randomString = "123456789ABCDEFGHJKLMNPQRSTUVWXYZ";
    private static final char[] SYMBOLS = new char[33];
    private static final String QID_SHORTEN = "^10+(?!$)";
    private static final String LAST_THREE_DIGITS = "(\\d+)(?=\\d{3}(?:,|$))";

    /* No mail is sent out with this domain. */
    public static final String MAIL_NOQAPP_COM = "@mail.noqapp.com";

    /* Mail is sent out with this domain. Though its not delivered to any one other than admin of the store. */
    public static final String MANAGER_NOQAPP_COM = "@m.noqapp.com";

    static {
        for (int idx = 0; idx < 33; ++idx) {
            SYMBOLS[idx] = randomString.charAt(idx);
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
            return StringUtils.lowerCase(firstName.getText()).replaceAll("\\s+", ".")
                + "."
                + StringUtils.lowerCase(lastName.getText()).replaceAll("\\s+", ".")
                + "."
                + shortenedQid;
        } else if (StringUtils.isNotBlank(firstName.getText())) {
            return StringUtils.lowerCase(firstName.getText()).replaceAll("\\s+", ".")
                + "."
                + shortenedQid;
        } else if (StringUtils.isNotBlank(lastName.getText())) {
            return StringUtils.lowerCase(lastName.getText()).replaceAll("\\s+", ".")
                + "."
                + shortenedQid;
        } else {
            return StringUtils.lowerCase(RandomString.newInstance(6).nextString())
                + "."
                + shortenedQid;
        }
    }

    public static String generateEmailAddressWithDomain(ScrubbedInput firstName, ScrubbedInput lastName, String qid) {
        return generateEmailAddress(firstName, lastName, qid) + MAIL_NOQAPP_COM;
    }

    public static String generateManagerEmailAddressWithDomain(ScrubbedInput firstName, ScrubbedInput lastName, String qid) {
        return generateEmailAddress(firstName, lastName, qid) + MANAGER_NOQAPP_COM;
    }

    public static String generateInviteCode(String firstName, String lastName, String qid) {
        String fName = firstName.replaceAll("\\s+", "");
        if (StringUtils.isBlank(lastName)) {
            return StringUtils.lowerCase(fName + qid.replaceFirst(LAST_THREE_DIGITS, "") + newInstance(1).nextString());
        }
        String lName = lastName.replaceAll("\\s+", "");
        if (fName.length() > 6 && lName.length() > 3) {
            return StringUtils.lowerCase(fName + lName.substring(0, 3) + qid.replaceFirst(LAST_THREE_DIGITS, "") + newInstance(1).nextString());
        } else {
            return StringUtils.lowerCase(fName + lastName.replaceAll("\\s+", "") + qid.replaceFirst(LAST_THREE_DIGITS, "") + newInstance(1).nextString());
        }
    }
}
