package dao.impl;

import dao.AssignmentDaoInterface;
import domain.Assignment;
import domain.Course;
import lombok.extern.slf4j.Slf4j;
import manager.DatabaseConnectionManager;

import java.io.Closeable;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import domain.User;

@Slf4j
public class AssignmentDao implements AssignmentDaoInterface{

    private final DatabaseConnectionManager databaseConnectionManager;
    private final Connection conn;
    private final CourseDao courseDao = new CourseDao();

    public AssignmentDao() {
        databaseConnectionManager = new DatabaseConnectionManager();
        conn = databaseConnectionManager.getConnection();
    }

    @Override
    public boolean doesAssignmentExist(int assignmentID) {
        String query = "SELECT COUNT(*) FROM assignments WHERE assignmentID = ?";

        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setInt(1, assignmentID);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int count = resultSet.getInt(1);
                    return count > 0; // If count > 0, the assignment exists
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false; // An error occurred or no matching assignment found
    }

    @Override
    public void insertAssignment(String assignmentName, int courseID, String assignmentContent) {
        String query = "INSERT INTO assignments (ASSIGNMENTNAME, COURSEID) VALUES (?, ?)";

        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setString(1, assignmentName);
            statement.setInt(2, courseID);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateAssignment(String assignmentName, int courseID, String assignmentContent) {
        String query = "UPDATE assignments SET ASSIGNMENTNAME = ?, COURSEID = ? WHERE ASSIGNMENTNAME = ?";

        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setString(1, assignmentName);
            statement.setInt(2, courseID);
            statement.setString(3, assignmentName);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean deleteAssignment(String assignmentName) {
        String query = "DELETE FROM assignments WHERE assignmentName = ?";

        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setString(1, assignmentName);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<String> getAssignmentNameByCourseID(int courseID) {
        List<String> assignmentNames = new ArrayList<>();
        String query = "SELECT assignmentName FROM assignments WHERE courseID = ?";

        try (PreparedStatement preparedStatement = conn.prepareStatement(query)) {
            preparedStatement.setInt(1, courseID);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String assignmentName = resultSet.getString("assignmentName");
                assignmentNames.add(assignmentName);
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle the exception appropriately in your application
        }

        return assignmentNames;
    }

    public Assignment getAssignmentByAssignmentName(String assignmentName) {
        Assignment assignment = new Assignment();
        String query = "SELECT * FROM assignments WHERE ASSIGNMENTNAME = ? ";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, assignmentName);

            log.info("Executing SQL query: " + stmt);

            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                int courseId = resultSet.getInt("COURSEID");
                int assignmentID = resultSet.getInt("ASSIGNMENTID");

                assignment = Assignment.builder()
                        .assignmentName(assignmentName)
                        .courseID(courseId)
                        .assignmentID(assignmentID)
                        .build();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return assignment;
    }
}
