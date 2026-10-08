package com.example.demo.messaging.producer;

import com.example.demo.messaging.config.RabbitMQConfig;
import com.example.demo.service.SystemSettingService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class EvaluationProducer {

    private final RabbitTemplate rabbitTemplate;
    private final SystemSettingService systemSettingService;

    public EvaluationProducer(
            RabbitTemplate rabbitTemplate,
            SystemSettingService systemSettingService
    ) {
        this.rabbitTemplate = rabbitTemplate;
        this.systemSettingService = systemSettingService;
    }

    public void sendSubmissionForEvaluation(
            Long submissionId
    ) {

        if (!systemSettingService.isAiEvaluationEnabled()) {

            System.out.println(
                    "AI evaluation is disabled. "
                            + "Submission will not be sent to RabbitMQ: "
                            + submissionId
            );

            return;
        }

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