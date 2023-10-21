package dao;

import domain.Assignment;
import domain.Course;
import domain.User;

import java.util.List;

public interface AssignmentDaoInterface {
    boolean doesAssignmentExist(int assignmentID);

    void insertAssignment(String assignmentName, int courseID, String assignmentContent);

    void updateAssignment(String assignmentName, int courseID, String assignmentContent);

    boolean deleteAssignment(String assignmentName);

    List<String> getAssignmentNameByCourseID(int courseID);

    public Assignment getAssignmentByAssignmentName(String assignmentName);
}
