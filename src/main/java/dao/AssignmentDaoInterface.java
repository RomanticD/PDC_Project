package dao;

import domain.User;

public interface AssignmentDaoInterface {
    boolean doesAssignmentSubmissionExist(int assignmentID);

    void updateAssignmentSubmission(String assignmentText, int assignmentID, User user);

    boolean deleteAssignmentSubmission(int assignmentID);
}
