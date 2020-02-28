package com.noqapp.domain.common;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * hitender
 * 2/24/20 3:02 PM
 */
public class ChatContentClassifier {

    private List<String> symptoms = new LinkedList<>();
    private List<String> nouns = new LinkedList<>();
    private List<String> locations = new LinkedList<>();
    private List<String> durations = new LinkedList<>();

    private Map<String, Integer> chatClassified = new HashMap<>();

    public List<String> getNouns() {
        return nouns;
    }

    public ChatContentClassifier setNouns(List<String> nouns) {
        this.nouns = nouns;
        return this;
    }

    public ChatContentClassifier addNoun(String noun) {
        this.nouns.add(noun);
        chatClassified.put("NOUN", nouns.size());
        return this;
    }

    public List<String> getLocations() {
        return locations;
    }

    public ChatContentClassifier setLocations(List<String> locations) {
        this.locations = locations;
        return this;
    }

    public ChatContentClassifier addLocation(String location) {
        this.locations.add(location);
        chatClassified.put("LOCATION", locations.size());
        return this;
    }

    public List<String> getSymptoms() {
        return symptoms;
    }

    public ChatContentClassifier setSymptoms(List<String> symptoms) {
        this.symptoms = symptoms;
        return this;
    }

    public ChatContentClassifier addSymptoms(String symptom) {
        this.symptoms.add(symptom);
        chatClassified.put("SYMPTOMS", symptoms.size());
        return this;
    }

    public List<String> getDurations() {
        return durations;
    }

    public ChatContentClassifier setDurations(List<String> durations) {
        this.durations = durations;
        return this;
    }

    public ChatContentClassifier addDuration(String duration) {
        this.durations.add(duration);
        chatClassified.put("DURATION", durations.size());
        return this;
    }

    public Map<String, Integer> getChatClassified() {
        return chatClassified;
    }

    public ChatContentClassifier setChatClassified(Map<String, Integer> chatClassified) {
        this.chatClassified = chatClassified;
        return this;
    }

    @Override
    public String toString() {
        return "ChatContentClassifier{" +
            "symptoms=" + symptoms +
            ", nouns=" + nouns +
            ", locations=" + locations +
            ", durations=" + durations +
            ", chatClassified=" + chatClassified +
            '}';
    }
}
