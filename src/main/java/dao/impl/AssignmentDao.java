package dao.impl;

import dao.AssignmentDaoInterface;
import domain.Assignment;
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
        return false;
    }

    @Override
    public void updateAssignment(String assignmentText, Assignment assignment, User user) {

    }

    @Override
    public boolean deleteAssignment(int assignmentID) {
        return false;
    }
}
