import { useCallback, useEffect, useState } from "react";

export default function useExamProtection({ maxWarnings = 3, onAutoSubmit }) {
  const [warningCount, setWarningCount] = useState(0);

  const [showViolationModal, setShowViolationModal] = useState(false);

  const [violationReason, setViolationReason] = useState("");

  const [isFullscreen, setIsFullscreen] = useState(
    !!document.fullscreenElement,
  );

  const addWarning = useCallback(
    (reason) => {
      setViolationReason(reason);

      setShowViolationModal(true);

      setWarningCount((previous) => {
        const next = previous + 1;

        if (next >= maxWarnings) {
          if (onAutoSubmit) {
            onAutoSubmit();
          }
        }

        return next;
      });
    },
    [maxWarnings, onAutoSubmit],
  );
  const continueExam = async () => {
    setShowViolationModal(false);

    await requestFullscreen();
  };

  const requestFullscreen = async () => {
    try {
      if (!document.fullscreenElement) {
        await document.documentElement.requestFullscreen();
      }
    } catch (error) {
      console.error(error);
    }
  };

  useEffect(() => {
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
  }, [addWarning]);

  useEffect(() => {
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
  }, [addWarning]);
  useEffect(() => {
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

      // F12
      if (event.key === "F12") {
        event.preventDefault();

        addWarning("Developer tools are not allowed.");

        return;
      }

      // Ctrl + C
      if (event.ctrlKey && key === "c") {
        event.preventDefault();

        addWarning("Copy is not allowed.");

        return;
      }

      // Ctrl + V
      if (event.ctrlKey && key === "v") {
        event.preventDefault();

        addWarning("Paste is not allowed.");

        return;
      }

      // Ctrl + X
      if (event.ctrlKey && key === "x") {
        event.preventDefault();

        addWarning("Cut is not allowed.");

        return;
      }

      // Ctrl + A
      if (event.ctrlKey && key === "a") {
        event.preventDefault();

        addWarning("Select All is disabled.");

        return;
      }

      // Ctrl + Shift + I
      if (event.ctrlKey && event.shiftKey && key === "i") {
        event.preventDefault();

        addWarning("Developer tools are not allowed.");

        return;
      }

      // Ctrl + Shift + J
      if (event.ctrlKey && event.shiftKey && key === "j") {
        event.preventDefault();

        addWarning("Developer tools are not allowed.");

        return;
      }

      // Ctrl + U
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
  }, [addWarning]);

  return {
    warningCount,

    requestFullscreen,

    continueExam,

    showViolationModal,

    violationReason,

    maxWarnings,
  };
}
