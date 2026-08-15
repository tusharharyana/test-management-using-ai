import { useState } from "react";
import { useNavigate } from "react-router-dom";

import { createTest } from "../../api/testApi";

const createEmptyQuestion = () => ({
  title: "",
  problemStatement: "",
  examples: "",
  maxMarks: 30,
  questionOrder: 1,

  testCases: [
    {
      input: "",
      expectedOutput: "",
      testCaseOrder: 1,
    },
  ],
});

function CreateTestPage() {
  const navigate = useNavigate();

  const [formData, setFormData] = useState({
    title: "",
    description: "",
    durationMinutes: 30,
    questions: [createEmptyQuestion()],
  });

  const [submitting, setSubmitting] = useState(false);

  const [error, setError] = useState("");

  const handleTestFieldChange = (event) => {
    const { name, value } = event.target;

    setFormData((previous) => ({
      ...previous,

      [name]: name === "durationMinutes" ? Number(value) : value,
    }));
  };

  const handleQuestionChange = (index, field, value) => {
    setFormData((previous) => {
      const updatedQuestions = [...previous.questions];

      updatedQuestions[index] = {
        ...updatedQuestions[index],

        [field]: field === "maxMarks" ? Number(value) : value,
      };

      return {
        ...previous,
        questions: updatedQuestions,
      };
    });
  };

  const addQuestion = () => {
    setFormData((previous) => ({
      ...previous,

      questions: [
        ...previous.questions,

        {
          ...createEmptyQuestion(),

          questionOrder: previous.questions.length + 1,
        },
      ],
    }));
  };

  const removeQuestion = (index) => {
    if (formData.questions.length === 1) {
      return;
    }

    setFormData((previous) => {
      const updatedQuestions = previous.questions
        .filter((_, questionIndex) => questionIndex !== index)
        .map((question, questionIndex) => ({
          ...question,

          questionOrder: questionIndex + 1,
        }));

      return {
        ...previous,
        questions: updatedQuestions,
      };
    });
  };

  const handleSubmit = async (event) => {
    event.preventDefault();

    setSubmitting(true);
    setError("");

    try {
      const payload = {
        ...formData,

        durationMinutes: Number(formData.durationMinutes),

        questions: formData.questions.map((question, index) => ({
          title: question.title,
          problemStatement: question.problemStatement,
          examples: question.examples,
          maxMarks: Number(question.maxMarks),
          questionOrder: index + 1,

          testCases: question.testCases.map((testCase, testCaseIndex) => ({
            input: testCase.input,
            expectedOutput: testCase.expectedOutput,
            testCaseOrder: testCaseIndex + 1,
          })),
        })),
      };

      await createTest(payload);

      navigate("/teacher");
    } catch (error) {
      console.error("Failed to create test:", error);

      const apiData = error.response?.data;

      if (apiData?.validationErrors) {
        const validationMessage = Object.values(apiData.validationErrors).join(
          ", ",
        );

        setError(validationMessage);
      } else {
        setError(apiData?.message || "Unable to create test.");
      }
    } finally {
      setSubmitting(false);
    }
  };

  const handleTestCaseChange = (questionIndex, testCaseIndex, field, value) => {
    setFormData((previous) => {
      const updatedQuestions = [...previous.questions];

      const updatedTestCases = [...updatedQuestions[questionIndex].testCases];

      updatedTestCases[testCaseIndex] = {
        ...updatedTestCases[testCaseIndex],
        [field]: value,
      };

      updatedQuestions[questionIndex] = {
        ...updatedQuestions[questionIndex],
        testCases: updatedTestCases,
      };

      return {
        ...previous,
        questions: updatedQuestions,
      };
    });
  };

  const addTestCase = (questionIndex) => {
    setFormData((previous) => {
      const updatedQuestions = [...previous.questions];

      const question = updatedQuestions[questionIndex];

      if (question.testCases.length >= 10) {
        return previous;
      }

      updatedQuestions[questionIndex] = {
        ...question,

        testCases: [
          ...question.testCases,

          {
            input: "",
            expectedOutput: "",
            testCaseOrder: question.testCases.length + 1,
          },
        ],
      };

      return {
        ...previous,
        questions: updatedQuestions,
      };
    });
  };

  const removeTestCase = (questionIndex, testCaseIndex) => {
    setFormData((previous) => {
      const updatedQuestions = [...previous.questions];

      const question = updatedQuestions[questionIndex];

      // At least one test case must remain
      if (question.testCases.length === 1) {
        return previous;
      }

      const updatedTestCases = question.testCases
        .filter((_, index) => index !== testCaseIndex)
        .map((testCase, index) => ({
          ...testCase,
          testCaseOrder: index + 1,
        }));

      updatedQuestions[questionIndex] = {
        ...question,
        testCases: updatedTestCases,
      };

      return {
        ...previous,
        questions: updatedQuestions,
      };
    });
  };

  return (
    <div className="create-test-page">
      <header className="teacher-navbar">
        <div className="teacher-brand" onClick={() => navigate("/teacher")}>
          <div className="teacher-brand-icon">{"</>"}</div>

          <div>
            <strong>AI Coding Assessment</strong>
            <span>Create Test</span>
          </div>
        </div>
      </header>

      <main className="create-test-container">
        <button
          className="teacher-back-button"
          onClick={() => navigate("/teacher")}
        >
          ← Back to Dashboard
        </button>

        <div className="create-test-heading">
          <span className="dashboard-eyebrow">New Assessment</span>

          <h1>Create Coding Test</h1>

          <p>
            Add the test details and competitive programming questions. Student
            code will be evaluated automatically by AI.
          </p>
        </div>

        {error && (
          <div className="dashboard-error">
            <span>{error}</span>
          </div>
        )}

        <form className="create-test-form" onSubmit={handleSubmit}>
          <section className="teacher-form-section">
            <div className="section-heading">
              <div className="section-number">1</div>

              <div>
                <h2>Test Information</h2>

                <p>Basic details about the assessment.</p>
              </div>
            </div>

            <div className="teacher-form-grid">
              <div className="teacher-form-group full-width">
                <label htmlFor="title">Test Title</label>

                <input
                  id="title"
                  name="title"
                  type="text"
                  value={formData.title}
                  onChange={handleTestFieldChange}
                  placeholder="Example: DSA Array Assessment"
                  required
                />
              </div>

              <div className="teacher-form-group full-width">
                <label htmlFor="description">Description</label>

                <textarea
                  id="description"
                  name="description"
                  rows="4"
                  value={formData.description}
                  onChange={handleTestFieldChange}
                  placeholder="Briefly describe this coding assessment..."
                />
              </div>

              <div className="teacher-form-group">
                <label htmlFor="durationMinutes">Duration in Minutes</label>

                <input
                  id="durationMinutes"
                  name="durationMinutes"
                  type="number"
                  min="1"
                  value={formData.durationMinutes}
                  onChange={handleTestFieldChange}
                  required
                />
              </div>
            </div>
          </section>

          <section className="teacher-form-section">
            <div className="section-heading">
              <div className="section-number">2</div>

              <div>
                <h2>Coding Questions</h2>

                <p>Add one or more problems for students.</p>
              </div>
            </div>

            <div className="questions-form-list">
              {formData.questions.map((question, index) => (
                <div className="question-form-card" key={index}>
                  <div className="question-form-header">
                    <div>
                      <span>Question {index + 1}</span>

                      <strong>{question.title || "Untitled Problem"}</strong>
                    </div>

                    {formData.questions.length > 1 && (
                      <button
                        type="button"
                        className="remove-question-button"
                        onClick={() => removeQuestion(index)}
                      >
                        Remove
                      </button>
                    )}
                  </div>

                  <div className="teacher-form-grid">
                    <div className="teacher-form-group full-width">
                      <label>Question Title</label>

                      <input
                        type="text"
                        value={question.title}
                        onChange={(event) =>
                          handleQuestionChange(
                            index,
                            "title",
                            event.target.value,
                          )
                        }
                        placeholder="Example: Find Maximum Element"
                        required
                      />
                    </div>

                    <div className="teacher-form-group full-width">
                      <label>Problem Statement</label>

                      <textarea
                        rows="7"
                        value={question.problemStatement}
                        onChange={(event) =>
                          handleQuestionChange(
                            index,
                            "problemStatement",
                            event.target.value,
                          )
                        }
                        placeholder={`Example:

Given an array of N integers, find and print the maximum element.

Input:
First line contains N.
Second line contains N integers.

Output:
Print the maximum element.`}
                        required
                      />
                    </div>

                    <div className="teacher-form-group full-width">
                      <label>Examples</label>

                      <textarea
                        rows="5"
                        value={question.examples}
                        onChange={(event) =>
                          handleQuestionChange(
                            index,
                            "examples",
                            event.target.value,
                          )
                        }
                        placeholder={`Input:
5
1 8 3 9 2

Output:
9`}
                      />
                    </div>

                    <div className="teacher-form-group">
                      <label>Maximum Marks</label>

                      <input
                        type="number"
                        min="1"
                        value={question.maxMarks}
                        onChange={(event) =>
                          handleQuestionChange(
                            index,
                            "maxMarks",
                            event.target.value,
                          )
                        }
                        required
                      />
                    </div>
                  </div>
                  <div className="test-cases-section">
                    <div className="test-cases-header">
                      <div>
                        <h3>Test Cases</h3>

                        <p>
                          Add input and expected output used to verify student
                          code.
                        </p>
                      </div>

                      <span className="test-case-count">
                        {question.testCases.length} / 10
                      </span>
                    </div>

                    <div className="test-cases-list">
                      {question.testCases.map((testCase, testCaseIndex) => (
                        <div className="test-case-card" key={testCaseIndex}>
                          <div className="test-case-card-header">
                            <strong>Test Case {testCaseIndex + 1}</strong>

                            {question.testCases.length > 1 && (
                              <button
                                type="button"
                                className="remove-test-case-button"
                                onClick={() =>
                                  removeTestCase(index, testCaseIndex)
                                }
                              >
                                Remove
                              </button>
                            )}
                          </div>

                          <div className="test-case-grid">
                            <div className="teacher-form-group">
                              <label>Input</label>

                              <textarea
                                rows="5"
                                value={testCase.input}
                                onChange={(event) =>
                                  handleTestCaseChange(
                                    index,
                                    testCaseIndex,
                                    "input",
                                    event.target.value,
                                  )
                                }
                                placeholder={`Example:

5
1 2 3 4 5`}
                              />
                            </div>

                            <div className="teacher-form-group">
                              <label>Expected Output</label>

                              <textarea
                                rows="5"
                                value={testCase.expectedOutput}
                                onChange={(event) =>
                                  handleTestCaseChange(
                                    index,
                                    testCaseIndex,
                                    "expectedOutput",
                                    event.target.value,
                                  )
                                }
                                placeholder={`Example:

15`}
                                required
                              />
                            </div>
                          </div>
                        </div>
                      ))}
                    </div>

                    <button
                      type="button"
                      className="add-test-case-button"
                      onClick={() => addTestCase(index)}
                      disabled={question.testCases.length >= 10}
                    >
                      {question.testCases.length >= 10
                        ? "Maximum 10 Test Cases"
                        : "+ Add Test Case"}
                    </button>
                  </div>
                </div>
              ))}
            </div>

            <button
              type="button"
              className="add-question-button"
              onClick={addQuestion}
            >
              + Add Another Question
            </button>
          </section>

          <div className="create-test-actions">
            <button
              type="button"
              className="cancel-create-button"
              onClick={() => navigate("/teacher")}
            >
              Cancel
            </button>

            <button
              type="submit"
              className="save-test-button"
              disabled={submitting}
            >
              {submitting ? "Creating Test..." : "Create Test"}
            </button>
          </div>
        </form>
      </main>
    </div>
  );
}

export default CreateTestPage;
