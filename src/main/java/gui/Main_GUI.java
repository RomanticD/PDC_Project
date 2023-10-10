package gui;

import domain.User;

import javax.swing.*;

public class Main_GUI extends JFrame {
    private User user;

    public Main_GUI(User user) {
        this.user = user;
        init();
    }

    private void init() {
        this.setTitle("Welcome, " + user.getName() + "!");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
        this.setResizable(false);
        this.getContentPane().setLayout(null);
        this.setSize(700,500);
        this.setLocationRelativeTo(null);

        addComponents();
    }

    private void addComponents() {

    }
}
