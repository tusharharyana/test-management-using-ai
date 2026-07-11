package com.example.demo.dto.request;

public class OverrideMarksRequest {

    private Integer teacherScore;

    private String teacherComment;


    public OverrideMarksRequest() {
    }


    public Integer getTeacherScore() {
        return teacherScore;
    }

    public void setTeacherScore(Integer teacherScore) {
        this.teacherScore = teacherScore;
    }


    public String getTeacherComment() {
        return teacherComment;
    }

    public void setTeacherComment(String teacherComment) {
        this.teacherComment = teacherComment;
    }
}