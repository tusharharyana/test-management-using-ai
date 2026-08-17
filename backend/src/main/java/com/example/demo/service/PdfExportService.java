package com.example.demo.service;

import com.example.demo.entity.Question;
import com.example.demo.entity.Submission;
import com.example.demo.entity.TestAttempt;
import com.example.demo.repository.SubmissionRepository;
import com.example.demo.repository.TestAttemptRepository;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;
import com.example.demo.entity.Evaluation;
import com.example.demo.repository.EvaluationRepository;

@Service
public class PdfExportService {

    private final TestAttemptRepository testAttemptRepository;
    private final SubmissionRepository submissionRepository;
    private final EvaluationRepository evaluationRepository;

    public PdfExportService(
            TestAttemptRepository testAttemptRepository,
            SubmissionRepository submissionRepository,
            EvaluationRepository evaluationRepository
    ) {
        this.testAttemptRepository = testAttemptRepository;
        this.submissionRepository = submissionRepository;
        this.evaluationRepository = evaluationRepository;
    }


    // =========================================================
    // COLORS
    // =========================================================

    private static final BaseColor PRIMARY_COLOR =
            new BaseColor(37, 99, 235);

    private static final BaseColor PRIMARY_DARK =
            new BaseColor(30, 64, 175);

    private static final BaseColor LIGHT_BLUE =
            new BaseColor(239, 246, 255);

    private static final BaseColor LIGHT_GRAY =
            new BaseColor(248, 250, 252);

    private static final BaseColor BORDER_COLOR =
            new BaseColor(226, 232, 240);

    private static final BaseColor TEXT_COLOR =
            new BaseColor(30, 41, 59);

    private static final BaseColor MUTED_COLOR =
            new BaseColor(100, 116, 139);


    // =========================================================
    // GENERATE PDF
    // =========================================================

