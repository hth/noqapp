package com.noqapp.view.form;

import com.noqapp.common.utils.ScrubbedInput;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * hitender
 * 2/23/20 1:26 PM
 */
@SuppressWarnings({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
public class ChatConversationForm {

    private ScrubbedInput conversation;

    private List<String> chatMessages = new LinkedList<>();
    private List<String> chatNouns = new LinkedList<>();

    private String chatMessagesAsString;
    private String chatNounsAsString;

    public ScrubbedInput getConversation() {
        return conversation;
    }

    public List<String> getChatMessages() {
        return chatMessages;
    }

    public ChatConversationForm setChatMessages(List<String> chatMessages) {
        this.chatMessages = chatMessages;
        return this;
    }

    public ChatConversationForm addChatMessage() {
        if (StringUtils.isNotBlank(conversation.getText())) {
            this.chatMessages.add(conversation.getText());
        }
        return this;
    }

    public ChatConversationForm setConversation(ScrubbedInput conversation) {
        this.conversation = conversation;
        return this;
    }

    public List<String> getChatNouns() {
        return chatNouns;
    }

    public ChatConversationForm setChatNouns(List<String> chatNouns) {
        this.chatNouns = chatNouns;
        return this;
    }

    public String getChatMessagesAsString() {
        if (chatMessages.size() > 0) {
            chatMessagesAsString = StringUtils.join(chatMessages, "|");
        }
        return chatMessagesAsString;
    }

    public ChatConversationForm setChatMessagesAsString(String chatMessagesAsString) {
        this.chatMessagesAsString = chatMessagesAsString;
        return this;
    }

    public String getChatNounsAsString() {
        if (chatNouns.size() > 0) {
            this.chatNounsAsString = StringUtils.join(chatNouns, "|");
        }
        return this.chatNounsAsString;
    }

    public ChatConversationForm setChatNounsAsString(String chatNounsAsString) {
        this.chatNounsAsString = chatNounsAsString;
        return this;
    }

    public void repopulate() {
        String[] a = chatMessagesAsString.split("\\|");
        this.getChatMessages().addAll(Arrays.asList(a));

        a = chatNounsAsString.split("\\|");
        this.getChatNouns().addAll(Arrays.asList(a));
    }
}
