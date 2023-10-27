package dao;

import domain.Submission;

import java.util.List;

public interface SubmissionDaoInterface {
    boolean doesSubmissionExist(Submission submission);

    boolean insertSubmission(Submission submission);

    boolean updateSubmission(Submission submission);

    boolean deleteSubmission(Submission submission);

    boolean reorderSubmissions(int assignmentID, int userID);

    List<Submission> getSubmissionsOfOneAssignmentAndStudent(int assignmentID, int userID);

    Submission getSubmissionFromTwoIDsAndOrder(int assignmentID, int userID, int order);
}