    public byte[] generateTestPdf(Long attemptId) {

        try {

            TestAttempt attempt =
                    testAttemptRepository
                            .findById(attemptId)
                            .orElseThrow(() ->
                                    new RuntimeException(
                                            "Test attempt not found"
                                    )
                            );
                boolean showAiScore =attempt.getTest().isShowAiScoreInPdf();
                boolean showAiFeedback = attempt.getTest().isShowAiFeedbackInPdf();


            List<Submission> submissions =
                    submissionRepository
                            .findAllByTestAttemptId(attemptId);
                            int totalAiScore = 0;

                int totalMaxMarks = 0;

                if (showAiScore) {

                for (Question question :
                        attempt.getTest().getQuestions()) {

                        totalMaxMarks += question.getMaxMarks();

                        Submission submission =
                                submissions.stream()
                                        .filter(s ->
                                                s.getQuestion()
                                                        .getId()
                                                        .equals(question.getId())
                                        )
                                        .findFirst()
                                        .orElse(null);

                        if (submission != null) {

                        Evaluation evaluation =
                                evaluationRepository
                                        .findBySubmissionId(
                                                submission.getId()
                                        )
                                        .orElse(null);

                        if (evaluation != null) {
                                totalAiScore +=
                                        evaluation.getTotalScore();
                        }
                        }
                }
                }


            ByteArrayOutputStream outputStream =
                    new ByteArrayOutputStream();


            Document document = new Document(
                    PageSize.A4,
                    45,
                    45,
                    55,
                    55
            );


            PdfWriter writer =
                    PdfWriter.getInstance(
                            document,
                            outputStream
                    );


            // Page number footer
            writer.setPageEvent(
                    new PdfPageEventHelper() {

                        @Override
                        public void onEndPage(
                                PdfWriter writer,
                                Document document
                        ) {

                            PdfContentByte canvas =
                                    writer.getDirectContent();

                            canvas.saveState();

                            Font footerFont =
                                    new Font(
                                            Font.FontFamily.HELVETICA,
                                            8,
                                            Font.NORMAL,
                                            MUTED_COLOR
                                    );

                            Phrase footer =
                                    new Phrase(
                                            "CodeRanBhumi - AI Coding Assessment Platform",
                                            footerFont
                                    );

                            ColumnText.showTextAligned(
                                    canvas,
                                    Element.ALIGN_LEFT,
                                    footer,
                                    document.left(),
                                    25,
                                    0
                            );

                            Phrase page =
                                    new Phrase(
                                            "Page "
                                                    + writer.getPageNumber(),
                                            footerFont
                                    );

                            ColumnText.showTextAligned(
                                    canvas,
                                    Element.ALIGN_RIGHT,
                                    page,
                                    document.right(),
                                    25,
                                    0
                            );

                            canvas.restoreState();
                        }
                    }
            );


            document.open();


            // =================================================
            // FONTS
            // =================================================

            Font mainTitleFont =
                    new Font(
                            Font.FontFamily.HELVETICA,
                            22,
                            Font.BOLD,
                            BaseColor.WHITE
                    );


            Font testTitleFont =
                    new Font(
                            Font.FontFamily.HELVETICA,
                            14,
                            Font.BOLD,
                            TEXT_COLOR
                    );


            Font sectionFont =
                    new Font(
                            Font.FontFamily.HELVETICA,
                            11,
                            Font.BOLD,
                            PRIMARY_DARK
                    );


            Font questionFont =
                    new Font(
                            Font.FontFamily.HELVETICA,
                            16,
                            Font.BOLD,
                            TEXT_COLOR
                    );


            Font normalFont =
                    new Font(
                            Font.FontFamily.HELVETICA,
                            10,
                            Font.NORMAL,
                            TEXT_COLOR
                    );


            Font smallFont =
                    new Font(
                            Font.FontFamily.HELVETICA,
                            9,
                            Font.NORMAL,
                            MUTED_COLOR
                    );


            Font boldFont =
                    new Font(
                            Font.FontFamily.HELVETICA,
                            10,
                            Font.BOLD,
                            TEXT_COLOR
                    );


            Font codeFont =
                    new Font(
                            Font.FontFamily.COURIER,
                            8.5f,
                            Font.NORMAL,
                            new BaseColor(30, 41, 59)
                    );


            // =================================================
            // HEADER
            // =================================================

            PdfPTable headerTable =
                    new PdfPTable(1);

            headerTable.setWidthPercentage(100);

            PdfPCell headerCell =
                    new PdfPCell();

            headerCell.setBackgroundColor(
                    PRIMARY_COLOR
            );

            headerCell.setBorder(
                    Rectangle.NO_BORDER
            );

            headerCell.setPaddingTop(20);
            headerCell.setPaddingBottom(20);
            headerCell.setPaddingLeft(20);
            headerCell.setPaddingRight(20);


            Paragraph platform =
                    new Paragraph(
                            "CodeRanBhumi - AI Coding Assessment",
                            mainTitleFont
                    );

            platform.setAlignment(
                    Element.ALIGN_CENTER
            );


            headerCell.addElement(platform);


            Paragraph testTitle =
                    new Paragraph(
                            attempt.getTest().getTitle(),
                            new Font(
                                    Font.FontFamily.HELVETICA,
                                    13,
                                    Font.NORMAL,
                                    BaseColor.WHITE
                            )
                    );

            testTitle.setAlignment(
                    Element.ALIGN_CENTER
            );


            headerCell.addElement(testTitle);

            headerTable.addCell(headerCell);

            document.add(headerTable);


            document.add(
                    new Paragraph(" ")
            );


            // =================================================
            // STUDENT INFORMATION
            // =================================================

            Paragraph studentHeading =
                    new Paragraph(
                            "STUDENT INFORMATION",
                            sectionFont
                    );

            studentHeading.setSpacingBefore(5);
            studentHeading.setSpacingAfter(8);

            document.add(studentHeading);


            PdfPTable studentTable =
                    new PdfPTable(2);

            studentTable.setWidthPercentage(100);

            studentTable.setWidths(
                    new float[]{1.2f, 2.8f}
            );


            addInfoRow(
                    studentTable,
                    "Student Name",
                    attempt.getStudentName(),
                    boldFont,
                    normalFont
            );


            addInfoRow(
                    studentTable,
                    "Student UID",
                    attempt.getStudentUid(),
                    boldFont,
                    normalFont
            );


            if (attempt.getSubmittedAt() != null) {

                DateTimeFormatter formatter =
                        DateTimeFormatter.ofPattern(
                                "dd-MMM-yyyy hh:mm a"
                        );

                addInfoRow(
                        studentTable,
                        "Submitted At",
                        attempt.getSubmittedAt()
                                .format(formatter),
                        boldFont,
                        normalFont
                );
            }


            addInfoRow(
                    studentTable,
                    "Duration",
                    attempt.getTest()
                            .getDurationMinutes()
                            + " minutes",
                    boldFont,
                    normalFont
            );


            document.add(studentTable);
            if (showAiScore) {

                document.add(
                        new Paragraph(" ")
                );

                addSectionHeading(
                        document,
                        "ASSESSMENT SUMMARY",
                        sectionFont
                );

                PdfPTable summaryTable =
                        new PdfPTable(2);

                summaryTable.setWidthPercentage(100);

                summaryTable.setWidths(
                        new float[]{1.5f, 1.5f}
                );

                addMetadataCell(
                        summaryTable,
                        "AI SCORE",
                        totalAiScore
                                + " / "
                                + totalMaxMarks
                );

                addMetadataCell(
                        summaryTable,
                        "QUESTIONS",
                        String.valueOf(
                                attempt.getTest()
                                        .getQuestions()
                                        .size()
                        )
                );

                document.add(summaryTable);

                document.add(
                        new Paragraph(" ")
                );
                }


            document.add(
                    new Paragraph(" ")
            );


            // =================================================
            // QUESTIONS
            // =================================================

            List<Question> questions =
                    attempt.getTest().getQuestions();


            for (int i = 0; i < questions.size(); i++) {

                Question question =
                        questions.get(i);


                // Question header
                PdfPTable questionHeader =
                        new PdfPTable(1);

                questionHeader.setWidthPercentage(100);


                PdfPCell questionHeaderCell =
                        new PdfPCell();

                questionHeaderCell.setBackgroundColor(
                        PRIMARY_COLOR
                );

                questionHeaderCell.setBorder(
                        Rectangle.NO_BORDER
                );

                questionHeaderCell.setPadding(10);


                Paragraph questionNumber =
                        new Paragraph(
                                "QUESTION " + (i + 1),
                                new Font(
                                        Font.FontFamily.HELVETICA,
                                        11,
                                        Font.BOLD,
                                        BaseColor.WHITE
                                )
                        );


                questionHeaderCell.addElement(
                        questionNumber
                );


                questionHeader.addCell(
                        questionHeaderCell
                );


                document.add(
                        questionHeader
                );


                document.add(
                        new Paragraph(" ")
                );


                // Question title
                Paragraph questionTitle =
                        new Paragraph(
                                question.getTitle(),
                                questionFont
                        );

                questionTitle.setSpacingAfter(8);

                document.add(questionTitle);


                // Marks + Language
                Submission submission =
                        submissions
                                .stream()
                                .filter(s ->
                                        s.getQuestion()
                                                .getId()
                                                .equals(
                                                        question.getId()
                                                )
                                )
                                .findFirst()
                                .orElse(null);
                Evaluation evaluation = null;

                if (submission != null) {

                evaluation =
                        evaluationRepository
                                .findBySubmissionId(
                                        submission.getId()
                                )
                                .orElse(null);
                }

                PdfPTable metadata =
                        new PdfPTable(2);

                metadata.setWidthPercentage(100);


                addMetadataCell(
                        metadata,
                        "Maximum Marks",
                        String.valueOf(
                                question.getMaxMarks()
                        )
                );


                String language =
                        submission != null
                                ? submission.getLanguage().name()
                                : "Not Submitted";


                addMetadataCell(
                        metadata,
                        "Language",
                        language
                );


                document.add(metadata);


                document.add(
                        new Paragraph(" ")
                );


                // =================================================
                // PROBLEM STATEMENT
                // =================================================

                addSectionHeading(
                        document,
                        "PROBLEM STATEMENT",
                        sectionFont
                );


                Paragraph problem =
                        new Paragraph(
                                question.getProblemStatement(),
                                normalFont
                        );

                problem.setLeading(15);

                document.add(problem);


                document.add(
                        new Paragraph(" ")
                );


                // =================================================
                // EXAMPLES
                // =================================================

                if (
                        question.getExamples() != null
                                &&
                        !question.getExamples()
                                .isBlank()
                ) {

                    addSectionHeading(
                            document,
                            "EXAMPLES",
                            sectionFont
                    );


                    PdfPTable exampleTable =
                            new PdfPTable(1);

                    exampleTable.setWidthPercentage(100);


                    PdfPCell exampleCell =
                            new PdfPCell();


                    exampleCell.setBackgroundColor(
                            LIGHT_BLUE
                    );

                    exampleCell.setBorderColor(
                            new BaseColor(
                                    191,
                                    219,
                                    254
                            )
                    );

                    exampleCell.setPadding(12);


                    Paragraph examples =
                            new Paragraph(
                                    question.getExamples(),
                                    new Font(
                                            Font.FontFamily.COURIER,
                                            9,
                                            Font.NORMAL,
                                            TEXT_COLOR
                                    )
                            );

                    examples.setLeading(13);


                    exampleCell.addElement(
                            examples
                    );


                    exampleTable.addCell(
                            exampleCell
                    );


                    document.add(
                            exampleTable
                    );


                    document.add(
                            new Paragraph(" ")
                    );
                }


                // =================================================
                // STUDENT CODE
                // =================================================

                addSectionHeading(
                        document,
                        "STUDENT'S CODE",
                        sectionFont
                );


                PdfPTable codeTable =
                        new PdfPTable(1);

                codeTable.setWidthPercentage(100);


                PdfPCell codeCell =
                        new PdfPCell();


                codeCell.setBackgroundColor(
                        LIGHT_GRAY
                );

                codeCell.setBorderColor(
                        BORDER_COLOR
                );

                codeCell.setPadding(12);


                if (submission != null) {

                    Paragraph code =
                            new Paragraph(
                                    submission.getSourceCode(),
                                    codeFont
                            );

                    code.setLeading(11);

                    codeCell.addElement(
                            code
                    );

                } else {

                    Paragraph noCode =
                            new Paragraph(
                                    "No code submitted.",
                                    smallFont
                            );

                    codeCell.addElement(
                            noCode
                    );
                }


                codeTable.addCell(
                        codeCell
                );


                document.add(
                        codeTable
                );

               // =========================================================
                // AI EVALUATION
                // =========================================================

                if (evaluation != null &&
                        (showAiScore || showAiFeedback)) {

                addSectionHeading(
                        document,
                        "AI EVALUATION",
                        sectionFont
                );


                // =====================================================
                // SCORE
                // =====================================================

                if (showAiScore) {

                        PdfPTable scoreTable =
                                new PdfPTable(1);

                        scoreTable.setWidthPercentage(100);


                        PdfPCell scoreCell =
                                new PdfPCell();

                        scoreCell.setBackgroundColor(
                                LIGHT_BLUE
                        );

                        scoreCell.setBorderColor(
                                BORDER_COLOR
                        );

                        scoreCell.setPadding(12);


                        Paragraph scoreTitle =
                                new Paragraph(
                                        "AI SCORE",
                                        new Font(
                                                Font.FontFamily.HELVETICA,
                                                8,
                                                Font.BOLD,
                                                MUTED_COLOR
                                        )
                                );


                        Paragraph scoreValue =
                                new Paragraph(
                                        evaluation.getTotalScore()
                                                + " / "
                                                + question.getMaxMarks(),
                                        new Font(
                                                Font.FontFamily.HELVETICA,
                                                18,
                                                Font.BOLD,
                                                PRIMARY_DARK
                                        )
                                );


                        scoreCell.addElement(scoreTitle);
                        scoreCell.addElement(scoreValue);

                        scoreTable.addCell(scoreCell);

                        document.add(scoreTable);

                        document.add(
                                new Paragraph(" ")
                        );


                        // =================================================
                        // EVALUATION CRITERIA
                        // =================================================

                        Paragraph criteriaHeading =
                                new Paragraph(
                                        "HOW YOUR CODE WAS EVALUATED",
                                        boldFont
                                );

                        criteriaHeading.setSpacingAfter(6);

                        document.add(criteriaHeading);


                        PdfPTable criteriaTable =
                                new PdfPTable(2);

                        criteriaTable.setWidthPercentage(100);

                        criteriaTable.setWidths(
                                new float[]{3.5f, 1.2f}
                        );


                        addCriteriaRow(
                                criteriaTable,
                                "Logic & Correctness",
                                "15",
                                evaluation.getCorrectnessScore()
                        );

                        addCriteriaRow(
                                criteriaTable,
                                "Edge Case Handling",
                                "6",
                                evaluation.getEdgeCaseScore()
                        );

                        addCriteriaRow(
                                criteriaTable,
                                "Efficiency",
                                "4",
                                evaluation.getEfficiencyScore()
                        );

                        addCriteriaRow(
                                criteriaTable,
                                "Code Quality",
                                "3",
                                evaluation.getCodeQualityScore()
                        );

                        addCriteriaRow(
                                criteriaTable,
                                "Syntax & Completeness",
                                "2",
                                evaluation.getSyntaxScore()
                        );


                        document.add(criteriaTable);

                        document.add(
                                new Paragraph(" ")
                        );

                }
                // =====================================================
                // FEEDBACK
                // =====================================================

                if (showAiFeedback &&
                        evaluation.getFeedback() != null &&
                        !evaluation.getFeedback().isBlank()) {

                        document.add(
                                new Paragraph(" ")
                        );

                        Paragraph feedbackHeading =
                                new Paragraph(
                                        "AI FEEDBACK",
                                        sectionFont
                                );

                        feedbackHeading.setSpacingAfter(6);

                        document.add(feedbackHeading);


                        PdfPTable feedbackTable =
                                new PdfPTable(1);

                        feedbackTable.setWidthPercentage(100);


                        PdfPCell feedbackCell =
                                new PdfPCell();

                        feedbackCell.setBackgroundColor(
                                LIGHT_GRAY
                        );

                        feedbackCell.setBorderColor(
                                BORDER_COLOR
                        );

                        feedbackCell.setPadding(12);


                        Paragraph feedback =
                                new Paragraph(
                                        evaluation.getFeedback(),
                                        normalFont
                                );

                        feedback.setLeading(14);

                        feedbackCell.addElement(
                                feedback
                        );

                        feedbackTable.addCell(
                                feedbackCell
                        );

                        document.add(
                                feedbackTable
                        );
                }
                }

                // Question spacing
                document.add(
                        new Paragraph(" ")
                );

                document.add(
                        new Paragraph(" ")
                );
            }


            // =================================================
            // END MESSAGE
            // =================================================

            PdfPTable endTable =
                    new PdfPTable(1);

            endTable.setWidthPercentage(100);


            PdfPCell endCell =
                    new PdfPCell();


            endCell.setBackgroundColor(
                    LIGHT_BLUE
            );

            endCell.setBorderColor(
                    new BaseColor(
                            191,
                            219,
                            254
                    )
            );

            endCell.setPadding(15);


            Paragraph endMessage =
                    new Paragraph(
                            "End of Student Submission",
                            new Font(
                                    Font.FontFamily.HELVETICA,
                                    10,
                                    Font.BOLD,
                                    PRIMARY_DARK
                            )
                    );


            endMessage.setAlignment(
                    Element.ALIGN_CENTER
            );


            endCell.addElement(
                    endMessage
            );


            endTable.addCell(
                    endCell
            );


            document.add(
                    endTable
            );


            document.close();


            return outputStream.toByteArray();


        } catch (Exception exception) {

            throw new RuntimeException(
                    "Failed to generate test PDF: "
                            + exception.getMessage(),
                    exception
            );
        }
    }


