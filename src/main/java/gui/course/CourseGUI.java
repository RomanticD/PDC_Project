package gui.course;

import constants.UIConstants;
import gui.MainGUI;
import service.CourseSelectionService;
import service.dao.CourseSelectionDao;
import service.CourseService;
import service.dao.CourseDao;
import domain.User;
import domain.enums.CourseDetailPageFrom;
import lombok.extern.slf4j.Slf4j;
import util.GraphicsUtil;

import javax.swing.*;
import java.awt.*;
import java.util.List;

@Slf4j
public class CourseGUI extends JFrame {
    private final User user;
    private final CourseService courseService;
    private final CourseSelectionService courseSelectionService;
    private List<domain.Course> courseList;

    public CourseGUI(User user) {
        this.setTitle("All Courses");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
        this.setResizable(false);
        this.setSize(UIConstants.COURSE_GUI_FRAME_SIZE[0], UIConstants.COURSE_GUI_FRAME_SIZE[1]);
        this.setLocationRelativeTo(null);
        this.user = user;
        this.courseSelectionService = new CourseSelectionDao();
        this.courseService = new CourseDao();
        this.courseList = courseService.getAllCourses();

        // Create a test panel to hold everything
        JPanel mainPanel = new JPanel();
        JPanel topPanel = new JPanel();
        JPanel bottomPanel = new JPanel();

        topPanel.setLayout(new BorderLayout());
        mainPanel.setLayout(new BorderLayout());
        bottomPanel.setLayout(new BorderLayout());

        // Create a "Back" button and add it to the top-left corner
        JButton backButton = new JButton("Back");
        backButton.setFont(new Font("Dialog", Font.BOLD, 15));

        JButton userCoursesButton = new JButton("Check Enrolled Courses");
        userCoursesButton.setFont(new Font("Dialog", Font.BOLD, 15));

        userCoursesButton.addActionListener(e -> {
            log.info("Ready to check " + user.getName() + "'s enrolled courses");
            CourseGUI.this.dispose();
            new UserCoursesGUI(user);
        });

        backButton.addActionListener(e -> {
            CourseGUI.this.dispose();
            new MainGUI(user);
        });
        topPanel.add(backButton, BorderLayout.WEST);

        if (!user.isAdmin()){
            topPanel.add(userCoursesButton, BorderLayout.EAST);
        }

        bottomPanel.add(GraphicsUtil.createColoredSquareWithLabel(UIConstants.ENROLLED_COLOR, 15, "Enrolled"), BorderLayout.NORTH);
        bottomPanel.add(GraphicsUtil.createColoredSquareWithLabel(UIConstants.ENROLLED_BEFORE_BUT_QUIT_COLOR, 15, "Enrolled Before"), BorderLayout.CENTER);
        bottomPanel.add(GraphicsUtil.createColoredSquareWithLabel(Color.white, 15, "No Operation Yet"), BorderLayout.SOUTH);

        mainPanel.add(topPanel, BorderLayout.NORTH);
        JScrollPane js = new JScrollPane(addCourseList(courseList), ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        mainPanel.add(js, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        this.setContentPane(mainPanel);
    }

    /**
     * Adds a list of courses to a JPanel.
     *
     * @param courseList the list of courses to be added.
     * @return a JPanel containing the course list.
     */

    public JPanel addCourseList(List<domain.Course> courseList) {
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new GridLayout(0, 1));

        for (domain.Course course : courseList) {
            listPanel.add(addCourseItem(course));
        }
        if (courseList.size() < 6) {
            for (int i = 0; i < 6 - courseList.size(); i++) {
                listPanel.add(new JPanel());
            }
        }
        return listPanel;
    }

    /**
     * Adds a single course item to a JPanel.
     *
     * @param course the course to be added as an item.
     * @return a JPanel containing the course item.
     */
    private JPanel addCourseItem(domain.Course course) {
        JPanel coursePanel = new JPanel();
        coursePanel.setLayout(new BorderLayout());

        JLabel nameLabel = new JLabel(course.getCourseName());
        nameLabel.setFont(new Font("Dialog", Font.BOLD, 15));
        if (selectedByUser(course)){
            nameLabel.setForeground(UIConstants.ENROLLED_COLOR);
        }else if (selectedByUserBefore(course)){
            nameLabel.setForeground(UIConstants.ENROLLED_BEFORE_BUT_QUIT_COLOR);
        }

        nameLabel.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 0)); // Add padding

        JButton checkButton = new JButton("Check");
        checkButton.addActionListener(e -> {
            log.info("Checking Button Clicked. Redirecting to " + course.getCourseName() + "'s Detail");
            CourseGUI.this.dispose();
            new CourseDetailGUI(course, user, CourseDetailPageFrom.COURSE_PAGE);
        });

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout());
        contentPanel.add(nameLabel, BorderLayout.WEST);
        contentPanel.add(checkButton, BorderLayout.EAST);

        coursePanel.add(contentPanel, BorderLayout.CENTER);

        return coursePanel;
    }

    private boolean selectedByUserBefore(domain.Course course) {
        return courseSelectionService.selectionStatusEqualsUnselected(user, course);
    }

    private boolean selectedByUser(domain.Course course) {
        return courseSelectionService.checkIfUserEnrolled(user, course);
    }
}