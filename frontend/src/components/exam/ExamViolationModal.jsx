import "./ExamViolationModal.css";

function ExamViolationModal({
  open,
  reason,
  warningCount,
  maxWarnings,
  onContinue,
}) {
  if (!open) return null;

  const remainingWarnings = Math.max(0, maxWarnings - warningCount);

  return (
    <div className="exam-modal-overlay">
      <div className="exam-modal">
        <div className="exam-modal-icon">⚠</div>

        <h2>Exam Security Warning</h2>

        <p className="exam-modal-reason">{reason}</p>

        <div className="exam-warning-info">
          <div>
            <strong>Warnings Used</strong>

            <span>
              {warningCount} / {maxWarnings}
            </span>
          </div>

          <div>
            <strong>Remaining</strong>

            <span>{remainingWarnings}</span>
          </div>
        </div>

        <p className="exam-modal-note">
          Leaving the examination window repeatedly will automatically submit
          your test.
        </p>

        <button className="exam-continue-btn" onClick={onContinue}>
          Continue Exam
        </button>
      </div>
    </div>
  );
}

export default ExamViolationModal;
