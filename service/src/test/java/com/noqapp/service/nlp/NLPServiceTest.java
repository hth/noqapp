package com.noqapp.service.nlp;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
        props.setProperty("annotators", "tokenize, ssplit, pos, lemma, parse, sentiment");
        nlpService = new NLPService(new StanfordCoreNLP(props), new MaxentTagger("nlp/stanford/models/english-left3words-distsim.tagger"));
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
        List<String> output = nlpService.lookupNoun("I have fever");
        assertEquals("[fever]", output.toString());

        output = nlpService.lookupNoun("For last 4 days");
        assertEquals("[days]", output.toString());

        output = nlpService.lookupNoun("Which doctor should I see");
        assertEquals("[doctor]", output.toString());

        output = nlpService.lookupNoun("Possible health benefits");
        assertEquals("[health, benefits]", output.toString());

        output = nlpService.lookupNoun("Dig boy dig");
        assertEquals("[boy, dig]", output.toString());

        output = nlpService.lookupNoun("I would like to fly to san francisco");
        assertEquals("[francisco]", output.toString());

        output = nlpService.lookupNoun("May be Wednesday or Thursday");
        assertEquals("[Wednesday, Thursday]", output.toString());
    }
}