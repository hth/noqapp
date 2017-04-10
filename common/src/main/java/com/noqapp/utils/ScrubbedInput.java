package com.noqapp.utils;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

/**
 * User: hitender
 * Date: 11/18/16 6:26 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
public class ScrubbedInput implements Serializable {
    private static final long serialVersionUID = -4941918823207463880L;
    public static final String UTF_8 = "UTF-8";

    protected String text;

    @SuppressWarnings ("unused")
    private ScrubbedInput() {
    }

    @SuppressWarnings ("unused")
    public ScrubbedInput(String text) {
        this.text = StringUtils.trim(TextInputScrubber.sanitize(TextInputScrubber.decode(text)));
    }

    @SuppressWarnings ("unused")
    public ScrubbedInput(Integer text) {
        this.text = Integer.toString(text);
    }

    public String getText() {
        return text == null ? "" : text;
    }

    @Override
    public String toString() {
        return text;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ScrubbedInput that = (ScrubbedInput) o;
        return !(text != null ? !text.equals(that.text) : that.text != null);
    }

    @Override
    public int hashCode() {
        return text != null ? text.hashCode() : 0;
    }
}