package service;

import domain.Course;
import domain.User;

import java.util.Date;
import java.util.List;

public interface CourseService {
    List<Course> getAllCourses();

    List<Course> getCourseByUser(User user);

    int getCourseIDByName(String courseName);

    List<String> getCourseNames(List<Course> courseList);

    Course updateCourseNames(Course course,String newCourseName);

    Course updataCourseDescriptions(Course course,String newCourseDescription);

    Course updateInstructor(Course course,String newInstructor);

    Course updateDeadline(Course course, Date newDeadline);

    boolean newCourse(Course newcourse);

    boolean deleteCourse(Course course);

    boolean doesCourseExist(Course course);
}
