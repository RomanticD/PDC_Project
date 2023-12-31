package util;

import domain.Assignment;
import domain.Course;
import domain.User;
import domain.enums.Role;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class InstanceUtil {
    public static User getTestUserInstance(){
        return User.builder()
                .userId(0)
                .email("Test@Example.com")
                .name("Test Case")
                .username("User Instance")
                .password("password")
                .role(Role.USER)
                .build();
    }

    public static Course getTestCourseInstance() throws ParseException {
        return Course.builder()
                .courseDescription("Test Description")
                .courseName("Course Instance")
                .instructor("Test Instructor")
                .deadLine(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2025-10-20 18:00:00"))
                .build();
    }

    public static Assignment getTestAssignmentInstance(){
        return Assignment.builder()
                .assignmentName("Assignment Instance")
                .courseID(1)
                .assignmentID(-1)
                .assignmentContent("Test Description")
                .deadLine(new Date())
                .build();
    }
}
