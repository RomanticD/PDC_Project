package gui;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import dao.UserDaoInterface;
import dao.impl.SubmissionDao;
import dao.impl.UserDao;
import domain.Assignment;
import domain.Submission;
import domain.User;
import util.FrameUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.util.List;

public class CorrectOrCheckGUI extends JFrame {
    private JButton backButton;
    private JButton operationButton;
    private JLabel concreteScores;
    private JLabel scoresLabel;
    private JLabel submissionContentLabel;
    private JLabel cardLabel;
    private JTextArea assignmentContent;
    private JTable submissionTable;
    private JTextArea evaluationContent;
    private JLabel nullLabel;
    private JTextArea submissionContent;
    private JLabel submissionLabel;
    private JPanel evaluationPanel;
    private JPanel cardPanel;
    private JScrollPane submissionPanel;
    private JPanel mainPanel;
    private JSpinner scoresSpinner;
    private JPanel scoresPanel;
    private JButton clearButton;

    SubmissionDao submissionDao = new SubmissionDao();

    CardLayout cardLayout = (CardLayout) cardPanel.getLayout();

    CardLayout evaluationLayout = (CardLayout) evaluationPanel.getLayout();

    CardLayout scoresLayout = (CardLayout) scoresPanel.getLayout();

    // Click the Correct button in ManageAssignmentGUI
    public CorrectOrCheckGUI(User user, Assignment assignment) {
        setTitle("Correct an assignment");
        cardLabel.setText("Submissions:");
        cardLayout.show(cardPanel, "submissionCard");
        UserDaoInterface userDao = new UserDao();

        DefaultTableModel tableModel = new DefaultTableModel();
        submissionTable.setModel(tableModel);
        tableModel.addColumn("StudentID");
        tableModel.addColumn("Name");
        tableModel.addColumn("Time");
        tableModel.addColumn("Order");
        tableModel.addColumn("Status");

        List<Submission> submissionList = submissionDao.getSubmissionsFromAssignment(assignment);
        for (Submission submission : submissionList) {
            Object[] rowData = {
                    submission.getStudentID(),
                    userDao.getUserById(submission.getStudentID()).getUsername(),
                    submission.getSubmissionTime(),
                    submission.getSubmissionOrder(),
                    submission.getSubmissionStatus()
            };
            tableModel.addRow(rowData);
        }
        TableColumn timeColumn = submissionTable.getColumnModel().getColumn(2);
        timeColumn.setCellRenderer(new SubmissionGUI.TimestampRenderer());

        submissionContent.setEditable(false);


        submissionTable.getSelectionModel().addListSelectionListener(e -> {
            int selectedRow = submissionTable.getSelectedRow();

            if (selectedRow != -1) {
                int studentID = (int) tableModel.getValueAt(selectedRow, 0);
                int submissionOrder = (int) tableModel.getValueAt(selectedRow, 3);
                Submission submission = submissionDao.getSubmissionFromTwoIDsAndOrder(assignment.getAssignmentID(), studentID, submissionOrder);
                submissionContent.setText(submission.getSubmissionContent());
            }
        });

        operationButton.setText("Correct");
        operationButton.addActionListener(e -> {
            int selectedRow = submissionTable.getSelectedRow();

            if (selectedRow >= 0) {
                int studentID = (int) tableModel.getValueAt(selectedRow, 0);
                int submissionOrder = (int) tableModel.getValueAt(selectedRow, 3);
                Submission submission = submissionDao.getSubmissionFromTwoIDsAndOrder(assignment.getAssignmentID(), studentID, submissionOrder);
                submission.setEvaluation(evaluationContent.getText());
                submission.setScores((int) scoresSpinner.getValue());

                if (submissionDao.correctSubmission(submission)) {
                    FrameUtil.showConfirmation(CorrectOrCheckGUI.this, "Correct successfully");
                } else {
                    FrameUtil.showConfirmation(CorrectOrCheckGUI.this, "Something wrong");
                }
            } else {
                FrameUtil.showConfirmation(CorrectOrCheckGUI.this, "You haven't select any submission!");
            }
            new CorrectOrCheckGUI(user, assignment);
        });

        backButton.addActionListener(e -> {
            CorrectOrCheckGUI.this.dispose();
            new SelectAssignmentGUI(user);
        });

        clearButton.addActionListener(e -> {
            evaluationContent.setText("");
        });

        scoresLayout.show(scoresPanel, "selectScoresCard");
        SpinnerNumberModel spinnerModel = new SpinnerNumberModel(0, 0, 100, 1);
        scoresSpinner.setModel(spinnerModel);
        JFormattedTextField scoresField = ((JSpinner.DefaultEditor) scoresSpinner.getEditor()).getTextField();
        FrameUtil.numericInputListener(scoresField);

        setContentPane(mainPanel);
        setSize(800, 500);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
        setLocationRelativeTo(null);
    }

