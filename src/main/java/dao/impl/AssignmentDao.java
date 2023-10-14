package dao.impl;

import dao.AssignmentDaoInterface;
import manager.DatabaseConnectionManager;
import java.sql.*;
import domain.User;

public class AssignmentDao implements AssignmentDaoInterface {

    private final DatabaseConnectionManager databaseConnectionManager;
    private final Connection conn;

    public AssignmentDao() {
        databaseConnectionManager = new DatabaseConnectionManager();
        conn = databaseConnectionManager.getConnection();
    }

    @Override
    public boolean doesAssignmentExist(String username) {
        return false;
    }

    @Override
    public User updateAssignment(User user, String newUsername) {
        return null;
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
    public void updateAssignment(String assignmentText, int userId) {
        String query = "INSERT INTO assignments (text, user_id) VALUES (?, ?)";

        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setString(1, assignmentText);
            statement.setInt(2, userId);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
