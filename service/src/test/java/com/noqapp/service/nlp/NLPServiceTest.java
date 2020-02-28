package com.noqapp.service.nlp;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.noqapp.domain.common.ChatContentClassifier;
import com.noqapp.domain.types.SentimentTypeEnum;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;

import java.util.List;
import java.util.Properties;

/**
 * hitender
 * 2019-01-15 10:11
 */
class NLPServiceTest {

    private NLPService nlpService;

    @BeforeEach
    void setUp() {
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref, sentiment");
        props.setProperty("ner.model",
            "edu/stanford/nlp/models/ner/english.all.3class.distsim.crf.ser.gz," +
                "edu/stanford/nlp/models/ner/english.muc.7class.distsim.crf.ser.gz," +
                "edu/stanford/nlp/models/ner/english.conll.4class.distsim.crf.ser.gz," +
                "nlp/noqueue/ner/medical-symptoms-ner-model.ser.gz");
        props.setProperty("parse.maxlen", "100");
        nlpService = new NLPService(
            new StanfordCoreNLP(props),
            new MaxentTagger("nlp/stanford/models/english-left3words-distsim.tagger"));
    }

    @Test
    void computeSentiment() {
        SentimentTypeEnum sentimentType = nlpService.computeSentiment("This is a good review");
        assertEquals(SentimentTypeEnum.P, sentimentType);

        sentimentType = nlpService.computeSentiment("This is a bad review");
        assertEquals(SentimentTypeEnum.N, sentimentType);

        sentimentType = nlpService.computeSentiment("This is a review");
        assertEquals(SentimentTypeEnum.P, sentimentType);

        sentimentType = nlpService.computeSentiment("This is a good review. Should have been a bad review");
        assertEquals(SentimentTypeEnum.P, sentimentType);

        sentimentType = nlpService.computeSentiment("Two wrong does not make it right. Should have been more careful");
        assertEquals(SentimentTypeEnum.N, sentimentType);
    }

    @Test
    void sentenceTag() {
        nlpService.sentenceTag("Hello World this is great place to be");
    }

    @Test
    void lookupNoun() {
        List<String> output = nlpService.lookupNoun("Hello World this is great place to be");
        assertEquals("[World, place]", output.toString());
    }

    @Test
    void communicationNoun() {
        String text = "I have fever";
        List<String> output = nlpService.lookupNoun(text);
        assertEquals("[fever]", output.toString());

        ChatContentClassifier chatContentClassifier = nlpService.lookup(text);
        assertEquals("[fever]", chatContentClassifier.getNouns().toString());

        text = "For last 4 days";
        output = nlpService.lookupNoun(text);
        assertEquals("[days]", output.toString());

        chatContentClassifier = nlpService.lookup(text);
        assertEquals("[days]", chatContentClassifier.getNouns().toString());

        text = "Which doctor should I see";
        output = nlpService.lookupNoun(text);
        assertEquals("[doctor]", output.toString());

        chatContentClassifier = nlpService.lookup(text);
        assertEquals("[doctor]", chatContentClassifier.getNouns().toString());

        text = "Possible health benefits";
        output = nlpService.lookupNoun(text);
        assertEquals("[health, benefits]", output.toString());

        chatContentClassifier = nlpService.lookup(text);
        assertEquals("[health, benefits]", chatContentClassifier.getNouns().toString());

        text = "Dig boy dig";
        output = nlpService.lookupNoun(text);
        assertEquals("[boy, dig]", output.toString());

        chatContentClassifier = nlpService.lookup(text);
        assertEquals("[boy, dig]", chatContentClassifier.getNouns().toString());

        text = "I would like to fly to san francisco";
        output = nlpService.lookupNoun(text);
        assertEquals("[francisco]", output.toString());

        chatContentClassifier = nlpService.lookup(text);
        assertEquals("[francisco]", chatContentClassifier.getNouns().toString());

        text = "I would like to fly to San Francisco or San Jose or Mumbai";
        output = nlpService.lookupNoun(text);
        assertEquals("[San, Francisco, San, Jose, Mumbai]", output.toString());

        chatContentClassifier = nlpService.lookup(text);
        assertEquals("[San, Francisco, San, Jose, Mumbai]", chatContentClassifier.getNouns().toString());

        text = "May be Wednesday or Thursday";
        output = nlpService.lookupNoun(text);
        assertEquals("[Wednesday, Thursday]", output.toString());

        chatContentClassifier = nlpService.lookup(text);
        assertEquals("[Wednesday, Thursday]", chatContentClassifier.getNouns().toString());

        chatContentClassifier = nlpService.lookup("Pain for last five days");
        assertEquals("[Pain, days]", chatContentClassifier.getNouns().toString());
    }
}