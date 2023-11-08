package service.dao;

import service.SubmissionService;
import domain.Assignment;
import domain.Submission;
import lombok.extern.slf4j.Slf4j;
import manager.DatabaseConnectionManager;

import java.io.Closeable;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class SubmissionDao implements SubmissionService, Closeable{
    private final Connection conn;

    public SubmissionDao() {
        DatabaseConnectionManager databaseConnectionManager = new DatabaseConnectionManager();
        conn = databaseConnectionManager.getConnection();
    }

    // Definition: one unique submission depends on one assignmentID, one userID and the submission order.
    @Override
    public boolean doesSubmissionExist(Submission submission) {
        String query = "SELECT COUNT(*) FROM submissions WHERE assignmentID = ? AND studentID = ? AND submissionOrder = ?";

        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setInt(1, submission.getAssignmentID());
            statement.setInt(2, submission.getStudentID());
            statement.setInt(3, submission.getSubmissionOrder());
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int count = resultSet.getInt(1);
                    return count > 0; // If count > 0, the assignment exists
                }
            }
        } catch (SQLException e) {
            log.error("Error when checkIfSubmissionExists: "  + e.getMessage());
        }

        return false; // An error occurred or no matching assignment found
    }

    @Override
    public boolean insertSubmission(Submission submission) {
        String query = "INSERT INTO submissions (SUBMISSIONCONTENT, assignmentID, studentID, submissionOrder, SUBMISSIONSTATUS, SUBMISSIONTIME) VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP)";

        List<Submission> submissions = getSubmissionsOfOneAssignmentAndStudent(submission.getAssignmentID(), submission.getStudentID());
        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setString(1, submission.getSubmissionContent());
            statement.setInt(2, submission.getAssignmentID());
            statement.setInt(3, submission.getStudentID());
            statement.setInt(4, submissions.size() + 1);
            statement.setString(5, "Not corrected yet.");
            statement.executeUpdate();
        } catch (SQLException e) {
            log.error("Error when insertSubmission: "  + e.getMessage());
            return false;
        }

        return true;
    }


    @Override
    public boolean updateSubmission(Submission submission) {
        String query = "UPDATE submissions SET SUBMISSIONCONTENT = ?, SUBMISSIONTIME = CURRENT_TIMESTAMP WHERE submissionID = ?";

        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setString(1, submission.getSubmissionContent());
            statement.setInt(2, submission.getSubmissionID());
            statement.executeUpdate();
        } catch (SQLException e) {
            log.error("Error when updateSubmission: "  + e.getMessage());
            return false;
        }

        return true;
    }

    @Override
    public boolean deleteSubmission(Submission submission) {
        if(!doesSubmissionExist(submission)){
            return false;
        }

        String query = "DELETE FROM submissions WHERE submissionID = ?";

        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setInt(1, submission.getSubmissionID());
            statement.executeUpdate();
        } catch (SQLException e) {
            log.error("Error when deleteSubmission: "  + e.getMessage());
            return false;
        }

        return reorderSubmissions(submission.getAssignmentID(), submission.getStudentID());
    }

    @Override
    public boolean reorderSubmissions(int assignmentID, int userID) {
        String query = "SELECT * FROM submissions WHERE assignmentID = ? AND studentID = ? ORDER BY submissionOrder ASC";

        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setInt(1, assignmentID);
            statement.setInt(2, userID);

            try (ResultSet resultSet = statement.executeQuery()) {
                int newSubmissionOrder = 1;
                while (resultSet.next()) {
                    int submissionID = resultSet.getInt("submissionID");
                    String updateQuery = "UPDATE submissions SET submissionOrder = ? WHERE submissionID = ?";

                    try (PreparedStatement updateStatement = conn.prepareStatement(updateQuery)) {
                        updateStatement.setInt(1, newSubmissionOrder);
                        updateStatement.setInt(2, submissionID);
                        updateStatement.executeUpdate();
                    }

                    newSubmissionOrder++;
                }
            }
        } catch (SQLException e) {
            log.error("Error when reorderSubmissions: "  + e.getMessage());
            return false;
        }

        return true;
    }

    @Override
    public boolean correctSubmission(Submission submission) {
        String query = "UPDATE submissions set evaluation = ?, scores = ?, submissionStatus = ? WHERE assignmentID = ? AND studentID = ? AND submissionOrder = ?";

        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setString(1, submission.getEvaluation());
            statement.setInt(2, submission.getScores());
            statement.setString(3, "Already corrected");
            statement.setInt(4, submission.getAssignmentID());
            statement.setInt(5, submission.getStudentID());
            statement.setInt(6, submission.getSubmissionOrder());
            statement.executeUpdate();
        } catch (SQLException e) {
            log.error("Error when correctSubmission: "  + e.getMessage());
            return false;
        }

        return true;
    }

    @Override
    public List<Submission> getSubmissionsOfOneAssignmentAndStudent(int assignmentID, int userID) {
        List<Submission> submissionList = new ArrayList<>();
        String query = "SELECT * FROM submissions WHERE assignmentID = ? AND studentID = ? ORDER BY submissionOrder ASC";

        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setInt(1, assignmentID);
            statement.setInt(2, userID);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int submissionID = resultSet.getInt("submissionID");
                    Timestamp submissionTime = resultSet.getTimestamp("submissionTIME");
                    String  submissionContent = resultSet.getString("submissionContent");
                    String submissionStatus = resultSet.getString("submissionStatus");
                    int order = resultSet.getInt("submissionOrder");

                    Submission submission = Submission.builder()
                            .assignmentID(assignmentID)
                            .studentID(userID)
                            .submissionID(submissionID)
                            .submissionContent(submissionContent)
                            .submissionTime(submissionTime)
                            .submissionStatus(submissionStatus)
                            .submissionOrder(order)
                            .build();

                    submissionList.add(submission);
                }
            }
        } catch (SQLException e) {
            log.error("Error when getSubmissionsOfOneAssignmentAndStudent: "  + e.getMessage());
        }

        return submissionList;
    }

    @Override
    public List<Submission> getSubmissionsFromAssignment(Assignment assignment) {
        List<Submission> submissionList = new ArrayList<>();
        String query = "SELECT * FROM submissions WHERE assignmentID = ? ORDER BY studentID, submissionOrder";

        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setInt(1, assignment.getAssignmentID());
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int submissionID = resultSet.getInt("submissionID");
                    int studentID = resultSet.getInt("studentID");
                    Timestamp submissionTime = resultSet.getTimestamp("submissionTIME");
                    String  submissionContent = resultSet.getString("submissionContent");
                    String submissionStatus = resultSet.getString("submissionStatus");
                    int order = resultSet.getInt("submissionOrder");

                    Submission submission = Submission.builder()
                            .assignmentID(assignment.getAssignmentID())
                            .studentID(studentID)
                            .submissionID(submissionID)
                            .submissionContent(submissionContent)
                            .submissionTime(submissionTime)
                            .submissionStatus(submissionStatus)
                            .submissionOrder(order)
                            .build();

                    submissionList.add(submission);
                }
            }
        } catch (SQLException e){
            log.error("Error when getSubmissionsFromAssignment: "  + e.getMessage());
        }

        return submissionList;
    }

    @Override
    public Submission getSubmissionFromTwoIDsAndOrder(int assignmentID, int userID, int order) {
        String query = "SELECT * FROM submissions WHERE assignmentID = ? AND studentID = ? AND submissionOrder = ?";

        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setInt(1, assignmentID);
            statement.setInt(2, userID);
            statement.setInt(3, order);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                int submissionID = resultSet.getInt("submissionID");
                Timestamp submissionTime = resultSet.getTimestamp("submissionTIME");
                String  submissionContent = resultSet.getString("submissionContent");
                String submissionStatus = resultSet.getString("submissionStatus");
                String evaluation = resultSet.getString("EVALUATION");
                int scores;
                scores = resultSet.getInt("SCORES");
                if (resultSet.wasNull()) {
                    scores = -1;
                }

                return  Submission.builder()
                        .assignmentID(assignmentID)
                        .studentID(userID)
                        .submissionID(submissionID)
                        .submissionContent(submissionContent)
                        .submissionTime(submissionTime)
                        .submissionStatus(submissionStatus)
                        .submissionOrder(order)
                        .evaluation(evaluation)
                        .scores(scores)
                        .build();
            }
        } catch (SQLException e) {
            log.error("Error when getSubmissionFromTwoIDsAndOrder: "  + e.getMessage());
        }

        return null;
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
