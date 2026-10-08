package com.example.demo.messaging.consumer;

import com.example.demo.messaging.config.RabbitMQConfig;
import com.example.demo.service.EvaluationService;
import com.example.demo.service.SystemSettingService;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class EvaluationConsumer {

    private final EvaluationService evaluationService;
    private final SystemSettingService systemSettingService;

    public EvaluationConsumer(
            EvaluationService evaluationService,
            SystemSettingService systemSettingService
    ) {
        this.evaluationService = evaluationService;
        this.systemSettingService = systemSettingService;
    }

    @RabbitListener(queues = RabbitMQConfig.EVALUATION_QUEUE)
    public void consumeSubmissionForEvaluation(Long submissionId) {

        if (!systemSettingService.isAiEvaluationEnabled()) {

            System.out.println(
                    "AI evaluation is disabled. "
                            + "Skipping RabbitMQ submission: "
                            + submissionId
            );

            return;
        }

        try {

            evaluationService.evaluateSubmission(submissionId);

            System.out.println(
                    "Successfully evaluated submission: "
                            + submissionId
            );

        } catch (Exception exception) {

            System.err.println(
                    "Evaluation failed for submission: "
                            + submissionId
            );

            exception.printStackTrace();
        }
    }
}