    // =========================================================
    // STUDENT INFORMATION ROW
    // =========================================================

    private void addInfoRow(
            PdfPTable table,
            String label,
            String value,
            Font labelFont,
            Font valueFont
    ) {

        PdfPCell labelCell =
                new PdfPCell(
                        new Phrase(
                                label,
                                labelFont
                        )
                );

        labelCell.setBackgroundColor(
                LIGHT_GRAY
        );

        labelCell.setBorderColor(
                BORDER_COLOR
        );

        labelCell.setPadding(9);


        PdfPCell valueCell =
                new PdfPCell(
                        new Phrase(
                                value,
                                valueFont
                        )
                );

        valueCell.setBorderColor(
                BORDER_COLOR
        );

        valueCell.setPadding(9);


        table.addCell(labelCell);
        table.addCell(valueCell);
    }


    // =========================================================
    // METADATA CELL
    // =========================================================

    private void addMetadataCell(
            PdfPTable table,
            String label,
            String value
    ) {

        PdfPCell cell =
                new PdfPCell();


        cell.setBackgroundColor(
                LIGHT_GRAY
        );

        cell.setBorderColor(
                BORDER_COLOR
        );

        cell.setPadding(10);


        Paragraph labelParagraph =
                new Paragraph(
                        label,
                        new Font(
                                Font.FontFamily.HELVETICA,
                                8,
                                Font.BOLD,
                                MUTED_COLOR
                        )
                );


        Paragraph valueParagraph =
                new Paragraph(
                        value,
                        new Font(
                                Font.FontFamily.HELVETICA,
                                10,
                                Font.BOLD,
                                TEXT_COLOR
                        )
                );


        cell.addElement(
                labelParagraph
        );

        cell.addElement(
                valueParagraph
        );


        table.addCell(cell);
    }


