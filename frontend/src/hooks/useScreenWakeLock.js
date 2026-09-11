import { useEffect, useRef } from "react";

function useScreenWakeLock(isActive) {
    const wakeLockRef = useRef(null);

    useEffect(() => {
        if (!isActive) {
            return;
        }

        let cancelled = false;

        const requestWakeLock = async () => {
            if (!("wakeLock" in navigator)) {
                console.warn("Screen Wake Lock API is not supported.");
                return;
            }

            try {
                const wakeLock = await navigator.wakeLock.request("screen");

                if (cancelled) {
                    await wakeLock.release();
                    return;
                }

                wakeLockRef.current = wakeLock;

                wakeLock.addEventListener("release", () => {
                    wakeLockRef.current = null;
                });
            } catch (error) {
                console.warn("Unable to acquire screen wake lock:", error);
            }
        };

        const handleVisibilityChange = () => {
            if (
                document.visibilityState === "visible" &&
                !wakeLockRef.current &&
                !cancelled
            ) {
                requestWakeLock();
            }
        };

        requestWakeLock();

        document.addEventListener(
            "visibilitychange",
            handleVisibilityChange
        );

        return () => {
            cancelled = true;

            document.removeEventListener(
                "visibilitychange",
                handleVisibilityChange
            );

            if (wakeLockRef.current) {
                wakeLockRef.current.release().catch(() => { });
                wakeLockRef.current = null;
            }
        };
    }, [isActive]);
}

export default useScreenWakeLock;