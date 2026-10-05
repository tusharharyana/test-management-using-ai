import axiosInstance from "./axiosInstance";

export const getAiEvaluationStatus = async () => {
    const response = await axiosInstance.get(
        `/system-settings/ai-evaluation`
    );

    return response.data;
};

export const updateAiEvaluationStatus = async (enabled) => {
    const response = await axiosInstance.put(
        `/system-settings/ai-evaluation`,
        {
            enabled,
        }
    );

    return response.data;
};