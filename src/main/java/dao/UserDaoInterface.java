package dao;

import domain.User;

import java.sql.ResultSet;

public interface UserDaoInterface {
    ResultSet validateUser(String username, String password);

    boolean createUser(User newUser);

    boolean isUserExists(String username);
}
