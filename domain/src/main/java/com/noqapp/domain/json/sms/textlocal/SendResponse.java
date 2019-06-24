package com.noqapp.domain.json.sms.textlocal;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;

/**
 * User: hitender
 * Date: 2019-06-24 00:36
 */
@SuppressWarnings({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable",
    "unused"
})
@JsonAutoDetect(
    fieldVisibility = JsonAutoDetect.Visibility.ANY,
    getterVisibility = JsonAutoDetect.Visibility.NONE,
    setterVisibility = JsonAutoDetect.Visibility.NONE
)
@JsonPropertyOrder(alphabetic = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SendResponse extends BaseMessage {

    @JsonProperty("balance")
    private int balance;

    @JsonProperty("batch_id")
    private String batchId;

    @JsonProperty("cost")
    private int cost;

    @JsonProperty("num_messages")
    private int numberOfMessage;

    @JsonProperty("message")
    private MessageSMS messageSMS;

    @JsonProperty("messages")
    private List<MessageSentTo> messageSentTo;

    public int getBalance() {
        return balance;
    }

    public SendResponse setBalance(int balance) {
        this.balance = balance;
        return this;
    }

    public String getBatchId() {
        return batchId;
    }

    public SendResponse setBatchId(String batchId) {
        this.batchId = batchId;
        return this;
    }

    public int getCost() {
        return cost;
    }

    public SendResponse setCost(int cost) {
        this.cost = cost;
        return this;
    }

    public int getNumberOfMessage() {
        return numberOfMessage;
    }

    public SendResponse setNumberOfMessage(int numberOfMessage) {
        this.numberOfMessage = numberOfMessage;
        return this;
    }

    public MessageSMS getMessageSMS() {
        return messageSMS;
    }

    public SendResponse setMessageSMS(MessageSMS messageSMS) {
        this.messageSMS = messageSMS;
        return this;
    }

    public List<MessageSentTo> getMessageSentTo() {
        return messageSentTo;
    }

    public SendResponse setMessageSentTo(List<MessageSentTo> messageSentTo) {
        this.messageSentTo = messageSentTo;
        return this;
    }
}
