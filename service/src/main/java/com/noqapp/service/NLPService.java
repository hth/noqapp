package com.noqapp.service;

import com.noqapp.domain.types.SentimentTypeEnum;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * hitender
 * 2019-01-15 09:56
 */
@Service
public class NLPService {
    private static final Logger LOG = LoggerFactory.getLogger(NLPService.class);

    private StanfordCoreNLP stanfordCoreNLP;

    @Autowired
    public NLPService(StanfordCoreNLP stanfordCoreNLP) {
        this.stanfordCoreNLP = stanfordCoreNLP;
    }

    /**
     * Only computes Negative and Positive sentiments. Neutral is considered as Positive sentiment.
     *
     * @param text
     * @return
     */
    public SentimentTypeEnum computeSentiment(String text) {
        int sentimentState = 0;
        SentimentTypeEnum sentimentType;
        if (StringUtils.isNotBlank(text)) {
            Annotation annotation = stanfordCoreNLP.process(text);
            List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
            for (CoreMap sentence : sentences) {
                sentimentType = SentimentTypeEnum.byDescription(sentence.get(SentimentCoreAnnotations.SentimentClass.class));
                sentimentState = sentimentState + sentimentType.getValue();
                LOG.info("{} {}", sentimentType, sentence);
            }

            return sentimentState < 0 ? SentimentTypeEnum.N : SentimentTypeEnum.P;
        }

        return null;
    }

    /** This is mostly used to log and spot negative sentiments. */
    public Map<String, SentimentTypeEnum> computeSentimentPerSentence(String text) {
        Map<String, SentimentTypeEnum> deconstruct = new LinkedHashMap<>();
        if (StringUtils.isNotBlank(text)) {
            Annotation annotation = stanfordCoreNLP.process(text);
            List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
            for (CoreMap sentence : sentences) {
                SentimentTypeEnum sentimentType = SentimentTypeEnum.byDescription(sentence.get(SentimentCoreAnnotations.SentimentClass.class));
                if (sentimentType == SentimentTypeEnum.N) {
                    deconstruct.put(sentence.toString(), sentimentType);
                    LOG.info("{} {}", sentimentType, sentence);
                }
            }

            return deconstruct;
        }

        return null;
    }
}
