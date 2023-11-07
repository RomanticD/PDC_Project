package gui.admin;

import constants.UIConstants;
import domain.Course;
import service.CourseService;
import service.dao.CourseDao;
import domain.User;
import gui.sub.BackgroundPanel;
import lombok.extern.slf4j.Slf4j;
import util.FrameUtil;

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
    private final CourseService courseService;

    public CourseManageGUI(User user) {
        this.setTitle("Manage Course");
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
        this.setResizable(true);
        this.setSize(600,800);
        this.setLocationRelativeTo(null);
        JPanel panel = getBackgroundPanel();
        addComponents(Objects.requireNonNull(panel));

        this.user = user;
        this.courseService = new CourseDao();
        List<Course> courseList = courseService.getAllCourses();

        // Create a test panel to hold everything
        JPanel mainPanel = new JPanel(new BorderLayout());
        JPanel topPanel = new JPanel(new BorderLayout());
        JPanel bottomPanel = new JPanel(new BorderLayout());

        // Create a "Back" button and add it to the top-left corner
        JButton backButton = new JButton("Back");
        backButton.setFont(new Font("Dialog", Font.BOLD, 15));

        JButton refreshButton = new JButton("Refresh");
        refreshButton.setFont(new Font("Dialog", Font.BOLD, 15));

        JButton createCourseButton = new JButton("Create a New Course");
        createCourseButton.setFont(new Font("Dialog", Font.BOLD, 15));

        backButton.addActionListener(e -> {
            backToAdminGUI();
        });
        topPanel.add(backButton, BorderLayout.WEST);

        refreshButton.addActionListener(e -> {
            this.dispose();
            new CourseManageGUI(user);
        });

        topPanel.add(refreshButton,BorderLayout.EAST);

        createCourseButton.addActionListener(e -> createCourseDialog());

        bottomPanel.add(createCourseButton);

        mainPanel.add(topPanel, BorderLayout.NORTH);
        JScrollPane js = new JScrollPane(addCourseList(courseList), ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        mainPanel.add(js, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        this.setContentPane(mainPanel);
    }

    private void createCourseDialog() {
        JDialog dialog = new JDialog();
        dialog.setLocationRelativeTo(null);
        dialog.setTitle("Create New Course");
        dialog.setSize(400,400);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setLocationRelativeTo(null);
        dialog.setResizable(true);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        

        JTextArea nameTextArea = new JTextArea();
        JPanel namePanel = createLabelAndTextArea("Course Name:", nameTextArea);


        JTextArea descriptionTextArea = new JTextArea();
        JPanel descriptionPanel = createLabelAndTextArea("Description:", descriptionTextArea);

        JTextArea instructorTextArea = new JTextArea();
        JPanel instructorPanel = createLabelAndTextArea("Instruction:", instructorTextArea);

        JTextArea deadlineTextArea = new JTextArea();
        JPanel deadlinePanel = createLabelAndTextArea("Deadline:", deadlineTextArea);


        JButton insertButton = new JButton("Create");
        insertButton.setFont(new Font("Dialog", Font.BOLD, 18));
        insertButton.addActionListener(e -> {
            createNewCourse(nameTextArea.getText(),descriptionTextArea.getText(),instructorTextArea.getText(),deadlineTextArea.getText());
        });

        JPanel insertPanel = new JPanel(new BorderLayout());

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

    private JPanel createLabelAndTextArea(String label, JTextArea textArea) {
        JPanel panel = new JPanel(new GridBagLayout());
        JLabel nameLabel = new JLabel(label);
        nameLabel.setFont(new Font("Dialog", Font.BOLD, 20));

        GridBagConstraints labelConstraints = new GridBagConstraints();
        labelConstraints.gridx = 0;
        labelConstraints.gridy = 0;
        labelConstraints.weightx = 0.2;

        GridBagConstraints textAreaConstraints = new GridBagConstraints();
        textAreaConstraints.gridx = 1;
        textAreaConstraints.gridy = 0;
        textAreaConstraints.weightx = 0.8;
        textAreaConstraints.fill = GridBagConstraints.HORIZONTAL;

        panel.add(nameLabel, labelConstraints);
        panel.add(textArea, textAreaConstraints);
        return panel;
    }


    private void createNewCourse(String courseName, String courseDescription, String instructor, String deadline) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        try {
            date = sdf.parse(deadline);
        } catch (ParseException e) {
            log.error("Cannot parse the date, wrong format!");
        }

        Course newCourse = Course.builder()
                .courseName(courseName)
                .courseDescription(courseDescription)
                .instructor(instructor)
                .deadLine(date)
                .build();

        if (courseService.doesCourseExist(newCourse)){
            FrameUtil.showErrorDialog("Course is existing!");
        }else{
            courseService.newCourse(newCourse);}
    }

    private void addComponents(JPanel panel) {
        SpringLayout springLayout = new SpringLayout();
        panel.setLayout(springLayout);
        ActionListener backToAdminGUI = e -> backToAdminGUI();
        FrameUtil.addBackButtonWithCustomAction(panel, springLayout, backToAdminGUI);
    }

    public JPanel addCourseList(List<domain.Course> courseList) {
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new GridLayout(0, 1));

        for (domain.Course course : courseList) {
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

    private JPanel addCourseItem(domain.Course course) {
        JPanel coursePanel = new JPanel();
        coursePanel.setLayout(new BorderLayout());

        JLabel nameLabel = new JLabel(course.getCourseName());
        nameLabel.setFont(new Font("Dialog", Font.BOLD, 15));

        JButton modifyButton = new JButton("Modify");
        JButton deleteButton = new JButton("Delete");

        nameLabel.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 0));
        modifyButton.addActionListener(e -> {
            createModifyDialog(course);
        });

        deleteButton.addActionListener(e -> {
            courseService.deleteCourse(course);
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

    private void createModifyDialog(domain.Course course) {
        JDialog modifyDialog = new JDialog();
        modifyDialog.setSize(400, 200);
        modifyDialog.setLocationRelativeTo(null);
        modifyDialog.setTitle("Choose One to Modify");
        modifyDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        modifyDialog.setResizable(true);
        modifyDialog.setModal(true);

        JPanel func = new JPanel(new BorderLayout()); // 使用 BorderLayout 布局

        JTextArea contentArea = new JTextArea(2, 20);
        contentArea.setFont(new Font("Dialog", Font.PLAIN, 20));

        JComboBox<String> modifyOptions = new JComboBox<>(new String[]{"CourseName", "Description", "Instructor", "Deadline"});
        modifyOptions.setFont(new Font("Dialog", Font.BOLD, 20));

        JButton modifyButton = createModifyButton("Modify", course, contentArea, modifyOptions);
        modifyButton.setFont(new Font("Dialog", Font.BOLD, 20));

        func.add(contentArea, BorderLayout.CENTER);
        func.add(modifyOptions, BorderLayout.NORTH);
        func.add(modifyButton, BorderLayout.SOUTH);

        modifyDialog.add(func);
        modifyDialog.setVisible(true);
    }

        private JButton createModifyButton(String label, domain.Course course, JTextArea contentArea, JComboBox<String> modifyOptions) {
        JButton button = new JButton(label);
        button.addActionListener(e -> {
            String selectedOption = modifyOptions.getSelectedItem().toString();
            modify(selectedOption, course, contentArea.getText());
        });
        return button;
    }


    private void modify(String funcName, domain.Course course, String content) {
        switch (funcName){
            case"CourseName":
            courseService.updateCourseNames(course,content);
                break;
            case"Description":
            courseService.updataCourseDescriptions(course,content);
                break;
            case"Instructor":
                courseService.updateInstructor(course,content);
                break;
            case"Deadline":modifyDeadline(course,content);
                break;
            default:
                break;
        }
    }

    private void modifyDeadline(domain.Course course, String deadline) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date;
        try {
            date = sdf.parse(deadline);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        courseService.updateDeadline(course,date);
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
