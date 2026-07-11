import axiosInstance from "./axiosInstance";

export const createSubmission = async (submissionData) => {
  const response = await axiosInstance.post("/submissions", submissionData);

  return response.data;
};

export const getSubmissionResult = async (submissionId) => {
  const response = await axiosInstance.get(
    `/submissions/${submissionId}/result`,
  );

  return response.data;
};
