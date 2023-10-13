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
        PreparedStatement ps;
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

    @Override
    public User updateUserUsername(User user, String newUsername) {
        String sql = "UPDATE user_profile SET username = ? WHERE username = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newUsername);
            ps.setString(2, user.getUsername());
            int updatedRows = ps.executeUpdate();

            if (updatedRows > 0) {
                // 更新成功，返回更新后的用户对象
                user.setUsername(newUsername);
                return user;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null; // 更新失败，返回null
    }

    @Override
    public User updateUserPassword(User user, String newPassword) {
        String sql = "UPDATE user_profile SET password = ? WHERE username = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newPassword);
            ps.setString(2, user.getUsername());
            int updatedRows = ps.executeUpdate();

            if (updatedRows > 0) {
                // 更新成功，返回更新后的用户对象
                user.setPassword(newPassword);
                return user;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null; // 更新失败，返回null
    }

    @Override
    public User updateUserEmail(User user, String newEmail) {
        String sql = "UPDATE user_profile SET email = ? WHERE username = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newEmail);
            ps.setString(2, user.getUsername());
            int updatedRows = ps.executeUpdate();

            if (updatedRows > 0) {
                // 更新成功，返回更新后的用户对象
                user.setEmail(newEmail);
                return user;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null; // 更新失败，返回null
    }

    @Override
    public User updateUserRole(User user, Role newRole) {
        String roleString = (newRole == Role.ADMIN) ? "ADMIN" : "USER";
        String sql = "UPDATE user_profile SET role = ? WHERE username = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, roleString);
            ps.setString(2, user.getUsername());
            int updatedRows = ps.executeUpdate();

            if (updatedRows > 0) {
                // 更新成功，返回更新后的用户对象
                user.setRole(newRole);
                return user;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null; // 更新失败，返回null
    }

    @Override
    public User updateUserName(User user, String newName) {
        String sql = "UPDATE user_profile SET name = ? WHERE username = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newName);
            ps.setString(2, user.getUsername());
            int updatedRows = ps.executeUpdate();

            if (updatedRows > 0) {
                // 更新成功，返回更新后的用户对象
                user.setName(newName);
                return user;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null; // 更新失败，返回null
    }

    @Override
    public User getUserByName(String username) {
        String sql = "SELECT * FROM user_profile WHERE username = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet resultSet = ps.executeQuery();

            if (resultSet.next()) {
                User user = User.builder()
                        .username(resultSet.getString("username"))
                        .name(resultSet.getString("name"))
                        .password(resultSet.getString("password"))
                        .email(resultSet.getString("email"))
                        .build();

                // Convert the role string to a Role enum
                String roleString = resultSet.getString("role");
                Role role = roleString.equals("ADMIN") ? Role.ADMIN : Role.USER;
                user.setRole(role);

                return user;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null; // 如果未找到用户，返回null
    }

}
