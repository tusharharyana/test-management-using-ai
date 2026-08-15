import { useCallback, useEffect, useRef, useState } from "react";

export default function useExamProtection({
  maxWarnings = 3,
  onAutoSubmit,
  isExamActive = true,
}) {
  const [warningCount, setWarningCount] = useState(0);

  const [showViolationModal, setShowViolationModal] = useState(false);

  const [violationReason, setViolationReason] = useState("");

  const [isFullscreen, setIsFullscreen] = useState(
    !!document.fullscreenElement,
  );

  // Prevent multiple auto-submit calls
  const autoSubmitTriggeredRef = useRef(false);

  const addWarning = useCallback(
    (reason) => {
      // Exam has already ended
      if (!isExamActive) {
        return;
      }

      // Auto-submit has already been triggered
      if (autoSubmitTriggeredRef.current) {
        return;
      }

      setViolationReason(reason);
      setShowViolationModal(true);

      setWarningCount((previous) => {
        // Never allow warning count to go beyond maxWarnings
        if (previous >= maxWarnings) {
          return previous;
        }

        const next = previous + 1;

        if (next >= maxWarnings) {
          autoSubmitTriggeredRef.current = true;

          setShowViolationModal(false);

          if (onAutoSubmit) {
            onAutoSubmit();
          }
        }

        return next;
      });
    },
    [maxWarnings, onAutoSubmit, isExamActive],
  );

  const continueExam = async () => {
    if (!isExamActive) {
      return;
    }

    setShowViolationModal(false);

    await requestFullscreen();
  };

  const requestFullscreen = async () => {
    if (!isExamActive) {
      return;
    }

    try {
      if (!document.fullscreenElement) {
        await document.documentElement.requestFullscreen();
      }
    } catch (error) {
      console.error("Fullscreen request failed:", error);
    }
  };

  // =========================
  // Blur / Tab Switching
  // =========================

  useEffect(() => {
    if (!isExamActive) {
      return;
    }

    const handleBlur = () => {
      addWarning("You left the exam window.");
    };

    const handleVisibility = () => {
      if (document.hidden) {
        addWarning("Tab switching detected.");
      }
    };

    window.addEventListener("blur", handleBlur);

    document.addEventListener("visibilitychange", handleVisibility);

    return () => {
      window.removeEventListener("blur", handleBlur);

      document.removeEventListener("visibilitychange", handleVisibility);
    };
  }, [addWarning, isExamActive]);

  // =========================
  // Fullscreen
  // =========================

  useEffect(() => {
    if (!isExamActive) {
      return;
    }

    const handleFullscreenChange = () => {
      const fullscreen = !!document.fullscreenElement;

      setIsFullscreen(fullscreen);

      if (!fullscreen) {
        addWarning("Exited Fullscreen");
      }
    };

    document.addEventListener("fullscreenchange", handleFullscreenChange);

    return () => {
      document.removeEventListener("fullscreenchange", handleFullscreenChange);
    };
  }, [addWarning, isExamActive]);

  // =========================
  // Context Menu / Copy / Keys
  // =========================

  useEffect(() => {
    if (!isExamActive) {
      return;
    }

    const handleContextMenu = (event) => {
      event.preventDefault();
    };

    const handleCopy = (event) => {
      event.preventDefault();
      addWarning("Copy is not allowed.");
    };

    const handleCut = (event) => {
      event.preventDefault();
      addWarning("Cut is not allowed.");
    };

    const handlePaste = (event) => {
      event.preventDefault();
      addWarning("Paste is not allowed.");
    };

    const handleKeyDown = (event) => {
      const key = event.key.toLowerCase();

      if (event.key === "F12") {
        event.preventDefault();

        addWarning("Developer tools are not allowed.");

        return;
      }

      if (event.ctrlKey && key === "c") {
        event.preventDefault();

        addWarning("Copy is not allowed.");

        return;
      }

      if (event.ctrlKey && key === "v") {
        event.preventDefault();

        addWarning("Paste is not allowed.");

        return;
      }

      if (event.ctrlKey && key === "x") {
        event.preventDefault();

        addWarning("Cut is not allowed.");

        return;
      }

      if (event.ctrlKey && key === "a") {
        event.preventDefault();

        addWarning("Select All is disabled.");

        return;
      }

      if (event.ctrlKey && event.shiftKey && key === "i") {
        event.preventDefault();

        addWarning("Developer tools are not allowed.");

        return;
      }

      if (event.ctrlKey && event.shiftKey && key === "j") {
        event.preventDefault();

        addWarning("Developer tools are not allowed.");

        return;
      }

      if (event.ctrlKey && key === "u") {
        event.preventDefault();

        addWarning("View Source is disabled.");

        return;
      }
    };

    document.addEventListener("contextmenu", handleContextMenu);

    document.addEventListener("copy", handleCopy);

    document.addEventListener("cut", handleCut);

    document.addEventListener("paste", handlePaste);

    document.addEventListener("keydown", handleKeyDown);

    return () => {
      document.removeEventListener("contextmenu", handleContextMenu);

      document.removeEventListener("copy", handleCopy);

      document.removeEventListener("cut", handleCut);

      document.removeEventListener("paste", handlePaste);

      document.removeEventListener("keydown", handleKeyDown);
    };
  }, [addWarning, isExamActive]);

  return {
    warningCount,

    requestFullscreen,

    continueExam,

    showViolationModal,

    violationReason,

    maxWarnings,

    isFullscreen,
  };
}
