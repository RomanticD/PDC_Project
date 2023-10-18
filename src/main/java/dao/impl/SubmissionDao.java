package dao.impl;

import dao.SubmissionDaoInterface;
import db.DatabaseConnectionManager;
import domain.Assignment;
import domain.User;

import java.sql.*;

public class SubmissionDao implements SubmissionDaoInterface {

    private final Connection conn;

    public SubmissionDao() {
        DatabaseConnectionManager databaseConnectionManager = new DatabaseConnectionManager();
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
    public void updateSubmission(String assignmentText, Assignment assignment, User user) {
        if(doesSubmissionExist(assignment.getAssignmentID(), user.getUserID())){
            String query = "UPDATE submissions SET SUBMISSIONFILEORLINK = ?, SUBMISSIONTIME = CURRENT_TIMESTAMP WHERE assignmentID = ?";

            try (PreparedStatement statement = conn.prepareStatement(query)) {
                statement.setString(1, assignmentText);
                statement.setInt(2, assignment.getAssignmentID());
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            String query = "INSERT INTO submissions (SUBMISSIONFILEORLINK, assignmentID, studentID, SUBMISSIONTIME) VALUES (?, ?, ?, CURRENT_TIMESTAMP)";

            try (PreparedStatement statement = conn.prepareStatement(query)) {
                statement.setString(1, assignmentText);
                statement.setInt(2, assignment.getAssignmentID());
                statement.setInt(3, user.getUserID());
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
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
}
