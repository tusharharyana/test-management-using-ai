function QuestionPanel({ question, currentIndex, totalQuestions }) {
  if (!question) {
    return (
      <div className="question-panel">
        <p>No question available.</p>
      </div>
    );
  }

  return (
    <div className="question-panel">
      <div className="question-number">
        Question {currentIndex + 1} of {totalQuestions}
      </div>

      <h1 className="question-title">{question.title}</h1>

      <div className="question-marks">{question.maxMarks} Marks</div>

      <section className="question-section">
        <h3>Problem Statement</h3>

        <p className="problem-statement">{question.problemStatement}</p>
      </section>

      {question.examples && (
        <section className="question-section">
          <h3>Example</h3>

          <pre className="example-box">{question.examples}</pre>
        </section>
      )}
    </div>
  );
}

export default QuestionPanel;
