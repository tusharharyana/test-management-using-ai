package com.example.demo.messaging.consumer;

import com.example.demo.messaging.config.RabbitMQConfig;
import com.example.demo.service.EvaluationService;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class EvaluationConsumer {

    private final EvaluationService evaluationService;


    public EvaluationConsumer(
            EvaluationService evaluationService
    ) {
        this.evaluationService = evaluationService;
    }


    @RabbitListener(
            queues = RabbitMQConfig.EVALUATION_QUEUE
    )
    public void consumeSubmission(
            Long submissionId
    ) {

        System.out.println(
                "Received submission from RabbitMQ: "
                        + submissionId
        );

        try {

            evaluationService.evaluateSubmission(
                    submissionId
            );

            System.out.println(
                    "Successfully evaluated submission: "
                            + submissionId
            );

        } catch (Exception exception) {

            System.err.println(
                    "Failed to evaluate submission "
                            + submissionId
                            + ": "
                            + exception.getMessage()
            );
        }
    }
}