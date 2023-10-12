package dao;

import domain.Role;
import domain.User;

import java.sql.ResultSet;

public interface UserDaoInterface {
    ResultSet validateUser(String username, String password);

    boolean createUser(User newUser);

    boolean isUserExists(String username);
    User updateUserUsername(User user, String newUsername);

    User updateUserPassword(User user, String newPassword);

    User updateUserEmail(User user, String newEmail);

    User updateUserRole(User user, Role newRole);

    User updateUserName(User user, String newName);
}