    // =========================================================
    // SECTION HEADING
    // =========================================================

    private void addSectionHeading(
            Document document,
            String text,
            Font font
    ) throws DocumentException {

        Paragraph heading =
                new Paragraph(
                        text,
                        font
                );

        heading.setSpacingBefore(5);
        heading.setSpacingAfter(6);

        document.add(heading);
    }

    private void addCriteriaRow(
        PdfPTable table,
        String criterion,
        String maximum,
        Integer score
        ) {

    PdfPCell criterionCell =
            new PdfPCell(
                    new Phrase(
                            criterion,
                            new Font(
                                    Font.FontFamily.HELVETICA,
                                    9,
                                    Font.BOLD,
                                    TEXT_COLOR
                            )
                    )
            );

    criterionCell.setBackgroundColor(
            LIGHT_GRAY
    );

    criterionCell.setBorderColor(
            BORDER_COLOR
    );

    criterionCell.setPadding(8);


    String scoreText =
            (score == null ? "0" : score)
                    + " / "
                    + maximum;


    PdfPCell scoreCell =
            new PdfPCell(
                    new Phrase(
                            scoreText,
                            new Font(
                                    Font.FontFamily.HELVETICA,
                                    9,
                                    Font.BOLD,
                                    PRIMARY_DARK
                            )
                    )
            );

    scoreCell.setHorizontalAlignment(
            Element.ALIGN_RIGHT
    );

    scoreCell.setBorderColor(
            BORDER_COLOR
    );

    scoreCell.setPadding(8);


    table.addCell(criterionCell);
    table.addCell(scoreCell);
}
}