package com.example.demo.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class StartTestRequest {

    @NotBlank(message = "Student name is required")
    @Size(
            min = 2,
            max = 100,
            message = "Student name must be between 2 and 100 characters"
    )
    private String studentName;


    @NotBlank(message = "Student UID is required")
    @Size(
            max = 50,
            message = "Student UID cannot exceed 50 characters"
    )
    private String studentUid;


    @NotBlank(message = "Access code is required")
    private String accessCode;


    public StartTestRequest() {
    }


    public String getStudentName() {
        return studentName;
    }


    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }


    public String getStudentUid() {
        return studentUid;
    }


    public void setStudentUid(String studentUid) {
        this.studentUid = studentUid;
    }


    public String getAccessCode() {
        return accessCode;
    }


    public void setAccessCode(String accessCode) {
        this.accessCode = accessCode;
    }
}