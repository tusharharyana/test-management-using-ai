import { useEffect, useState } from "react";

function TestTimer({ expiresAt, onTimeUp }) {
  const calculateRemainingSeconds = () => {
    if (!expiresAt) {
      return 0;
    }

    const expiryTime = new Date(expiresAt).getTime();

    const currentTime = new Date().getTime();

    return Math.max(0, Math.floor((expiryTime - currentTime) / 1000));
  };

  const [remainingSeconds, setRemainingSeconds] = useState(
    calculateRemainingSeconds,
  );

  useEffect(() => {
    const interval = setInterval(() => {
      const remaining = calculateRemainingSeconds();

      setRemainingSeconds(remaining);

      if (remaining <= 0) {
        clearInterval(interval);

        onTimeUp?.();
      }
    }, 1000);

    return () => clearInterval(interval);
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
