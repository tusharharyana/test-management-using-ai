import { useNavigate } from "react-router-dom";

function SubmissionSuccessPage() {
  const navigate = useNavigate();

  const storedResult = sessionStorage.getItem("lastSubmissionResult");

  let result = null;

  try {
    result = storedResult ? JSON.parse(storedResult) : null;
  } catch {
    result = null;
  }

  return (
    <div className="success-page">
      <div className="success-card">
        <div className="success-icon">✓</div>

        <h1>Test Submitted Successfully</h1>

        <p>Your code has been submitted and sent for AI evaluation.</p>

        {result && (
          <div className="success-details">
            <div>
              <span>Test</span>

              <strong>{result.testTitle}</strong>
            </div>

            <div>
              <span>Attempt ID</span>

              <strong>#{result.attemptId}</strong>
            </div>

            <div>
              <span>Solutions Submitted</span>

              <strong>{result.submissionIds?.length || 0}</strong>
            </div>
          </div>
        )}

        {result?.autoSubmitted && (
          <div className="auto-submit-notice">
            Your test was automatically submitted because the time expired.
          </div>
        )}

        <button
          className="return-home-button"
          onClick={() => {
            sessionStorage.removeItem("lastSubmissionResult");

            navigate("/");
          }}
        >
          Return to Home
        </button>
      </div>
    </div>
  );
}

export default SubmissionSuccessPage;
