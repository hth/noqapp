package com.noqapp.service.nlp;

import com.noqapp.domain.types.SentimentTypeEnum;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.tokensregex.TokenSequenceMatcher;
import edu.stanford.nlp.ling.tokensregex.TokenSequencePattern;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import edu.stanford.nlp.util.CoreMap;

import java.util.ArrayList;
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
    private MaxentTagger maxentTagger;

    @Autowired
    public NLPService(StanfordCoreNLP stanfordCoreNLP, MaxentTagger maxentTagger) {
        this.stanfordCoreNLP = stanfordCoreNLP;
        this.maxentTagger = maxentTagger;
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

    public void sentenceTag(String text) {
        String tag = maxentTagger.tagString(text);
        String[] eachTags = tag.split("\\s+");
        for (String eachTag : eachTags) {
            LOG.info("tag={}", eachTag);
        }
    }

    /**
     * 12. NN   Noun, singular or mass
     * 13. NNS  Noun, plural
     * 14. NNP  Proper noun, singular
     * 15. NNPS Proper noun, plural
     * @param text
     * @return
     */
    public List<String> lookupNoun(String text) {
        Annotation annotation = stanfordCoreNLP.process(text);
        List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
        List<String> output = new ArrayList<>();
        String regex = "([{pos:/NN|NNS|NNP|NNPS/}])";
        for (CoreMap sentence : sentences) {
            List<CoreLabel> tokens = sentence.get(CoreAnnotations.TokensAnnotation.class);
            TokenSequencePattern pattern = TokenSequencePattern.compile(regex);
            TokenSequenceMatcher matcher = pattern.getMatcher(tokens);
            while (matcher.find()) {
                output.add(matcher.group());
            }
        }
        return output;
    }
}
