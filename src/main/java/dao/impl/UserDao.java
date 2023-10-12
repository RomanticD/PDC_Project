package dao.impl;

import dao.UserDaoInterface;
import db.DatabaseConnectionManager;
import domain.Role;
import domain.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDao implements UserDaoInterface {
    private final DatabaseConnectionManager databaseConnectionManager;
    private final Connection conn;

    public UserDao() {
        databaseConnectionManager = new DatabaseConnectionManager();
        conn = databaseConnectionManager.getConnection();
    }

    public ResultSet validateUser(String username, String password) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement("select * from user_profile where username=? and password=?");
            ps.setString(1, username);
            ps.setString(2, password);
            rs = ps.executeQuery();
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
        return rs;
    }

    public boolean createUser(User newUser) {
        String sql = "insert into user_profile(name,username,password,email,role) values (?,?,?,?,?)";
        PreparedStatement ps = null;
        String role = "USER";

        if (newUser.getRole() == Role.ADMIN){
            role = "ADMIN";
        }

        try {
            ps = conn.prepareStatement(sql);
            ps.setString(1, newUser.getName());
            ps.setString(2, newUser.getUsername());
            ps.setString(3, newUser.getPassword());
            ps.setString(4, newUser.getEmail());
            ps.setString(5, role);
            ps.execute();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return true;
    }

    public boolean isUserExists(String username) {
        String sql = "SELECT * FROM user_profile WHERE username = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet resultSet = ps.executeQuery();
            return resultSet.next(); // Check if a row with the given username exists
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }
}
