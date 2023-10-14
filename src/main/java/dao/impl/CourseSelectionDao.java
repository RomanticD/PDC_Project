package dao.impl;

import dao.CourseSelectionDaoInterface;
import manager.DatabaseConnectionManager;
import domain.Course;
import domain.User;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class CourseSelectionDao implements CourseSelectionDaoInterface {
    private final DatabaseConnectionManager databaseConnectionManager;
    private final Connection conn;

    public CourseSelectionDao() {
        databaseConnectionManager = new DatabaseConnectionManager();
        conn = databaseConnectionManager.getConnection();
    }

    @Override
    public int[] getSelectedCourseId(User user) {
        List<Integer> courseIdList = new ArrayList<>();
        String query = "SELECT COURSE_ID FROM COURSE_SELECTION WHERE USER_ID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, user.getUserId());
            log.info("Executing SQL query: " + stmt);
            ResultSet resultSet = stmt.executeQuery();
            while (resultSet.next()) {
                courseIdList.add(resultSet.getInt("COURSE_ID"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        int[] courseIds = new int[courseIdList.size()];
        for (int i = 0; i < courseIdList.size(); i++) {
            courseIds[i] = courseIdList.get(i);
        }
        return courseIds;
    }

    @Override
    public boolean checkIfUserEnrolled(User user, Course course) {
        String query = "SELECT * FROM COURSE_SELECTION WHERE USER_ID = ? AND COURSE_ID = ? AND SELECTION_STATUS = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, user.getUserId());
            stmt.setInt(2, course.getCourseID());
            stmt.setString(3, "Selected");
            log.info("Executing SQL query: " + stmt);
            ResultSet resultSet = stmt.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean reenrolled(User user, Course course) {
        String query = "UPDATE COURSE_SELECTION SET SELECTION_STATUS = ? WHERE USER_ID = ? AND COURSE_ID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, "Selected");
            stmt.setInt(2, user.getUserId());
            stmt.setInt(3, course.getCourseID());
            log.info("Executing SQL query: " + stmt);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            log.error("Error occurred while reenrolling user. Details: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean newUserEnrolledRecord(User user, Course course) {
        String query = "INSERT INTO COURSE_SELECTION (USER_ID, COURSE_ID, SELECTION_STATUS) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, user.getUserId());
            stmt.setInt(2, course.getCourseID());
            stmt.setString(3, "Selected");
            log.info("Executing SQL query: " + stmt);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            log.error("Error occurred while creating new enrolled record. Details: " + e.getMessage());
        }
        return false;
    }


    @Override
    public boolean selectionStatusEqualsUnselected(User user, Course course) {
        String query = "SELECT * FROM COURSE_SELECTION WHERE USER_ID = ? AND COURSE_ID = ? AND SELECTION_STATUS = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, user.getUserId());
            stmt.setInt(2, course.getCourseID());
            stmt.setString(3, "Unselected");
            log.info("Executing SQL query: " + stmt);
            ResultSet resultSet = stmt.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            log.error("Error occurred while get user selection status. Details: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean setUnselectedToSelectionStatus(User user, Course course) {
        String query = "UPDATE COURSE_SELECTION SET SELECTION_STATUS = ? WHERE USER_ID = ? AND COURSE_ID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, "Unselected");
            stmt.setInt(2, user.getUserId());
            stmt.setInt(3, course.getCourseID());
            log.info("Executing SQL query: " + stmt);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            log.error("Error occurred while setting selection status to Unselected. Details: " + e.getMessage());
        }
        return false;
    }

}

