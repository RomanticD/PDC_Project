package dao;

import domain.Course;
import domain.User;

public interface CourseSelectionDaoInterface {
    int[] getSelectedCourseId(User user);

    boolean checkIfUserEnrolled(User user, Course course);

    boolean reenrolled(User user, Course course);

    boolean newUserEnrolledRecord(User user, Course course);

    boolean selectionStatusEqualsUnselected(User user, Course course);

    boolean setUnselectedToSelectionStatus(User user, Course course);
}
