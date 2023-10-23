package dao.impl;

import dao.SubmissionDaoInterface;
import domain.Assignment;
import domain.User;
import lombok.extern.slf4j.Slf4j;
import manager.DatabaseConnectionManager;

import java.io.Closeable;
import java.io.IOException;
import java.sql.*;

@Slf4j
public class SubmissionDao implements SubmissionDaoInterface, Closeable{

    private final DatabaseConnectionManager databaseConnectionManager;
    private final Connection conn;

    public SubmissionDao() {
        databaseConnectionManager = new DatabaseConnectionManager();
        conn = databaseConnectionManager.getConnection();
    }

    @Override
    public boolean doesSubmissionExist(int assignmentID, int userID) {
        String query = "SELECT COUNT(*) FROM submissions WHERE assignmentID = ?";

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
    public void insertSubmission(String submissionText, Assignment assignment, User user) {
        String query = "INSERT INTO submissions (SUBMISSIONFILEORLINK, assignmentID, studentID, SUBMISSIONTIME) VALUES (?, ?, ?, CURRENT_TIMESTAMP)";

        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setString(1, submissionText);
            statement.setInt(2, assignment.getAssignmentID());
            statement.setInt(3, user.getUserId());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void updateSubmission(String submissionText, Assignment assignment, User user) {
        String query = "UPDATE submissions SET SUBMISSIONFILEORLINK = ?, SUBMISSIONTIME = CURRENT_TIMESTAMP WHERE assignmentID = ?";

        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setString(1, submissionText);
            statement.setInt(2, assignment.getAssignmentID());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean deleteSubmission(int assignmentID, int userID) {
        String query = "DELETE FROM submissions WHERE assignmentID = ? AND studentID = ?";

        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setInt(1, assignmentID);
            statement.setInt(2, userID);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
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
