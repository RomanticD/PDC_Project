package dao;

import domain.Course;
import domain.User;

import java.util.ArrayList;

public interface CourseSelectionDaoInterface {
    int[] getSelectedCourseId(User user);
}
