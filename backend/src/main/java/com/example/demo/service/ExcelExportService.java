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

@Service
public class ExcelExportService {

    private final EvaluationRepository evaluationRepository;

    public ExcelExportService(
            EvaluationRepository evaluationRepository
    ) {
        this.evaluationRepository = evaluationRepository;
    }

    public byte[] exportTestResults(
            Long testId
    ) {

        try {

            List<Evaluation> evaluations =
                    evaluationRepository.findBySubmission_TestAttempt_Test_Id(
                            testId
                    );

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