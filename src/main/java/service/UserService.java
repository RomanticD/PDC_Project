package service;

import domain.enums.Role;
import domain.User;

import java.sql.ResultSet;

public interface UserService {
    ResultSet validateUser(String username, String password);

    boolean createUser(User newUser);

    boolean isUserExists(String username);
    User updateUserUsername(User user, String newUsername);

    User updateUserPassword(User user, String newPassword);

    User updateUserEmail(User user, String newEmail);

    User updateUserRole(User user, Role newRole);

    User updateUserName(User user, String newName);

    User getUserByUsername(String username);

    User getUserById(int id);

    boolean deleteUserById(int id);

    boolean deleteUserByUsername(String username);
}
