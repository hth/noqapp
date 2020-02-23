package com.noqapp.service.nlp;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.noqapp.domain.types.SentimentTypeEnum;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.stanford.nlp.pipeline.StanfordCoreNLP;

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
        nlpService = new NLPService(new StanfordCoreNLP(props));
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
}