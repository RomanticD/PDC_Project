package dao.impl;

import dao.CourseSelectionDaoInterface;
import db.DatabaseConnectionManager;
import domain.Course;
import domain.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CourseSelectionDao implements CourseSelectionDaoInterface {
    private final DatabaseConnectionManager databaseConnectionManager;
    private final Connection conn;

    public CourseSelectionDao() {
        databaseConnectionManager = new DatabaseConnectionManager();
        conn = databaseConnectionManager.getConnection();
    }

    @Override
    public int[] getSelectedCourseId(User user) {
        List<Integer> courseIdList = new ArrayList<>();
        String query = "SELECT COURSE_ID FROM COURSE_SELECTION WHERE USER_ID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, user.getUserId()); // userId是你要查询的具体用户ID
            ResultSet resultSet = stmt.executeQuery();
            while (resultSet.next()) {
                courseIdList.add(resultSet.getInt("COURSE_ID"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        int[] courseIds = new int[courseIdList.size()];
        for (int i = 0; i < courseIdList.size(); i++) {
            courseIds[i] = courseIdList.get(i);
        }
        return courseIds;
    }
}

