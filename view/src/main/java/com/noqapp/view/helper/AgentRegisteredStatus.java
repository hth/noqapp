package com.noqapp.view.helper;

/**
 * Checks agents registered status.
 * hitender
 * 2018-12-25 07:44
 */
public class AgentRegisteredStatus {
    private static final AgentRegisteredStatus AVAILABLE_INSTANCE = new AgentRegisteredStatus(true, new String[0]);
    private boolean available;
    private String[] recommendation;

    private AgentRegisteredStatus(boolean available, String[] recommendation) {
        this.available = available;
        this.recommendation = recommendation;
    }

    public static AgentRegisteredStatus available() {
        return AVAILABLE_INSTANCE;
    }

    public static AgentRegisteredStatus notAvailable(String name) {
        String[] recommendation = new String[]{"Click on '<b>INVITE REGISTERED USER</b>' below."};
        return new AgentRegisteredStatus(false, recommendation);
    }

    public boolean isAvailable() {
        return available;
    }

    public String[] getRecommendation() {
        return recommendation;
    }
}
