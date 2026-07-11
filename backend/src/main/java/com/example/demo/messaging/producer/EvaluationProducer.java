package com.example.demo.messaging.producer;

import com.example.demo.messaging.config.RabbitMQConfig;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class EvaluationProducer {

    private final RabbitTemplate rabbitTemplate;


    public EvaluationProducer(
            RabbitTemplate rabbitTemplate
    ) {
        this.rabbitTemplate = rabbitTemplate;
    }


    public void sendSubmissionForEvaluation(
            Long submissionId
    ) {

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EVALUATION_EXCHANGE,
                RabbitMQConfig.EVALUATION_ROUTING_KEY,
                submissionId
        );

        System.out.println(
                "Sent submission to RabbitMQ: "
                        + submissionId
        );
    }
}