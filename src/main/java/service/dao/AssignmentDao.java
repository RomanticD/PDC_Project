package service.dao;

import service.AssignmentService;
import service.CourseService;
import domain.Assignment;
import lombok.extern.slf4j.Slf4j;
import manager.DatabaseConnectionManager;

import java.io.Closeable;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
public class AssignmentDao implements AssignmentService, Closeable{
    private final Connection conn;
    private final CourseService courseService = new CourseDao();

    public AssignmentDao() {
        DatabaseConnectionManager databaseConnectionManager = new DatabaseConnectionManager();
        conn = databaseConnectionManager.getConnection();
    }

    // Definition: every course can't have two same assignment names.
    // Usually, we make sure about every course can't have two same assignment names.
    // But two different courses can have the same assignment name.
    @Override
    public boolean doesAssignmentExist(Assignment assignment) {
        String query = "SELECT COUNT(*) FROM assignments WHERE courseID = ? AND assignmentName = ?";

        try (PreparedStatement preparedStatement = conn.prepareStatement(query)) {
            preparedStatement.setInt(1, assignment.getCourseID());
            preparedStatement.setString(2, assignment.getAssignmentName());
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    int count = resultSet.getInt(1);
                    return count > 0; // If count == 0, the assignment doesn't exist
                }
            }

            log.info("Executing SQL query: doesAssignmentExist");

        } catch (SQLException e) {
            log.error("Error when checkIfAssignmentExists: "  + e.getMessage());
        }

        return false; // Error occurred, consider the assignment name as unique
    }

    @Override
    public boolean insertAssignment(Assignment assignment) {
        if(doesAssignmentExist(assignment)){
            return false;
        }

        String query = "INSERT INTO assignments (ASSIGNMENTNAME, COURSEID, ASSIGNMENTCONTENT, DEADLINE) VALUES (?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = conn.prepareStatement(query)) {
            preparedStatement.setString(1, assignment.getAssignmentName());
            preparedStatement.setInt(2, assignment.getCourseID());
            preparedStatement.setString(3, assignment.getAssignmentContent());
            preparedStatement.setTimestamp(4, new java.sql.Timestamp(assignment.getDeadLine().getTime()));
            preparedStatement.executeUpdate();

            log.info("Executing SQL query: insertAssignment");

        } catch (SQLException e) {
            log.error("Error when insertAssignment: "  + e.getMessage());
            return false;
        }

        return true;
    }

    @Override
    public boolean updateAssignment(Assignment assignment) {
        if(!doesAssignmentExist(assignment)){
            return false;
        }

        String query = "UPDATE assignments SET ASSIGNMENTCONTENT = ?, COURSEID = ?, DEADLINE = ? WHERE ASSIGNMENTNAME = ?";

        try (PreparedStatement preparedStatement = conn.prepareStatement(query)) {
            preparedStatement.setString(1, assignment.getAssignmentContent());
            preparedStatement.setInt(2, assignment.getCourseID());
            preparedStatement.setTimestamp(3, new java.sql.Timestamp(assignment.getDeadLine().getTime()));
            preparedStatement.setString(4, assignment.getAssignmentName());
            preparedStatement.executeUpdate();

            log.info("Executing SQL query - updateAssignment: updateAssignment");

        } catch (SQLException e) {
            log.error("Error when updateAssignment: "  + e.getMessage());
            return false;
        }

        return true;
    }

    @Override
    public boolean deleteAssignment(Assignment assignment) {
        if(!doesAssignmentExist(assignment)){
            return false;
        }

        String query = "DELETE FROM assignments WHERE assignmentName = ? AND COURSEID = ?";

        try (PreparedStatement preparedStatement = conn.prepareStatement(query)) {
            preparedStatement.setString(1, assignment.getAssignmentName());
            preparedStatement.setInt(2, assignment.getCourseID());
            preparedStatement.executeUpdate();

            log.info("Executing SQL query: deleteAssignment");

        } catch (SQLException e) {
            log.error("Error when deleteAssignment: "  + e.getMessage());
            return false;
        }
        return true;
    }

    @Override
    public List<String> getAssignmentNamesByCourseID(int courseID) {
        List<String> assignmentNames = new ArrayList<>();
        String query = "SELECT assignmentName FROM assignments WHERE courseID = ?";

        try (PreparedStatement preparedStatement = conn.prepareStatement(query)) {
            preparedStatement.setInt(1, courseID);
            ResultSet resultSet = preparedStatement.executeQuery();

            log.info("Executing SQL query: getAssignmentNamesByCourseID");

            while (resultSet.next()) {
                String assignmentName = resultSet.getString("assignmentName");
                assignmentNames.add(assignmentName);
            }

        } catch (SQLException e) {
            log.error("Error when getAssignmentNamesByCourseID: "  + e.getMessage());
        }

        return assignmentNames;
    }

    public Assignment getAssignmentByAssignmentAndCourseName(String assignmentName, String courseName) {
        String query = "SELECT * FROM assignments WHERE ASSIGNMENTNAME = ? AND COURSEID = ?";

        try (PreparedStatement preparedStatement = conn.prepareStatement(query)) {
            preparedStatement.setString(1, assignmentName);
            preparedStatement.setInt(2, courseService.getCourseIDByName(courseName));

            log.info("Executing SQL query: getAssignmentByAssignmentAndCourseName");

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                int courseId = resultSet.getInt("courseID");
                int assignmentID = resultSet.getInt("assignmentID");
                String assignmentContent = resultSet.getString("assignmentContent");
                Timestamp timestamp = resultSet.getTimestamp("deadline");
                Date deadline = (timestamp != null) ? new Date(timestamp.getTime()) : null;

                return Assignment.builder()
                        .assignmentContent(assignmentContent)
                        .assignmentName(assignmentName)
                        .assignmentID(assignmentID)
                        .courseID(courseId)
                        .deadLine(deadline)
                        .build();
            }

        } catch (SQLException e) {
            log.error("Error when getAssignmentByAssignmentAndCourseName: "  + e.getMessage());
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
