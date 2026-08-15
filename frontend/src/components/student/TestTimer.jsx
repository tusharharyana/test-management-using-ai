import { useEffect, useRef, useState } from "react";

function TestTimer({ expiresAt, onTimeUp }) {
  const calculateRemainingSeconds = () => {
    if (!expiresAt) {
      return 0;
    }

    const expiryTime = new Date(expiresAt).getTime();
    const currentTime = Date.now();

    return Math.max(0, Math.floor((expiryTime - currentTime) / 1000));
  };

  const [remainingSeconds, setRemainingSeconds] = useState(
    calculateRemainingSeconds,
  );

  // Important:
  // prevents onTimeUp from being called more than once
  const timeUpCalledRef = useRef(false);

  useEffect(() => {
    // Reset only when a NEW exam expiry time is received
    timeUpCalledRef.current = false;

    const updateTimer = () => {
      const remaining = calculateRemainingSeconds();

      setRemainingSeconds(remaining);

      if (remaining <= 0) {
        if (!timeUpCalledRef.current) {
          timeUpCalledRef.current = true;
          onTimeUp?.();
        }

        return true;
      }

      return false;
    };

    // Check immediately
    if (updateTimer()) {
      return;
    }

    const interval = setInterval(() => {
      const finished = updateTimer();

      if (finished) {
        clearInterval(interval);
      }
    }, 1000);

    return () => {
      clearInterval(interval);
    };
  }, [expiresAt, onTimeUp]);

  const hours = Math.floor(remainingSeconds / 3600);

  const minutes = Math.floor((remainingSeconds % 3600) / 60);

  const seconds = remainingSeconds % 60;

  const formatNumber = (number) => String(number).padStart(2, "0");

  const isWarning = remainingSeconds <= 300;

  return (
    <div className={isWarning ? "test-timer warning" : "test-timer"}>
      <span className="timer-icon">⏱</span>
      {hours > 0 && <>{formatNumber(hours)}:</>}
      {formatNumber(minutes)}:{formatNumber(seconds)}
    </div>
  );
}

export default TestTimer;
