package com.noqapp.service.nlp;

import com.noqapp.domain.common.ChatContentClassifier;
import com.noqapp.domain.types.SentimentTypeEnum;
import com.noqapp.domain.types.catgeory.MedicalDepartmentEnum;

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
        List<String> output = new ArrayList<>();

        Annotation annotation = stanfordCoreNLP.process(text);
        List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
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

    public ChatContentClassifier lookup(String text) {
        ChatContentClassifier chatContentClassifier = new ChatContentClassifier();

        Annotation annotation = stanfordCoreNLP.process(text);
        List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
        for (CoreMap sentence : sentences) {
            List<CoreLabel> tokens = sentence.get(CoreAnnotations.TokensAnnotation.class);
            for (CoreLabel token : tokens) {
                String word = token.get(CoreAnnotations.TextAnnotation.class);
                String pos = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);
                String ne = token.get(CoreAnnotations.NamedEntityTagAnnotation.class);

                switch (pos) {
                    case "NN":
                    case "NNS":
                    case "NNP":
                    case "NNPS":
                        chatContentClassifier.addNoun(word);
                }

                MedicalDepartmentEnum medicalDepartment = null;
                try {
                    medicalDepartment = MedicalDepartmentEnum.valueOf(ne);
                } catch (Exception e) {
                    LOG.info("Could not find ne={}", ne);
                }

                if (null != medicalDepartment) {
                    chatContentClassifier.addSymptoms(word);
                } else {
                    switch (ne) {
                        case "CAUSE_OF_DEATH":
                            chatContentClassifier.addSymptoms(word);
                            break;
                        case "CITY":
                            chatContentClassifier.addLocation(word);
                            break;
                        case "DATE":
                            chatContentClassifier.addDuration(word);
                        default:
                            LOG.info("Unknown {} {} {}", word, pos, ne);
                    }
                }
            }
        }
        LOG.info("{}", chatContentClassifier);
        return chatContentClassifier;
    }
}
