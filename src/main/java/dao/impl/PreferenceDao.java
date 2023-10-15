package dao.impl;

import dao.PreferenceDaoInterface;
import domain.Preference;
import lombok.extern.slf4j.Slf4j;
import manager.DatabaseConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Slf4j
public class PreferenceDao implements PreferenceDaoInterface {
    private final DatabaseConnectionManager databaseConnectionManager;
    private final Connection conn;

    public PreferenceDao() {
        databaseConnectionManager = new DatabaseConnectionManager();
        conn = databaseConnectionManager.getConnection();
    }

    @Override
    public boolean updatePreference(Preference updatedPreference) {
        try {
            String query = "UPDATE PREFERENCE SET REMEMBER_ME=?, STORED_USERNAME=?, STORED_PASSWORD=? WHERE USER_ID=?";
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setBoolean(1, updatedPreference.isRememberMe());
            preparedStatement.setString(2, updatedPreference.getStoredUsername());
            preparedStatement.setString(3, updatedPreference.getStoredPassword());
            preparedStatement.setInt(4, updatedPreference.getUserId());
            log.info("Executing SQL query: " + preparedStatement);
            int updatedRows = preparedStatement.executeUpdate();
            return updatedRows > 0;
        } catch (SQLException e) {
            log.error("Error when updating Preference, message: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean resetPreference(int userId) {
        try {
            String query = "DELETE FROM PREFERENCE WHERE USER_ID = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setInt(1, userId);
            log.info("Executing SQL query: " + preparedStatement);
            int deletedRows = preparedStatement.executeUpdate();
            return deletedRows > 0;
        } catch (SQLException e) {
            log.error("Error when reset Preference, message: " + e.getMessage());
        }
        return false;
    }

    @Override
    public Preference getPreferenceByUserId(int userId) {
        try {
            String query = "SELECT * FROM PREFERENCE WHERE USER_ID=?";
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setInt(1, userId);
            log.info("Executing SQL query: " + preparedStatement);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                boolean rememberMe = resultSet.getBoolean("REMEMBER_ME");
                String storedUsername = resultSet.getString("STORED_USERNAME");
                String storedPassword = resultSet.getString("STORED_PASSWORD");
                return Preference.builder()
                        .rememberMe(rememberMe)
                        .storedUsername(storedUsername)
                        .storedPassword(storedPassword)
                        .userId(userId)
                        .build();
            }
        } catch (SQLException e) {
            log.error("Error when get Preference, message: " + e.getMessage());
        }
        return null;
    }

    @Override
    public boolean setNewUserPreference(Preference newUserPreference) {
        try {
            String query = "INSERT INTO PREFERENCE(USER_ID, REMEMBER_ME, STORED_USERNAME, STORED_PASSWORD) VALUES (?, ?, ?, ?)";
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setInt(1, newUserPreference.getUserId());
            preparedStatement.setBoolean(2, newUserPreference.isRememberMe());
            preparedStatement.setString(3, newUserPreference.getStoredUsername());
            preparedStatement.setString(4, newUserPreference.getStoredPassword());
            log.info("Executing SQL query: " + preparedStatement);
            int insertedRows = preparedStatement.executeUpdate();
            return insertedRows > 0;
        } catch (SQLException e) {
            log.error("Error when set new Preference, message: " + e.getMessage());
        }
        return false;
    }
}
