package gui;

import constants.UIConstants;
import dao.UserDao;
import domain.Role;
import domain.User;
import gui.sub.BackgroundPanel;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

public class Main_GUI extends JFrame {
    private User user;
    
    private JPanel panel;

    private JFrame frame;

    public Main_GUI(User user) {
        this.user = user;
        this.setTitle("Welcome, " + user.getRole() + " " + user.getName() + " !");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
        this.setResizable(false);
        this.setSize(320,480);
        this.setLocationRelativeTo(null);
        this.panel = getBackgroundPanel();

        addComponents(Objects.requireNonNull(getBackgroundPanel()));
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

        JButton myProfileButton = new JButton("Profile");
        myProfileButton.setFont(new Font("Dialog", Font.BOLD, 20));
        springLayout.putConstraint(SpringLayout.WEST, myProfileButton, 75, SpringLayout.WEST, panel);
        springLayout.putConstraint(SpringLayout.EAST, myProfileButton, -75, SpringLayout.EAST, panel);
        springLayout.putConstraint(SpringLayout.NORTH, myProfileButton, 105, SpringLayout.NORTH, panel);
        panel.add(myProfileButton);

        
        if (this.user.getRole() == Role.ADMIN){
            JButton adminButton = new JButton("Admin");
            adminButton.setFont(new Font("Dialog", Font.BOLD, 20));
            springLayout.putConstraint(SpringLayout.WEST, adminButton, 75, SpringLayout.WEST, panel);
            springLayout.putConstraint(SpringLayout.EAST, adminButton, -75, SpringLayout.EAST, panel);
            springLayout.putConstraint(SpringLayout.NORTH, adminButton, 210, SpringLayout.NORTH, panel);
            panel.add(adminButton);
        }else{
            JButton assignmentButton = new JButton("Assignment");
            assignmentButton.setFont(new Font("Dialog", Font.BOLD, 20));
            springLayout.putConstraint(SpringLayout.WEST, assignmentButton, 75, SpringLayout.WEST, panel);
            springLayout.putConstraint(SpringLayout.EAST, assignmentButton, -75, SpringLayout.EAST, panel);
            springLayout.putConstraint(SpringLayout.NORTH, assignmentButton, 210, SpringLayout.NORTH, panel);
            panel.add(assignmentButton);
        }
        

        JButton coursesButton = new JButton("Course");
        coursesButton.setFont(new Font("Dialog", Font.BOLD, 20));
        springLayout.putConstraint(SpringLayout.WEST, coursesButton, 75, SpringLayout.WEST, panel);
        springLayout.putConstraint(SpringLayout.EAST, coursesButton, -75, SpringLayout.EAST, panel);
        springLayout.putConstraint(SpringLayout.NORTH, coursesButton, 315, SpringLayout.NORTH, panel);
        panel.add(coursesButton);
        coursesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Main_GUI.this.dispose();
                new Course_GUI(user);
            }
        });

        this.getContentPane().add(panel);
    }
}
