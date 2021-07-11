package com.noqapp.service;

import com.noqapp.domain.jms.FlexAppointment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

/**
 * hitender
 * 7/6/21 8:54 AM
 */
@Service
public class JMSProducerService {
    private static final Logger LOG = LoggerFactory.getLogger(JMSProducerService.class);

    private String activemqDestinationFlexAppointment;

    private JmsTemplate jmsFlexAppointmentTemplate;

    @Autowired
    public JMSProducerService(
        @Value("${activemq.destination.flexAppointment}")
        String activemqDestinationFlexAppointment,

        @Qualifier("jmsFlexAppointmentTemplate")
        JmsTemplate jmsFlexAppointmentTemplate
    ) {
        this.activemqDestinationFlexAppointment = activemqDestinationFlexAppointment;
        this.jmsFlexAppointmentTemplate = jmsFlexAppointmentTemplate;
    }

    public void invokeFlexAppointment(String codeQR, String scheduleDate, int beginTime) {
        jmsFlexAppointmentTemplate.send(
            activemqDestinationFlexAppointment,
            session -> session.createObjectMessage(FlexAppointment.newInstance(codeQR, scheduleDate, beginTime)));
    }
}