    // Click the Check button in SubmissionGUI
    public CorrectOrCheckGUI(User user, Assignment assignment, int submissionOrder) {
        setTitle("Check out your assignment");
        cardLabel.setText(assignment.getAssignmentName());
        cardLayout.show(cardPanel, "assignmentCard");
        assignmentContent.setText(assignment.getAssignmentContent());
        assignmentContent.setEditable(false);
        clearButton.setVisible(false);

        Submission submission = submissionDao.getSubmissionFromTwoIDsAndOrder(assignment.getAssignmentID(), user.getUserId(), submissionOrder);
        submissionContent.setText(submission.getSubmissionContent());
        submissionContent.setEditable(false);

        operationButton.setText("Delete");
        if (submission.getSubmissionStatus().matches("Already corrected")) {
            operationButton.setVisible(false);
        }
        operationButton.addActionListener(e -> {
            if (submissionDao.deleteSubmission(submission)) {
                FrameUtil.showConfirmation(CorrectOrCheckGUI.this, "Delete successfully!");
            } else {
                FrameUtil.showConfirmation(CorrectOrCheckGUI.this, "Something wrong!");
            }
            new SubmissionGUI(user, assignment);
        });

        if (submission.getEvaluation() == null || submission.getEvaluation().isEmpty()) {
            evaluationLayout.show(evaluationPanel, "nullCard");
        } else {
            evaluationLayout.show(evaluationPanel, "evaluationCard");
            evaluationContent.setText(submission.getEvaluation());
            evaluationContent.setEditable(false);
        }

        backButton.addActionListener(e -> {
            CorrectOrCheckGUI.this.dispose();
            new SubmissionGUI(user, assignment);
        });

        scoresLayout.show(scoresPanel, "concreteScoresCard");
        if (submission.getScores() == -1) {
            concreteScores.setText("No scores yet");
        } else {
            concreteScores.setText(Integer.toString(submission.getScores()));
        }

        setContentPane(mainPanel);
        setSize(600, 500);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
        setLocationRelativeTo(null);
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayoutManager(6, 3, new Insets(0, 0, 0, 0), -1, -1));
        final JToolBar toolBar1 = new JToolBar();
        mainPanel.add(toolBar1, new GridConstraints(0, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(-1, 20), null, 0, false));
        backButton = new JButton();
        backButton.setText("Back");
        toolBar1.add(backButton);
        final Spacer spacer1 = new Spacer();
        toolBar1.add(spacer1);
        clearButton = new JButton();
        clearButton.setText("Clear");
        toolBar1.add(clearButton);
        operationButton = new JButton();
        operationButton.setText("Operate");
        toolBar1.add(operationButton);
        submissionContentLabel = new JLabel();
        submissionContentLabel.setText("Evaluation:");
        mainPanel.add(submissionContentLabel, new GridConstraints(1, 1, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        scoresLabel = new JLabel();
        scoresLabel.setText("Scores:");
        mainPanel.add(scoresLabel, new GridConstraints(5, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(50, 17), null, 0, false));
        cardPanel = new JPanel();
        cardPanel.setLayout(new CardLayout(0, 0));
        mainPanel.add(cardPanel, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(200, -1), new Dimension(250, 24), null, 0, false));
        assignmentContent = new JTextArea();
        assignmentContent.setText("");
        cardPanel.add(assignmentContent, "assignmentCard");
        submissionPanel = new JScrollPane();
        cardPanel.add(submissionPanel, "submissionCard");
        submissionTable = new JTable();
        submissionPanel.setViewportView(submissionTable);
        cardLabel = new JLabel();
        cardLabel.setText("Card content");
        mainPanel.add(cardLabel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        evaluationPanel = new JPanel();
        evaluationPanel.setLayout(new CardLayout(0, 0));
        mainPanel.add(evaluationPanel, new GridConstraints(2, 1, 3, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(200, -1), new Dimension(225, 200), null, 0, false));
        evaluationContent = new JTextArea();
        evaluationPanel.add(evaluationContent, "evaluationCard");
        nullLabel = new JLabel();
        nullLabel.setHorizontalAlignment(0);
        nullLabel.setHorizontalTextPosition(0);
        nullLabel.setText("No content");
        evaluationPanel.add(nullLabel, "nullCard");
        submissionContent = new JTextArea();
        mainPanel.add(submissionContent, new GridConstraints(4, 0, 2, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(-1, 50), null, 0, false));
        submissionLabel = new JLabel();
        submissionLabel.setText("Submission content:");
        mainPanel.add(submissionLabel, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        scoresPanel = new JPanel();
        scoresPanel.setLayout(new CardLayout(0, 0));
        mainPanel.add(scoresPanel, new GridConstraints(5, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, new Dimension(-1, 20), 0, false));
        concreteScores = new JLabel();
        concreteScores.setText("");
        scoresPanel.add(concreteScores, "concreteScoresCard");
        scoresSpinner = new JSpinner();
        scoresPanel.add(scoresSpinner, "selectScoresCard");
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }
}
