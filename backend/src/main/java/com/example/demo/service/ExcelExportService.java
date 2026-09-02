package com.example.demo.service;

import com.example.demo.entity.Evaluation;
import com.example.demo.entity.Submission;
import com.example.demo.repository.EvaluationRepository;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;

import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;
import com.example.demo.entity.Test;
import com.example.demo.repository.TestRepository;
import com.example.demo.entity.TestAttempt;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class ExcelExportService {

    private final EvaluationRepository evaluationRepository;
    private final TestRepository testRepository;

    public ExcelExportService(
            EvaluationRepository evaluationRepository,
             TestRepository testRepository
    ) {
        this.evaluationRepository = evaluationRepository;
        this.testRepository = testRepository;
    }

    public byte[] exportTestResults(
            Long testId
    ) {

        try {

            List<Evaluation> evaluations =
                    evaluationRepository.findBySubmission_TestAttempt_Test_Id(
                            testId
                    );
                    
                Test test =
                        testRepository.findById(testId)
                                .orElseThrow(() ->
                                        new RuntimeException(
                                                "Test not found with id: " + testId
                                        )
                                );

                int maximumPossibleScore =
                        test.getQuestions()
                                .stream()
                                .mapToInt(question -> question.getMaxMarks())
                                .sum();

            Workbook workbook = new XSSFWorkbook();

            Sheet sheet =
                    workbook.createSheet("Test Results");

            int rowNumber = 0;

            Row header =
                    sheet.createRow(rowNumber++);

            header.createCell(0).setCellValue("S.No");
            header.createCell(1).setCellValue("Student UID");
            header.createCell(2).setCellValue("Student Name");
            header.createCell(3).setCellValue("Question");
            header.createCell(4).setCellValue("AI Score");
            header.createCell(5).setCellValue("Final Score");
            header.createCell(6).setCellValue("Max Marks");
            header.createCell(7).setCellValue("Language");
            header.createCell(8).setCellValue("Status");
            header.createCell(9).setCellValue("Submitted At");

            int serial = 1;

            for (Evaluation evaluation : evaluations) {

                Submission submission =
                        evaluation.getSubmission();

                Row row =
                        sheet.createRow(rowNumber++);

                row.createCell(0).setCellValue(serial++);

                row.createCell(1).setCellValue(
                        submission.getTestAttempt()
                                .getStudentUid()
                );

                row.createCell(2).setCellValue(
                        submission.getTestAttempt()
                                .getStudentName()
                );

                row.createCell(3).setCellValue(
                        submission.getQuestion()
                                .getTitle()
                );

                row.createCell(4).setCellValue(
                        evaluation.getTotalScore()
                );

                Integer finalScore =
                        evaluation.getTeacherScore() != null
                                ? evaluation.getTeacherScore()
                                : evaluation.getTotalScore();

                row.createCell(5).setCellValue(
                        finalScore
                );

                row.createCell(6).setCellValue(
                        submission.getQuestion()
                                .getMaxMarks()
                );

                row.createCell(7).setCellValue(
                        submission.getLanguage()
                                .name()
                );

                row.createCell(8).setCellValue(
                        submission.getStatus()
                                .name()
                );

                row.createCell(9).setCellValue(
                        submission.getSubmittedAt()
                                .toString()
                );
            }

            for (int i = 0; i <= 9; i++) {
                sheet.autoSizeColumn(i);
            }

       // =========================================================
        // TOTAL RESULT SHEET
        // =========================================================

        Sheet totalSheet =
                workbook.createSheet("Total Result");

        int totalRowNumber = 0;

        Row totalHeader =
                totalSheet.createRow(totalRowNumber++);

        totalHeader.createCell(0).setCellValue("S.No");
        totalHeader.createCell(1).setCellValue("Student UID");
        totalHeader.createCell(2).setCellValue("Student Name");
        totalHeader.createCell(3).setCellValue("Final Score");
        totalHeader.createCell(4).setCellValue("Maximum Marks");
        totalHeader.createCell(5).setCellValue("Percentage");

        // Group evaluations by student attempt
        Map<Long, List<Evaluation>> evaluationsByAttempt =
                new LinkedHashMap<>();

        for (Evaluation evaluation : evaluations) {

        Long attemptId =
                evaluation.getSubmission()
                        .getTestAttempt()
                        .getId();

        evaluationsByAttempt
                .computeIfAbsent(
                        attemptId,
                        key -> new java.util.ArrayList<>()
                )
                .add(evaluation);
        }

        int totalSerial = 1;

        for (List<Evaluation> studentEvaluations :
                evaluationsByAttempt.values()) {

        if (studentEvaluations.isEmpty()) {
                continue;
        }

        TestAttempt attempt =
                studentEvaluations.get(0)
                        .getSubmission()
                        .getTestAttempt();

        int finalScore = 0;

        for (Evaluation evaluation : studentEvaluations) {

                Integer questionFinalScore =
                        evaluation.getTeacherScore() != null
                                ? evaluation.getTeacherScore()
                                : evaluation.getTotalScore();

                finalScore += questionFinalScore;
        }

        double percentage =
                maximumPossibleScore > 0
                        ? (finalScore * 100.0) / maximumPossibleScore
                        : 0.0;

        Row row =
                totalSheet.createRow(totalRowNumber++);

        row.createCell(0).setCellValue(totalSerial++);

        row.createCell(1).setCellValue(
                attempt.getStudentUid()
        );

        row.createCell(2).setCellValue(
                attempt.getStudentName()
        );

        row.createCell(3).setCellValue(
                finalScore
        );

        row.createCell(4).setCellValue(
                maximumPossibleScore
        );

        row.createCell(5).setCellValue(
                percentage
        );
        }

        // Auto-size Total Result columns
        for (int i = 0; i <= 5; i++) {
        totalSheet.autoSizeColumn(i);
        }

            ByteArrayOutputStream outputStream =
                    new ByteArrayOutputStream();

            workbook.write(outputStream);

            workbook.close();

            return outputStream.toByteArray();

        } catch (Exception exception) {

            throw new RuntimeException(
                    "Failed to export Excel",
                    exception
            );
        }
    }
}