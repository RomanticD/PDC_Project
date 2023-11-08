package gui.admin;

import constants.UIConstants;
import domain.User;
import gui.MainGUI;
import gui.assignment.SelectAssignmentGUI;
import gui.course.CourseGUI;
import gui.sub.BackgroundPanel;
import gui.sub.clock.Clock;
import lombok.extern.slf4j.Slf4j;
import util.FrameUtil;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

@Slf4j
public class AdminGUI extends JFrame {
    private  User user;
    public AdminGUI(User user){
        this.user = user;
        this.setTitle("Welcome, " + user.getRole() + " " + user.getName() + " !");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
        this.setResizable(false);
        this.setSize(600,UIConstants.MAIN_GUI_FRAME_SIZE[1]);
        this.setLocationRelativeTo(null);
        JPanel panel = getBackgroundPanel();
        addComponents(Objects.requireNonNull(panel));
    }

    /**
     * Retrieves the background panel for the AdminGUI.
     *
     * @return The background panel with an image, or null if an error occurs.
     */
    private JPanel getBackgroundPanel() {
        try {
            BufferedImage backgroundImage = ImageIO.read(new File(UIConstants.ADMIN_GUI_IMAGE));
            return new BackgroundPanel(backgroundImage);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Adds components to the provided panel, including buttons and a clock display.
     *
     * @param panel The panel to which components will be added.
     */
    private void addComponents(JPanel panel) {
        SpringLayout springLayout = new SpringLayout();
        panel.setLayout(springLayout);

        ActionListener backToMainGUI = e -> backToMainGUI();
        FrameUtil.addBackButtonWithCustomAction(panel, springLayout, backToMainGUI);

        //Assignment Manage Button
        JButton manageAssignmentButton = addButton("Manage Assignment", panel, panel, 160, springLayout);
        manageAssignmentButton.setFont(new Font("Dialog", Font.BOLD, 18));
        manageAssignmentButton.setPreferredSize(new Dimension(300, 50));
        manageAssignmentButton.addActionListener(e -> {
            log.info("Going to Assignment GUI");
            AdminGUI.this.dispose();
            new SelectAssignmentGUI(user);
        });


        //Course Manage Button
        JButton manageCourseButton = addButton("Manage Course", panel, manageAssignmentButton, 90, springLayout);
        manageCourseButton.setFont(new Font("Dialog", Font.BOLD, 18));
        manageCourseButton.setPreferredSize(new Dimension(300, 50));
        manageCourseButton.addActionListener(e -> {
            log.info("Going to Manage Course GUI");
            AdminGUI.this.dispose();
            new CourseManageGUI(user);
        });


        //Clock to display current time
        Clock clock = new Clock();
        springLayout.putConstraint(SpringLayout.WEST, clock, 10, SpringLayout.WEST, panel);
        springLayout.putConstraint(SpringLayout.NORTH, clock, (UIConstants.MAIN_GUI_FRAME_SIZE[1] - UIConstants.CLOCK_SIZE) / 2, SpringLayout.NORTH, panel);
        panel.add(clock);

        this.getContentPane().add(panel);
    }

    /**
     * Closes the current AdminGUI and opens the MainGUI.
     */
    private void backToMainGUI() {
        AdminGUI.this.dispose();
        new MainGUI(user);
    }

    /**
     * Creates and adds a JButton to the specified container with custom constraints.
     *
     * @param name                   The name or text for the button.
     * @param container              The parent component to which the button will be added.
     * @param verticalRelatedComponent The component to which the button's position is related vertically.
     * @param topPaddingToVerticalComponent The padding value from the verticalRelatedComponent.
     * @param springLayout            The SpringLayout used to set button constraints.
     * @return The created JButton.
     */
    private JButton addButton(String name, JComponent container, JComponent verticalRelatedComponent, int topPaddingToVerticalComponent, SpringLayout springLayout){
        JButton button = new JButton(name);
        button.setFont(new Font("Dialog", Font.BOLD, 12));
        springLayout.putConstraint(SpringLayout.WEST, button, UIConstants.MAIN_PAGE_BUTTON_LEADING_PADDING, SpringLayout.WEST, container);
        springLayout.putConstraint(SpringLayout.EAST, button, UIConstants.MAIN_PAGE_BUTTON_BOTTOM_PADDING, SpringLayout.EAST, container);
        springLayout.putConstraint(SpringLayout.NORTH, button, topPaddingToVerticalComponent, SpringLayout.NORTH, verticalRelatedComponent);
        container.add(button);
        return button;
    }
}
