package com.example.demo.service;

import com.example.demo.entity.Submission;
import com.example.demo.enums.SubmissionStatus;
import com.example.demo.messaging.producer.EvaluationProducer;
import com.example.demo.repository.SubmissionRepository;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EvaluationRecoveryService {

    private final SubmissionRepository submissionRepository;
    private final EvaluationProducer evaluationProducer;
    private final SystemSettingService systemSettingService;

    public EvaluationRecoveryService(
        SubmissionRepository submissionRepository,
        EvaluationProducer evaluationProducer,
        SystemSettingService systemSettingService
) {
    this.submissionRepository = submissionRepository;
    this.evaluationProducer = evaluationProducer;
    this.systemSettingService = systemSettingService;
}

    @Scheduled(fixedDelay = 60000)
    public void recoverPendingSubmissions() {

        if (!systemSettingService.isAiEvaluationEnabled()) {
        System.out.println(
                "AI evaluation is disabled. Skipping pending submission recovery."
        );
        return;
}

        List<Submission> pendingSubmissions =
                submissionRepository.findByStatus(
                        SubmissionStatus.PENDING
                );

        LocalDateTime cutoff =
                LocalDateTime.now().minusMinutes(2);

        for (Submission submission : pendingSubmissions) {

            if (submission.getSubmittedAt().isBefore(cutoff)) {

                System.out.println(
                        "Recovering pending submission: "
                                + submission.getId()
                );

                evaluationProducer.sendSubmissionForEvaluation(
                        submission.getId()
                );
            }
        }
    }
}