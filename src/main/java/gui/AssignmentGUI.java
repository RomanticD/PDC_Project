package gui;

import domain.User;

import javax.swing.*;

public class AssignmentGUI extends JFrame {
    private final User user;
    private JPanel panel1;
    private JToolBar toolBar1;
    private JButton backButton;
    private JComboBox comboBox1;
    private JComboBox comboBox2;
    private JButton clearButton;
    private JButton submitButton;
    private JPanel panel2;
    private JLabel assignmentLabel;
    private JLabel programmeLabel;
    private JTextArea textArea1;
    private JTextArea textArea2;

    public AssignmentGUI(User user) {
        this.user = user;
        setContentPane(panel1);
        setTitle("welcome");
        setSize(500, 500);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
        backButton.addActionListener(e -> {
            new MainGUI(user);
            AssignmentGUI.this.dispose();
        });
    }
}
