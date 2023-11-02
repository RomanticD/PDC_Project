package gui.modification;

import constants.UIConstants;
import dao.CourseDaoInterface;
import dao.CourseSelectionDaoInterface;
import dao.impl.CourseDao;
import dao.impl.CourseSelectionDao;
import domain.Course;
import domain.User;
import domain.enums.Role;
import gui.AdminGUI;
import gui.CourseGUI;
import gui.MainGUI;
import gui.UserCoursesGUI;
import gui.sub.BackgroundPanel;
import lombok.extern.slf4j.Slf4j;
import util.FrameUtil;
import util.GraphicsUtil;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;
@Slf4j
public class CourseManageGUI extends JFrame {
    private final User user;
    private final CourseDaoInterface courseDao;
    private final CourseSelectionDaoInterface courseSelectionDao;
    private List<Course> courseList;
    public CourseManageGUI(User user) {
        this.setTitle("CourseManage");
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
        this.setResizable(true);
        this.setSize(600,800);
        this.setLocationRelativeTo(null);
        JPanel panel = getBackgroundPanel();
        addComponents(Objects.requireNonNull(panel));

        this.user = user;
        this.courseSelectionDao = new CourseSelectionDao();
        this.courseDao = new CourseDao();
        this.courseList = courseDao.getAllCourses();

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

        JButton refreshButton = new JButton("Refresh");
        refreshButton.setFont(new Font("Dialog", Font.BOLD, 15));

        JButton insertButton = new JButton("Insert");
        insertButton.setFont(new Font("Dialog", Font.BOLD, 15));

        backButton.addActionListener(e -> {
            backToAdminGUI();
        });
        topPanel.add(backButton, BorderLayout.WEST);

        refreshButton.addActionListener(e -> {
            this.dispose();
            new CourseManageGUI(user);
        });
        topPanel.add(refreshButton,BorderLayout.EAST);

        insertButton.addActionListener(e -> {
            insertCourse();
        });

        bottomPanel.add(insertButton);

        mainPanel.add(topPanel, BorderLayout.NORTH);
        JScrollPane js = new JScrollPane(addCourseList(courseList), ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        mainPanel.add(js, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        this.setContentPane(mainPanel);
    }

    private void insertCourse() {
        JDialog dialog = new JDialog();
        dialog.setLocationRelativeTo(null);
        dialog.setTitle("Insert New Course");
        dialog.setSize(400,400);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setResizable(true);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        JPanel namePanel = new JPanel(new BorderLayout());
        JPanel descriptionPanel = new JPanel(new BorderLayout());
        JPanel instructorPanel = new JPanel(new BorderLayout());
        JPanel deadlinePanel = new JPanel(new BorderLayout());
        JPanel insertPanel = new JPanel(new BorderLayout());

        JLabel nameLabel = new JLabel("Course Name:");
        nameLabel.setFont(new Font("Dialog", Font.BOLD, 20));
        JTextField nameTextField = new JTextField(20);

        JLabel descriptionLabel = new JLabel("Description:");
        descriptionLabel.setFont(new Font("Dialog", Font.BOLD, 20));
        JTextArea descriptionTextArea = new JTextArea(0,20);

        JLabel instructorLabel = new JLabel("Instructor:");
        instructorLabel.setFont(new Font("Dialog", Font.BOLD, 20));
        JTextField instructorTextField = new JTextField(20);

        JLabel deadlineLabel = new JLabel("Deadline:");
        deadlineLabel.setFont(new Font("Dialog", Font.BOLD, 20));
        JTextField deadlineTextField = new JTextField(20);

        JButton insertButton = new JButton("Insert");
        insertButton.setFont(new Font("Dialog", Font.BOLD, 18));
        insertButton.addActionListener(e -> {
            NewCourse(nameTextField.getText(),descriptionTextArea.getText(),instructorTextField.getText(),deadlineTextField.getText());
        });

        namePanel.add(nameLabel,BorderLayout.WEST);
        namePanel.add(nameTextField,BorderLayout.EAST);
        descriptionPanel.add(descriptionLabel,BorderLayout.WEST);
        descriptionPanel.add(descriptionTextArea,BorderLayout.EAST);
        instructorPanel.add(instructorLabel,BorderLayout.WEST);
        instructorPanel.add(instructorTextField,BorderLayout.EAST);
        deadlinePanel.add(deadlineLabel,BorderLayout.WEST);
        deadlinePanel.add(deadlineTextField,BorderLayout.EAST);
        insertPanel.add(insertButton,BorderLayout.CENTER);

        contentPanel.add(namePanel);
        contentPanel.add(descriptionPanel);
        contentPanel.add(instructorPanel);
        contentPanel.add(deadlinePanel);
        contentPanel.add(insertPanel);
        dialog.setContentPane(contentPanel);
        pack();

        dialog.setVisible(true);
    }

    private void NewCourse(String courseName, String courseDescription, String Instructor, String deadline) {
        int newID = courseDao.FindMinUnusedCourseID();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date;
        try {
            date = sdf.parse(deadline);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        Course newCourse = new Course(newID, courseName, courseDescription, Instructor, date);
        if (courseDao.doesCourseExist(newCourse)){
            FrameUtil.showErrorDialog("Course is existing!");
        }else{courseDao.newCourse(newCourse);}
    }

    private void addComponents(JPanel panel) {
        SpringLayout springLayout = new SpringLayout();
        panel.setLayout(springLayout);

        ActionListener backToAdminGUI = e -> backToAdminGUI();
        FrameUtil.addBackButtonWithCustomAction(panel, springLayout, backToAdminGUI);

    }

    public JPanel addCourseList(List<Course> courseList) {
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new GridLayout(0, 1));

        for (Course course : courseList) {
            JPanel courseItem = addCourseItem(course);
            listPanel.add(courseItem);
        }
        if (courseList.size() < 6) {
            for (int i = 0; i < 6 - courseList.size(); i++) {
                listPanel.add(new JPanel());
            }
        }
        return listPanel;
    }

    private JPanel addCourseItem(Course course) {
        JPanel coursePanel = new JPanel();
        coursePanel.setLayout(new BorderLayout());

        JLabel nameLabel = new JLabel(course.getCourseName());
        nameLabel.setFont(new Font("Dialog", Font.BOLD, 15));

        JButton modifyButton = new JButton("Modify");
        JButton deleteButton = new JButton("Delete");

        nameLabel.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 0));

        final Course currentCourse = course;

        modifyButton.addActionListener(e -> {
            JDialog modyfyDialog = new JDialog();
            modyfyDialog.setSize(400,400);
            modyfyDialog.setLocationRelativeTo(null);
            modyfyDialog.setTitle("Choose One to Modify");
            modyfyDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            modyfyDialog.setResizable(true);

            JPanel func = new JPanel();
            func.setLayout(new BoxLayout(func, BoxLayout.Y_AXIS));
            JPanel textPanle = new JPanel(new BorderLayout());
            JPanel namePanel = new JPanel(new BorderLayout());
            JPanel descriptionPanel = new JPanel(new BorderLayout());
            JPanel InstructorPanel = new JPanel(new BorderLayout());
            JPanel deadlinePanel = new JPanel(new BorderLayout());

            JTextArea contentArea = new JTextArea(5,20);
            JButton modifyNameButton = new JButton("CourseName");
            JButton modifyDescriptionButton = new JButton("Description");
            JButton modifyInstructorButton = new JButton("Instructor");
            JButton modifyDeadlineButton = new JButton("Deadline");

            modifyNameButton.addActionListener(e1 -> {
                modify(modifyNameButton.getText(),currentCourse,contentArea.getText());
                log.info("modifyNameButton Clicked");
            });

            modifyDeadlineButton.addActionListener(e1 -> {
                modify(modifyDeadlineButton.getText(),currentCourse,contentArea.getText());
            });

            modifyInstructorButton.addActionListener(e1 -> {
                modify(modifyInstructorButton.getText(),currentCourse,contentArea.getText());
            });

            modifyDeadlineButton.addActionListener(e1 -> {
                modify(modifyDeadlineButton.getText(),currentCourse,contentArea.getText());
            });


            textPanle.add(contentArea,BorderLayout.CENTER);
            namePanel.add(modifyNameButton,BorderLayout.CENTER);
            descriptionPanel.add(modifyDescriptionButton,BorderLayout.CENTER);
            InstructorPanel.add(modifyInstructorButton,BorderLayout.CENTER);
            deadlinePanel.add(modifyDeadlineButton,BorderLayout.CENTER);
            func.add(textPanle);
            func.add(namePanel);
            func.add(descriptionPanel);
            func.add(InstructorPanel);
            func.add(deadlinePanel);

            modyfyDialog.add(func);

            modyfyDialog.setVisible(true);




        });

        deleteButton.addActionListener(e -> {
            courseDao.deleteCourse(currentCourse);
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(modifyButton);
        buttonPanel.add(deleteButton);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout());
        contentPanel.add(nameLabel, BorderLayout.WEST);
        contentPanel.add(buttonPanel, BorderLayout.EAST);

        coursePanel.add(contentPanel, BorderLayout.CENTER);

        return coursePanel;
    }

    private void modify(String funcName,Course course, String content) {
        switch (funcName){
            case"CourseName":course.setCourseName(content);
            courseDao.updateCourseNames(course,content);
                break;
            case"Description":course.setCourseDescription(content);
            courseDao.updataCourseDescriptions(course,content);
                break;
            case"Instructor":course.setInstructor(content);
                courseDao.updateInstructor(course,content);
                break;
            case"Deadline":modifyDeadline(course,content);
                break;
            default:
                break;
        }
    }

    private void modifyDeadline(Course course, String deadline) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date;
        try {
            date = sdf.parse(deadline);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        courseDao.updateDeadline(course,date);
    }


    private void backToAdminGUI() {
        this.dispose();
        new AdminGUI(user);
    }

    private JPanel getBackgroundPanel() {
        try {
            BufferedImage backgroundImage = ImageIO.read(new File(UIConstants.MAIN_BACKGROUND_IMAGE));
            return new BackgroundPanel(backgroundImage);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


}
