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
        JPanel deadlinePanel = createLabelAndTextArea("Begin Date:", deadlineTextArea);


        JButton insertButton = new JButton("Create");
        insertButton.setFont(new Font("Dialog", Font.BOLD, 18));
        insertButton.addActionListener(e -> {
            if (confirmOperation("Are you sure you want to create this item?")) {
                String nameContent =  nameTextArea.getText();
                String descriptionContent =  descriptionTextArea.getText();
                String instructorContent =  instructorTextArea.getText();
                if ( nameContent.isEmpty() || descriptionContent.isEmpty() || instructorContent.isEmpty()) {
                    SwingUtilities.invokeLater(() -> {
                        FrameUtil.showErrorDialog("Text cannot be empty");
                    });
                } else {
                    boolean isCreateSuccess = createNewCourse(nameTextArea.getText(), descriptionTextArea.getText(), instructorTextArea.getText(), deadlineTextArea.getText());
                    if (isCreateSuccess){
                        CourseManageGUI.this.dispose();
                        dialog.dispose();
                        new CourseManageGUI(user);
                    }
                }
            }
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

    /**
     * Creates a JPanel containing a label and a JTextArea for user input.
     *
     * @param label     The label to display next to the JTextArea.
     * @param textArea  The JTextArea for user input.
     * @return A JPanel with the label and JTextArea components.
     */
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

    /**
     * Creates a new course with the provided information and adds it to the course service.
     *
     * @param courseName        The name of the course.
     * @param courseDescription The description of the course.
     * @param instructor        The name of the instructor.
     * @param deadline          The deadline for the course in "yyyy-MM-dd" format.
     * @return True if the course is successfully created and added; false if the course already exists.
     */
    private boolean createNewCourse(String courseName, String courseDescription, String instructor, String deadline) {
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
            return false;
        }else{
            courseService.newCourse(newCourse);
            return true;
        }
    }

    /**
     * Adds components to the provided panel, including a "Back" button.
     *
     * @param panel The panel to which components are added.
     */
    private void addComponents(JPanel panel) {
        SpringLayout springLayout = new SpringLayout();
        panel.setLayout(springLayout);
        ActionListener backToAdminGUI = e -> backToAdminGUI();
        FrameUtil.addBackButtonWithCustomAction(panel, springLayout, backToAdminGUI);
    }

    /**
     * Creates a panel containing a list of course items and returns it.
     *
     * @param courseList A list of courses to display in the panel.
     * @return A JPanel containing course items for the provided course list.
     */
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

    /**
     * Creates and returns a JPanel representing a course item.
     *
     * @param course The course to display in the item.
     * @return A JPanel representing the course item.
     */
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
            if (confirmOperation("Are you sure you want to delete this item?")) {
                courseService.deleteCourse(course);
                CourseManageGUI.this.dispose();
                new CourseManageGUI(user);;
            }
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

    /**
     * Displays a confirmation dialog with the given message and returns true if the user confirms the operation.
     *
     * @param message The message to display in the confirmation dialog.
     * @return True if the user confirms the operation; false otherwise.
     */
    private boolean confirmOperation(String message) {
        int choice = JOptionPane.showConfirmDialog(this, message, "Confirmation", JOptionPane.YES_NO_OPTION);
        return choice == JOptionPane.YES_OPTION;
    }

    /**
     * Creates a dialog for modifying course information.
     *
     * @param course The course to be modified.
     */
    private void createModifyDialog(domain.Course course) {
        JDialog modifyDialog = new JDialog();
        modifyDialog.setSize(600, 200);
        modifyDialog.setLocationRelativeTo(null);
        modifyDialog.setTitle("Choose One to Modify");
        modifyDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        modifyDialog.setResizable(true);
        modifyDialog.setModal(true);

        JPanel func = new JPanel(new BorderLayout());

        JTextArea contentArea = new JTextArea(2, 20);
        contentArea.setFont(new Font("Dialog", Font.PLAIN, 20));

        JComboBox<String> modifyOptions = new JComboBox<>(new String[]{"CourseName", "Description", "Instructor", "Begin Date"});
        modifyOptions.setFont(new Font("Dialog", Font.BOLD, 20));

        JLabel informLabel;


        informLabel = new JLabel();
        informLabel.setText(" current course name: " + course.getCourseName());
        informLabel.setFont(new Font("Dialog", Font.PLAIN, 20));
        informLabel.setVerticalAlignment(SwingConstants.CENTER);


        modifyOptions.addActionListener(e -> {
            String selectedOption = Objects.requireNonNull(modifyOptions.getSelectedItem()).toString();
            switch (selectedOption) {
                case "CourseName":
                    informLabel.setText(" current " + selectedOption + ": " + course.getCourseName());
                    break;
                case "Description":
                    informLabel.setText(" current " + selectedOption + ": " + course.getCourseDescription());
                    break;
                case "Instructor":
                    informLabel.setText(" current " + selectedOption + ": " + course.getInstructor());
                    break;
                case "Begin Date":
                    informLabel.setText(" current " + selectedOption + ": " + course.getDeadLine().toString());
                    break;
                default:
                    break;
            }
        });

        JButton modifyButton = createModifyButton("Modify", course, contentArea, modifyOptions, modifyDialog);
        modifyButton.setFont(new Font("Dialog", Font.BOLD, 20));

        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.add(contentArea, BorderLayout.NORTH);
        buttonPanel.add(modifyButton, BorderLayout.SOUTH);
        func.add(modifyOptions, BorderLayout.NORTH);
        func.add(informLabel, BorderLayout.CENTER);
        func.add(buttonPanel,BorderLayout.SOUTH);


        modifyDialog.add(func);
        modifyDialog.setVisible(true);
    }

    /**
     * Creates a JButton for modifying course information.
     *
     * @param label         The label to be displayed on the button.
     * @param course        The course to be modified.
     * @param contentArea   The JTextArea where the user can input content for modification.
     * @param modifyOptions The JComboBox for selecting the type of modification.
     * @param dialog        The dialog associated with the button.
     * @return The created JButton for modifying course information.
     */
    private JButton createModifyButton(String label, domain.Course course, JTextArea contentArea, JComboBox<String> modifyOptions, JDialog dialog) {
        JButton modifyButton = new JButton(label);
        modifyButton.addActionListener(e -> {
            String selectedOption = modifyOptions.getSelectedItem().toString();
            String content = contentArea.getText();

            if (content.isEmpty()) {
                FrameUtil.showErrorDialog("Text cannot be empty");
            } else {
                executeModify(selectedOption, course, content, dialog);
            }
        });
        return modifyButton;
    }


    /**
     * Executes the modification of course information based on the selected function.
     *
     * @param funcName The selected function for modification (e.g., "CourseName", "Description", "Instructor", "Begin Date").
     * @param course   The course to be modified.
     * @param content  The content to be set for the modification.
     * @param dialog   The dialog associated with the modification.
     */
    private void executeModify(String funcName, Course course, String content, JDialog dialog) {
        if (confirmOperation("Are you sure you want to modify this item?")) {
            switch (funcName) {
                case "CourseName":
                    courseService.updateCourseNames(course, content);
                    break;
                case "Description":
                    courseService.updataCourseDescriptions(course, content);
                    break;
                case "Instructor":
                    courseService.updateInstructor(course, content);
                    break;
                case "Begin Date":
                    modifyDeadline(course, content);
                    break;
                default:
                    break;
            }
            closeAndOpenNewGUI(dialog);
        }
    }

    /**
     * Closes the current CourseManageGUI and the provided dialog, then opens a new CourseManageGUI instance.
     *
     * @param dialog The dialog to be closed.
     */
    private void closeAndOpenNewGUI(JDialog dialog) {
        CourseManageGUI.this.dispose();
        dialog.dispose();
        new CourseManageGUI(user);
    }


    /**
     * Modifies the deadline of a course and updates it in the database.
     *
     * @param course    The course to be modified.
     * @param deadline  The new deadline in the "yyyy-MM-dd" format.
     */
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

    /**
     * Closes the current AdminGUI and opens the MainGUI.
     */
    private void backToAdminGUI() {
        CourseManageGUI.this.dispose();
        new AdminGUI(user);
    }

    /**
     * Retrieves the background panel for the AdminGUI.
     *
     * @return The background panel with an image, or null if an error occurs.
     */
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
