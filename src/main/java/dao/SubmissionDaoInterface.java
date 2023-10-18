package dao;

import domain.Assignment;
import domain.User;

public interface SubmissionDaoInterface {
    boolean doesSubmissionExist(int assignmentID, int userID);

    void insertSubmission(String assignmentText, Assignment assignment, User user);

    void updateSubmission(String assignmentText, Assignment assignment, User user);

    boolean deleteSubmission(int assignmentID, int userID);
}
