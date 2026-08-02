import axiosInstance from "./axiosInstance";

export const exportTestResults = async (testId) => {
  const response = await axiosInstance.get(`/tests/${testId}/export`, {
    responseType: "blob",
  });

  return response.data;
};
