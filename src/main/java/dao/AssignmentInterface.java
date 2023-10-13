package dao;

import domain.User;

public interface AssignmentInterface {
    boolean doesAssignmentExist(String username);

    User updateAssignment(User user, String newUsername);

    boolean doesAssignmentExist(int assignmentID);

    void updateAssignment(String assignmentText, int userId);
}
