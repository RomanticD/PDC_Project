package dao.impl;

import dao.CourseDaoInterface;
import db.DatabaseConnectionManager;
import domain.Course;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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

                Course course = new Course(courseId, courseName, courseDescription, instructor);
                courses.add(course);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return courses;
    }
}

