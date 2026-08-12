import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { downloadMyTest } from "../../api/exportApi";

function SubmissionSuccessPage() {
  const navigate = useNavigate();
  const [downloading, setDownloading] = useState(false);
  const storedResult = sessionStorage.getItem("lastSubmissionResult");

  let result = null;

  try {
    result = storedResult ? JSON.parse(storedResult) : null;
  } catch {
    result = null;
  }

  const handleDownloadTest = async () => {
    if (!result?.attemptId) {
      alert("Unable to find your test attempt.");
      return;
    }

    try {
      setDownloading(true);

      const pdf = await downloadMyTest(result.attemptId);

      const url = window.URL.createObjectURL(pdf);

      const link = document.createElement("a");

      link.href = url;

      link.download = `${result.testTitle || "My_Test"}_Submission.pdf`;

      document.body.appendChild(link);

      link.click();

      link.remove();

      window.URL.revokeObjectURL(url);

    } catch (error) {
      console.error("Failed to download test:", error);

      alert("Unable to download your test. Please try again.");

    } finally {
      setDownloading(false);
    }
  };

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

        <div className="success-action-buttons">

          <button
            className="download-test-button"
            onClick={handleDownloadTest}
            disabled={downloading}
          >
            {downloading
              ? "Preparing PDF..."
              : "Download My Test"}
          </button>

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
    </div>
  );
}

export default SubmissionSuccessPage;
