package dao;

import domain.Assignment;
import domain.User;

public interface AssignmentDaoInterface {
    boolean doesAssignmentExist(int assignmentID);

    void updateAssignment(String assignmentText, Assignment assignment, User user);

    boolean deleteAssignment(int assignmentID);
}
