package com.example.demo.messaging.producer;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class SubmissionEventListener {

    private final EvaluationProducer evaluationProducer;


    public SubmissionEventListener(
            EvaluationProducer evaluationProducer
    ) {
        this.evaluationProducer = evaluationProducer;
    }


    @TransactionalEventListener(
            phase = TransactionPhase.AFTER_COMMIT
    )
    public void handleSubmissionCreated(
            Long submissionId
    ) {

        evaluationProducer.sendSubmissionForEvaluation(
                submissionId
        );
    }
}