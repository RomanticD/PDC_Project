package domain;

import java.util.Date;

public class Submission {
    private int submissionID;
    private int studentID;
    private int assignmentID;
    private Date submissionTime;
    private String submissionFileOrLink;
    private String submissionStatus;

    public int getSubmissionID() {
        return submissionID;
    }

    public void setSubmissionID(int submissionID) {
        this.submissionID = submissionID;
    }

    public int getStudentID() {
        return studentID;
    }

    public void setStudentID(int studentID) {
        this.studentID = studentID;
    }

    public int getAssignmentID() {
        return assignmentID;
    }

    public void setAssignmentID(int assignmentID) {
        this.assignmentID = assignmentID;
    }

    public Date getSubmissionTime() {
        return submissionTime;
    }

    public void setSubmissionTime(Date submissionTime) {
        this.submissionTime = submissionTime;
    }

    public String getSubmissionFileOrLink() {
        return submissionFileOrLink;
    }

    public void setSubmissionFileOrLink(String submissionFileOrLink) {
        this.submissionFileOrLink = submissionFileOrLink;
    }

    public String getSubmissionStatus() {
        return submissionStatus;
    }

    public void setSubmissionStatus(String submissionStatus) {
        this.submissionStatus = submissionStatus;
    }
}
