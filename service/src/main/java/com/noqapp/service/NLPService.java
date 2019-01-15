package com.noqapp.service;

import com.noqapp.domain.annotation.Mobile;
import com.noqapp.domain.types.SentimentTypeEnum;

import org.apache.commons.lang3.StringUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;

import java.util.List;

/**
 * hitender
 * 2019-01-15 09:56
 */
@Service
public class NLPService {

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
    @Mobile
    public SentimentTypeEnum computeSentiment(String text) {
        int totalCount = 0;
        SentimentTypeEnum sentimentType;
        if (StringUtils.isNotBlank(text)) {
            Annotation annotation = stanfordCoreNLP.process(text);
            List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
            for (CoreMap sentence : sentences) {
                sentimentType = SentimentTypeEnum.byDescription(sentence.get(SentimentCoreAnnotations.SentimentClass.class));
                totalCount = totalCount + sentimentType.getValue();
            }
        }

        return totalCount < 0 ? SentimentTypeEnum.N : SentimentTypeEnum.P;
    }
}
