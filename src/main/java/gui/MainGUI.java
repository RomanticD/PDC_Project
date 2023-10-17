package gui;

import constants.UIConstants;
import domain.User;
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
public class MainGUI extends JFrame {
    private final User user;

    public MainGUI(User user) {
        this.user = user;
        this.setTitle("Welcome, " + user.getRole() + " " + user.getName() + " !");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
        this.setResizable(false);
        this.setSize(500,480);
        this.setLocationRelativeTo(null);
        JPanel panel = getBackgroundPanel();
        addComponents(Objects.requireNonNull(panel));
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

    private void addComponents(JPanel panel) {
        SpringLayout springLayout = new SpringLayout();
        panel.setLayout(springLayout);

        ActionListener backToLoginGUI = e -> backToLoginGUI();
        FrameUtil.addBackButtonWithCustomAction(panel, springLayout, backToLoginGUI);

        JButton myProfileButton = new JButton("Profile");
        myProfileButton.setFont(new Font("Dialog", Font.BOLD, 20));
        springLayout.putConstraint(SpringLayout.WEST, myProfileButton, UIConstants.MAIN_PAGE_BUTTON_PADDING, SpringLayout.WEST, panel);
//        springLayout.putConstraint(SpringLayout.EAST, myProfileButton, -UIConstants.MAIN_PAGE_BUTTON_PADDING, SpringLayout.EAST, panel);
        springLayout.putConstraint(SpringLayout.NORTH, myProfileButton, 105, SpringLayout.NORTH, panel);
        myProfileButton.addActionListener(e -> {
            log.info("Going to Profile GUI");
            MainGUI.this.dispose();
            new ProfileGUI(user);
        });
        panel.add(myProfileButton);

        
        if (user.isAdmin()){
            JButton adminButton = new JButton("Admin");
            adminButton.setFont(new Font("Dialog", Font.BOLD, 20));
            springLayout.putConstraint(SpringLayout.WEST, adminButton, UIConstants.MAIN_PAGE_BUTTON_PADDING, SpringLayout.WEST, panel);
//            springLayout.putConstraint(SpringLayout.EAST, adminButton, -UIConstants.MAIN_PAGE_BUTTON_PADDING, SpringLayout.EAST, panel);
            springLayout.putConstraint(SpringLayout.NORTH, adminButton, 105, SpringLayout.NORTH, myProfileButton);
            panel.add(adminButton);
        }else{
            JButton assignmentButton = new JButton("Assignment");
            assignmentButton.setFont(new Font("Dialog", Font.BOLD, 20));
            springLayout.putConstraint(SpringLayout.WEST, assignmentButton, UIConstants.MAIN_PAGE_BUTTON_PADDING, SpringLayout.WEST, panel);
//            springLayout.putConstraint(SpringLayout.EAST, assignmentButton, -UIConstants.MAIN_PAGE_BUTTON_PADDING, SpringLayout.EAST, panel);
            springLayout.putConstraint(SpringLayout.NORTH, assignmentButton, 105, SpringLayout.NORTH, myProfileButton);
            panel.add(assignmentButton);
            assignmentButton.addActionListener(e -> {
                new AssignmentGUI(user);
            });
        }
        

        JButton coursesButton = new JButton("Course");
        coursesButton.setFont(new Font("Dialog", Font.BOLD, 20));
        springLayout.putConstraint(SpringLayout.WEST, coursesButton, UIConstants.MAIN_PAGE_BUTTON_PADDING, SpringLayout.WEST, panel);
//        springLayout.putConstraint(SpringLayout.EAST, coursesButton, -UIConstants.MAIN_PAGE_BUTTON_PADDING, SpringLayout.EAST, panel);
        springLayout.putConstraint(SpringLayout.NORTH, coursesButton, 210, SpringLayout.NORTH, myProfileButton);
        panel.add(coursesButton);
        coursesButton.addActionListener(e -> {
            log.info("Going to Course GUI");
            MainGUI.this.dispose();
            new CourseGUI(user);
        });

        Clock clock = new Clock();
        springLayout.putConstraint(SpringLayout.WEST, clock, 10, SpringLayout.WEST, panel);
        springLayout.putConstraint(SpringLayout.NORTH, clock, 105, SpringLayout.NORTH, panel);
        panel.add(clock);

        this.getContentPane().add(panel);
    }

    private void backToLoginGUI() {
        FrameUtil.disposeCurrentFrameAndCreateNewFrame(UIConstants.APP_NAME, MainGUI.this, new LoginGUI(user));
    }
}
