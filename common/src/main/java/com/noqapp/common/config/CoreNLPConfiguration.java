package com.noqapp.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;

import java.util.Properties;

/**
 * hitender
 * 2019-01-14 14:04
 */
@Configuration
public class CoreNLPConfiguration {

    /**
     * With chat bot. Need more work.
     */
//    @Bean
//    public StanfordCoreNLP stanfordCoreNLP() {
//        Properties props = new Properties();
//        props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref, sentiment");
//        props.setProperty("ner.model",
//            "edu/stanford/nlp/models/ner/english.all.3class.distsim.crf.ser.gz," +
//                "edu/stanford/nlp/models/ner/english.muc.7class.distsim.crf.ser.gz," +
//                "edu/stanford/nlp/models/ner/english.conll.4class.distsim.crf.ser.gz," +
//                "nlp/noqueue/ner/medical-symptoms-ner-model.ser.gz");
//        props.setProperty("ignorecase", "true");
//        props.setProperty("validpospattern", "^(NN|JJ).*");
//        props.setProperty("parse.maxlen", "100");
//        return new StanfordCoreNLP(props);
//    }

    /**
     * Without chat bot. Simple sentiment analysis.
     *
     * @return
     */
    @Bean
    public StanfordCoreNLP stanfordCoreNLP() {
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, parse, sentiment, pos, lemma");
        return new StanfordCoreNLP(props);
    }

    @Bean
    public MaxentTagger maxentTagger() {
        return new MaxentTagger("nlp/stanford/models/english-left3words-distsim.tagger");
    }
}
