package com.example.demo.messaging.producer;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class SubmissionEventPublisher {

    private final ApplicationEventPublisher eventPublisher;


    public SubmissionEventPublisher(
            ApplicationEventPublisher eventPublisher
    ) {
        this.eventPublisher = eventPublisher;
    }


    public void publish(Long submissionId) {

        eventPublisher.publishEvent(submissionId);
    }
}