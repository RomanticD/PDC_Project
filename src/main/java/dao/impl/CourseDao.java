package dao.impl;

import dao.CourseDaoInterface;
import manager.DatabaseConnectionManager;
import domain.Course;
import domain.User;
import lombok.extern.slf4j.Slf4j;

import java.io.Closeable;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Slf4j
public class CourseDao implements CourseDaoInterface, Closeable {
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

            log.info("Executing SQL query: " + stmt);

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

            log.info("Executing SQL query: " + stmt);

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

    @Override
    public int getCourseIDByName(String courseName) {
        int courseID = -1;
        String query = "SELECT courseID FROM courses WHERE courseName = ?";

        try (PreparedStatement preparedStatement = conn.prepareStatement(query)) {
            preparedStatement.setString(1, courseName);
            log.info("Executing SQL query: " + preparedStatement);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                courseID = resultSet.getInt("courseID");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return courseID;
    }

    public List<String> getCourseNames(List<Course> courseList){
        List<String> courseNames = new ArrayList<>();

        for (Course course : courseList) {
            courseNames.add(course.getCourseName());
        }

        return courseNames;
    }

    @Override
    public Course updateCourseNames(Course course, String newCourseName) {
        String sql = "UPDATE courses SET COURSENAME = ? WHERE COUESEID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newCourseName);
            ps.setInt(2, course.getCourseID());
            log.info("Executing SQL query: " + ps);
            int updatedRows = ps.executeUpdate();

            if (updatedRows > 0) {
                course.setCourseName(newCourseName);
                return course;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public Course updataCourseDescriptions(Course course, String newCourseDescription) {
        String sql = "UPDATE courses SET COURSEDESCRIPTION = ? WHERE COUESEID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newCourseDescription);
            ps.setInt(2, course.getCourseID());
            log.info("Executing SQL query: " + ps);
            int updatedRows = ps.executeUpdate();

            if (updatedRows > 0) {
                course.setCourseDescription(newCourseDescription);
                return course;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public Course updateInstructor(Course course, String newInstructor) {
        String sql = "UPDATE courses SET INSTRUCTOR = ? WHERE COUESEID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newInstructor);
            ps.setInt(2,course.getCourseID());
            log.info("Executing SQL query: " + ps);
            int updatedRows = ps.executeUpdate();

            if (updatedRows > 0) {
                course.setInstructor(newInstructor);
                return course;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public Course updateDeadline(Course course, Date newDeadline) {
        String sql = "UPDATE courses SET DEADLINE = ? WHERE COUESEID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, (java.sql.Date) newDeadline);
            ps.setInt(2, course.getCourseID());
            log.info("Executing SQL query: " + ps);
            int updatedRows = ps.executeUpdate();

            if (updatedRows > 0) {
                course.setDeadLine(newDeadline);
                return course;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean newCourse(Course newcourse) {
        String sql = "insert into course (COURSEID,COURSENAME,COURSEDESCRIPTON,INSTRUCTOR,DEADLINE) values (?,?,?,?,?)";
        PreparedStatement ps = null;

        try {
            ps = conn.prepareStatement(sql);
            ps.setInt(1, newcourse.getCourseID());
            ps.setString(2, newcourse.getCourseName());
            ps.setString(3, newcourse.getCourseDescription());
            ps.setString(4, newcourse.getInstructor());
            ps.setDate(5, (java.sql.Date) newcourse.getDeadLine());
            log.info("Executing SQL query: " + ps);
            ps.execute();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return true;
    }

    @Override
    public boolean deleteCourse(Course course) {
        if(!doesCourseExist(course)){
            return false;
        }

        String query = "DELETE FROM course WHERE COURSEID = ? AND COURSENAME";

        try (PreparedStatement preparedStatement = conn.prepareStatement(query)) {
            preparedStatement.setInt(1, course.getCourseID());
            preparedStatement.setString(2, course.getCourseName());
            preparedStatement.executeUpdate();

            log.info("Executing SQL query: deleteCourse");

        } catch (SQLException e) {
            log.error("Error when deleteCourse: "  + e.getMessage());
            return false;
        }
        return true;
    }

    @Override
    public boolean doesCourseExist(Course course) {
        String query = "SELECT COUNT(*) FROM course WHERE COURSEID = ? OR COURSENAME = ?";

        try (PreparedStatement preparedStatement = conn.prepareStatement(query)) {
            preparedStatement.setInt(1, course.getCourseID());
            preparedStatement.setString(2, course.getCourseName());
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    int count = resultSet.getInt(1);
                    return count > 0; // If count == 0, the assignment doesn't exist
                }
            }

            log.info("Executing SQL query: doesCourseExist");

        } catch (SQLException e) {
            log.error("Error when checkIfCourseExists: "  + e.getMessage());
        }

        return false;
    }

    @Override
    public int FindMinUnusedCourseID() {
        String query = "SELECT COURSEID FROM COURSE";
        int muID = 0;
        try{
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery(query);

            Set<Integer> existingCourseIDs = new HashSet<>();
            while (resultSet.next()) {
                existingCourseIDs.add(resultSet.getInt("COURSEID"));
            }
            while (existingCourseIDs.contains(muID)) {
                muID++;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return muID;
    }

    @Override
    public void close() throws IOException {
        try {
            conn.close();
        } catch (SQLException e) {
            log.error("Error when closing connection");
            throw new IOException(e);
        }
    }
}

