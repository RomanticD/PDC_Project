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
public class AssignmentDao implements AssignmentDaoInterface, Closeable{

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

        try (PreparedStatement preparedStatement = conn.prepareStatement(query)) {
            preparedStatement.setInt(1, assignmentID);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    int count = resultSet.getInt(1);
                    return count > 0; // If count > 0, the assignment exists
                }
            }

            log.info("Executing SQL query: " + preparedStatement);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false; // An error occurred or no matching assignment found
    }

    @Override
    public void insertAssignment(String assignmentName, int courseID, String assignmentContent) {
        String query = "INSERT INTO assignments (ASSIGNMENTNAME, COURSEID, ASSIGNMENTCONTENT) VALUES (?, ?, ?)";

        try (PreparedStatement preparedStatement = conn.prepareStatement(query)) {
            preparedStatement.setString(1, assignmentName);
            preparedStatement.setInt(2, courseID);
            preparedStatement.setString(3, assignmentContent);
            preparedStatement.executeUpdate();

            log.info("Executing SQL query: " + preparedStatement);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateAssignment(String assignmentName, int courseID, String assignmentContent) {
        String query = "UPDATE assignments SET ASSIGNMENTCONTENT = ?, COURSEID = ? WHERE ASSIGNMENTNAME = ?";

        try (PreparedStatement preparedStatement = conn.prepareStatement(query)) {
            preparedStatement.setString(1, assignmentContent);
            preparedStatement.setInt(2, courseID);
            preparedStatement.setString(3, assignmentName);
            preparedStatement.executeUpdate();

            log.info("Executing SQL query: " + preparedStatement);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean deleteAssignment(String assignmentName) {
        String query = "DELETE FROM assignments WHERE assignmentName = ?";

        try (PreparedStatement preparedStatement = conn.prepareStatement(query)) {
            preparedStatement.setString(1, assignmentName);
            preparedStatement.executeUpdate();

            log.info("Executing SQL query: " + preparedStatement);

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

            log.info("Executing SQL query: " + preparedStatement);

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
        String query = "SELECT * FROM assignments WHERE ASSIGNMENTNAME = ? ";

        try (PreparedStatement preparedStatement = conn.prepareStatement(query)) {
            preparedStatement.setString(1, assignmentName);

            log.info("Executing SQL query: " + preparedStatement);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                int courseId = resultSet.getInt("COURSEID");
                int assignmentID = resultSet.getInt("ASSIGNMENTID");
                String assignmentContent = resultSet.getString("ASSIGNMENTCONTENT");

                return Assignment.builder()
                        .assignmentContent(assignmentContent)
                        .assignmentName(assignmentName)
                        .assignmentID(assignmentID)
                        .courseID(courseId)
                        .build();
            }

        } catch (SQLException e) {
            e.printStackTrace();
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
