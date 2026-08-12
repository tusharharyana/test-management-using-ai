import axiosInstance from "./axiosInstance";

// Teacher - Export test results as Excel
export const exportTestResults = async (testId) => {
  const response = await axiosInstance.get(
    `/tests/${testId}/export`,
    {
      responseType: "blob",
    },
  );

  return response.data;
};


// Student - Download submitted test as PDF
export const downloadMyTest = async (attemptId) => {
  const response = await axiosInstance.get(
    `/tests/attempt/${attemptId}`,
    {
      responseType: "blob",
    },
  );

  return response.data;
};