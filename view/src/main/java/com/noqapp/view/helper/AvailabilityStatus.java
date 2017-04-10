package com.noqapp.view.helper;

/**
 * User: hitender
 * Date: 11/25/16 11:39 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
public final class AvailabilityStatus {
    private static final AvailabilityStatus AVAILABLE_INSTANCE = new AvailabilityStatus(true, new String[0]);
    private boolean available;
    private String[] suggestions;

//    public static AvailabilityStatus notAvailable(String name) {
//        String[] suggestions = new String[3];
//        for (int i = 0; i < suggestions.length; i++) {
//            suggestions[i] = name + (i + 1);
//        }
//        return new AvailabilityStatus(false, suggestions);
//    }

    private AvailabilityStatus(boolean available, String[] suggestions) {
        this.available = available;
        this.suggestions = suggestions;
    }

    // internal

    public static AvailabilityStatus available() {
        return AVAILABLE_INSTANCE;
    }

    //TODO find a valid use for this. It was meant to suggest different email address for registration
    public static AvailabilityStatus notAvailable(String name) {
        String[] suggestions = new String[]{"Click on '<b>Recover Password</b>' below if you have lost password."};
        return new AvailabilityStatus(false, suggestions);
    }

    public boolean isAvailable() {
        return available;
    }

    public String[] getSuggestions() {
        return suggestions;
    }
}
