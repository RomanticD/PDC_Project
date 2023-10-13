package dao;

import domain.Course;
import domain.User;

import java.util.List;

public interface CourseDaoInterface {
    List<Course> getAllCourses();

    List<Course> getCourseByUser(User user);
}
