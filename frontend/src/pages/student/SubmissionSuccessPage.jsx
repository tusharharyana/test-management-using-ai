import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { downloadMyTest, getEvaluationStatus } from "../../api/exportApi";
function SubmissionSuccessPage() {
  const navigate = useNavigate();
  const [downloading, setDownloading] = useState(false);
  const [checkingEvaluation, setCheckingEvaluation] = useState(false);
  const [evaluationStatus, setEvaluationStatus] = useState("IN_PROGRESS");
  const [evaluationProgress, setEvaluationProgress] = useState(null);
  const storedResult = sessionStorage.getItem("lastSubmissionResult");

  useEffect(() => {
    if (document.fullscreenElement) {
      document.exitFullscreen().catch(() => {});
    }
  }, []);

  let result = null;

  try {
    result = storedResult ? JSON.parse(storedResult) : null;
  } catch {
    result = null;
  }

  const handleCheckEvaluation = async () => {
    if (!result?.attemptId) {
      alert("Unable to find your test attempt.");
      return;
    }

    try {
      setCheckingEvaluation(true);

      const status = await getEvaluationStatus(result.attemptId);

      setEvaluationStatus(status.status);

      setEvaluationProgress(status);
    } catch (error) {
      console.error("Failed to check evaluation status:", error);

      alert("Unable to check evaluation status. Please try again.");
    } finally {
      setCheckingEvaluation(false);
    }
  };

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
    <div className="submission-success-page">
      <div className="submission-success-card">
        {/* Top status action */}
        <div className="submission-success-topbar">
          <button
            className="submission-success-check-btn"
            onClick={handleCheckEvaluation}
            disabled={checkingEvaluation || evaluationStatus === "COMPLETED"}
          >
            {checkingEvaluation
              ? "Checking..."
              : evaluationStatus === "COMPLETED"
                ? "✓ Evaluation Completed"
                : "↻ Check Status"}
          </button>
        </div>

        {/* Main content */}
        <div className="submission-success-content">
          <div className="submission-success-icon">✓</div>

          <h1>Test Submitted Successfully</h1>

          <p>
            {evaluationStatus === "COMPLETED"
              ? "Your results are ready."
              : "Your test has been submitted. AI evaluation is in progress."}
          </p>

          {/* Evaluation status */}
          <div
            className={`submission-success-status ${
              evaluationStatus === "COMPLETED"
                ? "submission-success-status-completed"
                : evaluationStatus === "FAILED"
                  ? "submission-success-status-failed"
                  : "submission-success-status-pending"
            }`}
          >
            {evaluationStatus === "COMPLETED" && <>✓ AI Evaluation Completed</>}

            {evaluationStatus === "IN_PROGRESS" && (
              <>⏳ AI Evaluation in Progress</>
            )}

            {evaluationStatus === "FAILED" && <>⚠ AI Evaluation Failed</>}
          </div>
        </div>

        {/* Bottom actions */}
        <div className="submission-success-actions">
          <button
            className="submission-success-download-btn"
            onClick={handleDownloadTest}
            disabled={downloading || evaluationStatus !== "COMPLETED"}
          >
            {downloading ? "Preparing PDF..." : "Download My Test"}
          </button>

          <button
            className="submission-success-home-btn"
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
