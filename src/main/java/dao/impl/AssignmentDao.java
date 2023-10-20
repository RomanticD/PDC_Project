package dao.impl;

import dao.AssignmentDaoInterface;
import domain.Assignment;
import domain.Course;
import lombok.extern.slf4j.Slf4j;
import manager.DatabaseConnectionManager;

import java.io.Closeable;
import java.io.IOException;
import java.sql.*;
import domain.User;

@Slf4j
public class AssignmentDao implements AssignmentDaoInterface{

    private final DatabaseConnectionManager databaseConnectionManager;
    private final Connection conn;

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
}
