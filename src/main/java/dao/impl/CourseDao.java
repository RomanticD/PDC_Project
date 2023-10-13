package dao.impl;

import dao.CourseDaoInterface;
import db.DatabaseConnectionManager;
import domain.Course;
import domain.User;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
public class CourseDao implements CourseDaoInterface {
    private final DatabaseConnectionManager databaseConnectionManager;
    private final Connection conn;

    public CourseDao() {
        databaseConnectionManager = new DatabaseConnectionManager();
        conn = databaseConnectionManager.getConnection();
    }

    public List<Course> getAllCourses() {
        List<Course> courses = new ArrayList<>();
        String query = "SELECT * FROM COURSES";
        try (PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet resultSet = stmt.executeQuery()) {
            while (resultSet.next()) {
                int courseId = resultSet.getInt("COURSEID");
                String courseName = resultSet.getString("COURSENAME");
                String courseDescription = resultSet.getString("COURSEDESCRIPTION");
                String instructor = resultSet.getString("INSTRUCTOR");
                Date deadLine = resultSet.getDate("DEADLINE");

                Course course = Course.builder()
                        .courseID(courseId)
                        .courseName(courseName)
                        .courseDescription(courseDescription)
                        .instructor(instructor)
                        .deadLine(deadLine)
                        .build();

                courses.add(course);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return courses;
    }

    @Override
    public List<Course> getCourseByUser(User user) {
        List<Course> courses = new ArrayList<>();
        String query = "SELECT * FROM COURSES " +
                "INNER JOIN COURSE_SELECTION ON COURSES.COURSEID = COURSE_SELECTION.COURSE_ID " +
                "WHERE COURSE_SELECTION.USER_ID = ? " +
                "AND COURSE_SELECTION.SELECTION_STATUS = 'Selected'";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, user.getUserId());
            ResultSet resultSet = stmt.executeQuery();
            while (resultSet.next()) {
                int courseId = resultSet.getInt("COURSEID");
                String courseName = resultSet.getString("COURSENAME");
                String courseDescription = resultSet.getString("COURSEDESCRIPTION");
                String instructor = resultSet.getString("INSTRUCTOR");
                Date deadLine = resultSet.getDate("DEADLINE");

                Course course = Course.builder()
                        .courseID(courseId)
                        .courseName(courseName)
                        .courseDescription(courseDescription)
                        .instructor(instructor)
                        .deadLine(deadLine)
                        .build();

                courses.add(course);
            }

            if (courses.isEmpty()){
                log.warn( user.getName()+ " has no course selected!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return courses;
    }
}

