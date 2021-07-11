package com.noqapp.common.config;

import com.noqapp.common.errorHandler.JMSErrorHandler;

import org.apache.activemq.ActiveMQConnectionFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;

import java.util.Arrays;

/**
 * hitender
 * 7/20/20 11:53 AM
 */
@Configuration
@EnableJms
public class JMSConfiguration {
    @Value("${activemq.host}")
    private String activemqHost;

    @Value("${activemq.port}")
    private String activemqPort;

    @Value("${activemq.destination.mail.signup}")
    private String activemqDestinationMailSignUp;

    @Value("${activemq.destination.mail.change}")
    private String activemqDestinationMailChange;

    @Value("${activemq.destination.feedback}")
    private String activemqDestinationFeedback;

    @Value("${activemq.destination.review.negative}")
    private String activemqDestinationReviewNegative;


    private JMSErrorHandler jmsErrorHandler = new JMSErrorHandler();

    @Bean
    public ActiveMQConnectionFactory connectionFactory() {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
        connectionFactory.setBrokerURL("tcp://" + activemqHost + ":" + activemqPort);
        connectionFactory.setTrustedPackages(Arrays.asList("com.noqapp", "java.util"));
        connectionFactory.setMaxThreadPoolSize(15);
        return connectionFactory;
    }

    @Bean
    public DefaultJmsListenerContainerFactory jmsMailSingUpListenerContainerFactory() {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory());
        factory.setErrorHandler(jmsErrorHandler);
        return factory;
    }

    @Bean(name = "jmsMailSignUpTemplate")
    public JmsTemplate jmsMailSignUpTemplate() {
        JmsTemplate template = new JmsTemplate();
        template.setConnectionFactory(connectionFactory());
        template.setDefaultDestinationName(activemqDestinationMailSignUp);
        return template;
    }

    @Bean
    public DefaultJmsListenerContainerFactory jmsMailChangeListenerContainerFactory() {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory());
        factory.setErrorHandler(jmsErrorHandler);
        return factory;
    }

    @Bean(name = "jmsMailChangeTemplate")
    public JmsTemplate jmsMailChangeTemplate() {
        JmsTemplate template = new JmsTemplate();
        template.setConnectionFactory(connectionFactory());
        template.setDefaultDestinationName(activemqDestinationMailChange);
        return template;
    }

    @Bean
    public DefaultJmsListenerContainerFactory jmsFeedbackListenerContainerFactory() {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory());
        factory.setErrorHandler(jmsErrorHandler);
        return factory;
    }

    @Bean(name = "jmsFeedbackTemplate")
    public JmsTemplate jmsFeedbackTemplate() {
        JmsTemplate template = new JmsTemplate();
        template.setConnectionFactory(connectionFactory());
        template.setDefaultDestinationName(activemqDestinationFeedback);
        return template;
    }

    @Bean
    public DefaultJmsListenerContainerFactory jmsReviewNegativeListenerContainerFactory() {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory());
        factory.setErrorHandler(jmsErrorHandler);
        return factory;
    }

    @Bean(name = "jmsReviewNegativeTemplate")
    public JmsTemplate jmsReviewNegativeTemplate() {
        JmsTemplate template = new JmsTemplate();
        template.setConnectionFactory(connectionFactory());
        template.setDefaultDestinationName(activemqDestinationReviewNegative);
        return template;
    }
}
