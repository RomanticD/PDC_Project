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
public class AdminGUI extends JFrame {
    private  User user;
    public AdminGUI(User user){
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

    private void load(){

    }
    private void addComponents(JPanel panel) {
        SpringLayout springLayout = new SpringLayout();
        panel.setLayout(springLayout);

        ActionListener backToLoginGUI = e -> backToMainGUI();
        FrameUtil.addBackButtonWithCustomAction(panel, springLayout, backToLoginGUI);

        //Assignment Manage Button
        JButton myProfileButton = addButton("Manage Assignment", panel, panel, 30, springLayout);
        myProfileButton.addActionListener(e -> {
            log.info("Going to Assignment GUI");
            AdminGUI.this.dispose();
            new ProfileGUI(user);
        });


        //Course Manage Button
        JButton adminButton = addButton("Manage Course", panel, myProfileButton, 90, springLayout);
        adminButton.addActionListener(e -> {
            log.info("Going to Manage Course GUI");
            AdminGUI.this.dispose();
            new SelectAssignmentGUI(user);
        });


        //Course Button
        JButton coursesButton = addButton("Manage Course Selection", panel, myProfileButton, 150, springLayout);
        coursesButton.addActionListener(e -> {
            log.info("Going to Manage Course Selection");
            AdminGUI.this.dispose();
            new CourseGUI(user);
        });

        //Clock to display current time
        Clock clock = new Clock();
        springLayout.putConstraint(SpringLayout.WEST, clock, 10, SpringLayout.WEST, panel);
        springLayout.putConstraint(SpringLayout.NORTH, clock, (UIConstants.MAIN_GUI_FRAME_SIZE[1] - UIConstants.CLOCK_SIZE) / 2, SpringLayout.NORTH, panel);
        panel.add(clock);

        this.getContentPane().add(panel);
    }

    private void backToMainGUI() {
    }


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
