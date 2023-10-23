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
        this.setSize(UIConstants.MAIN_GUI_FRAME_SIZE[0],UIConstants.MAIN_GUI_FRAME_SIZE[1]);
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

        //Profile Button
        JButton myProfileButton = addButton("Profile", panel, panel, 105, springLayout);
        myProfileButton.addActionListener(e -> {
            log.info("Going to Profile GUI");
            MainGUI.this.dispose();
            new ProfileGUI(user);
        });

        
        if (user.isAdmin()){
            //Admin Button if user is admin
            JButton adminButton = addButton("Admin", panel, myProfileButton, 105, springLayout);
            adminButton.addActionListener(e -> {
                MainGUI.this.dispose();
                new SelectAssignmentGUI(user);
            });
        }else{
            //Assignment Button if user is student
            JButton assignmentButton = addButton("Assignment", panel, myProfileButton, 105, springLayout);
            assignmentButton.addActionListener(e -> {
                MainGUI.this.dispose();
                new SelectAssignmentGUI(user);
            });
        }

        //Course Button
        JButton coursesButton = addButton("Course", panel, myProfileButton, 210, springLayout);
        coursesButton.addActionListener(e -> {
            log.info("Going to Course GUI");
            MainGUI.this.dispose();
            new CourseGUI(user);
        });

        //Clock to display current time
        Clock clock = new Clock();
        springLayout.putConstraint(SpringLayout.WEST, clock, 10, SpringLayout.WEST, panel);
        springLayout.putConstraint(SpringLayout.NORTH, clock, (UIConstants.MAIN_GUI_FRAME_SIZE[1] - UIConstants.CLOCK_SIZE) / 2, SpringLayout.NORTH, panel);
        panel.add(clock);

        this.getContentPane().add(panel);
    }

    private void backToLoginGUI() {
        FrameUtil.disposeCurrentFrameAndCreateNewFrame(UIConstants.APP_NAME, MainGUI.this, new LoginGUI(user));
    }

    private JButton addButton(String name, JComponent container, JComponent verticalRelatedComponent, int topPaddingToVerticalComponent, SpringLayout springLayout){
        JButton button = new JButton(name);
        button.setFont(new Font("Dialog", Font.BOLD, 20));
        springLayout.putConstraint(SpringLayout.WEST, button, UIConstants.MAIN_PAGE_BUTTON_LEADING_PADDING, SpringLayout.WEST, container);
        springLayout.putConstraint(SpringLayout.EAST, button, UIConstants.MAIN_PAGE_BUTTON_BOTTOM_PADDING, SpringLayout.EAST, container);
        springLayout.putConstraint(SpringLayout.NORTH, button, topPaddingToVerticalComponent, SpringLayout.NORTH, verticalRelatedComponent);
        container.add(button);

        return button;
    }
}
