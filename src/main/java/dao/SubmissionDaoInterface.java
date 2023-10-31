package dao;

import domain.Assignment;
import domain.Submission;

import java.util.List;

public interface SubmissionDaoInterface {
    boolean doesSubmissionExist(Submission submission);

    boolean insertSubmission(Submission submission);

    boolean updateSubmission(Submission submission);

    boolean deleteSubmission(Submission submission);

    boolean reorderSubmissions(int assignmentID, int userID);

    boolean correctSubmission(Submission submission);

    List<Submission> getSubmissionsOfOneAssignmentAndStudent(int assignmentID, int userID);

    List<Submission> getSubmissionsFromAssignment(Assignment assignment);

    Submission getSubmissionFromTwoIDsAndOrder(int assignmentID, int userID, int order);
}
