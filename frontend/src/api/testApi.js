import axiosInstance from "./axiosInstance";

export const createTest = async (testData) => {
  const response = await axiosInstance.post("/tests", testData);

  return response.data;
};

export const getAllTests = async () => {
  const response = await axiosInstance.get("/tests");

  return response.data;
};

export const getTestById = async (testId) => {
  const response = await axiosInstance.get(`/tests/${testId}`);

  return response.data;
};

export const activateTest = async (testId) => {
  const response = await axiosInstance.patch(`/tests/${testId}/activate`);

  return response.data;
};

export const completeTest = async (testId) => {
  const response = await axiosInstance.patch(`/tests/${testId}/complete`);

  return response.data;
};
