package gui;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import service.UserService;
import service.dao.SubmissionDao;
import service.dao.UserDao;
import domain.Assignment;
import domain.Submission;
import domain.User;
import util.FrameUtil;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.FontUIResource;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.util.List;
import java.util.Locale;

import static util.FrameUtil.getRoundedBorder;

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
    private JScrollPane submissionsPanel;
    private JPanel mainPanel;
    private JSpinner scoresSpinner;
    private JPanel scoresPanel;
    private JButton clearButton;
    private JButton checkButton;
    private JPanel mainScoresPanel;
    private JButton downloadButton;
    private JPanel submissionPanel;
    private JLabel noContentLabel;
    private JLabel downloadLabel;

    SubmissionDao submissionDao = new SubmissionDao();

    // Click the Correct button in ManageAssignmentGUI
    public CorrectOrCheckGUI(User user, Assignment assignment) {
        CardLayout cardLayout = (CardLayout) cardPanel.getLayout();
        CardLayout scoresLayout = (CardLayout) scoresPanel.getLayout();

        setTitle("Correct an assignment");
        cardLabel.setText("Submissions:");
        cardLayout.show(cardPanel, "submissionCard");
        cardPanel.setBorder(getRoundedBorder());
        UserService userService = new UserDao();
        submissionContent.setEditable(false);

        submissionPanel.setBorder(getRoundedBorder());
        evaluationPanel.setBorder(getRoundedBorder());

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
                    userService.getUserById(submission.getStudentID()).getUsername(),
                    submission.getSubmissionTime(),
                    submission.getSubmissionOrder(),
                    submission.getSubmissionStatus()
            };
            tableModel.addRow(rowData);
        }
        TableColumn timeColumn = submissionTable.getColumnModel().getColumn(2);
        timeColumn.setCellRenderer(new SubmissionGUI.TimestampRenderer());

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        submissionTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        submissionTable.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        submissionTable.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);

        TableColumn column = submissionTable.getColumnModel().getColumn(0);
        column.setPreferredWidth(25);
        column = submissionTable.getColumnModel().getColumn(1);
        column.setPreferredWidth(50);
        column = submissionTable.getColumnModel().getColumn(3);
        column.setPreferredWidth(25);

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

        checkButton.addActionListener(e -> {
            int selectedRow = submissionTable.getSelectedRow();

            if (selectedRow >= 0) {
                CorrectOrCheckGUI.this.dispose();
                int studentID = (int) tableModel.getValueAt(selectedRow, 0);
                int submissionOrder = (int) tableModel.getValueAt(selectedRow, 3);
                Submission submission = submissionDao.getSubmissionFromTwoIDsAndOrder(assignment.getAssignmentID(), studentID, submissionOrder);

                new CorrectOrCheckGUI(user, assignment, submission);
            } else {
                FrameUtil.showConfirmation(CorrectOrCheckGUI.this, "You haven't select any submission!");
                new CorrectOrCheckGUI(user, assignment);
            }
        });

        clearButton.addActionListener(e -> evaluationContent.setText(""));

        scoresLayout.show(scoresPanel, "selectScoresCard");
        SpinnerNumberModel spinnerModel = new SpinnerNumberModel(0, 0, 100, 1);
        scoresSpinner.setModel(spinnerModel);
        JFormattedTextField scoresField = ((JSpinner.DefaultEditor) scoresSpinner.getEditor()).getTextField();
        FrameUtil.numericInputListener(scoresField);

        Dimension preferredSize = scoresSpinner.getPreferredSize();
        preferredSize.height += 50; // Increase the height as needed
        scoresSpinner.setPreferredSize(preferredSize);

        setContentPane(mainPanel);
        setSize(755, 600);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
        setLocationRelativeTo(null);
    }

    // Click the Check button in SubmissionGUI
    public CorrectOrCheckGUI(User user, Assignment assignment, Submission submission) {
        if (user.isAdmin()) {
            operationButton.setVisible(false);
        }

        CardLayout cardLayout = (CardLayout) cardPanel.getLayout();
        cardPanel.setBorder(getRoundedBorder());
        CardLayout evaluationLayout = (CardLayout) evaluationPanel.getLayout();
        CardLayout scoresLayout = (CardLayout) scoresPanel.getLayout();

        setTitle("Check out your assignment");
        cardLabel.setText(assignment.getAssignmentName());
        checkButton.setVisible(false);
        clearButton.setVisible(false);

        if (assignment.getAssignmentContent() == null || assignment.getAssignmentContent().isEmpty()) {
            cardLayout.show(cardPanel, "nullCard");
        } else {
            cardLayout.show(cardPanel, "assignmentCard");
            assignmentContent.setText(assignment.getAssignmentContent());
            assignmentContent.setEditable(false);
        }

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
            if (user.isAdmin()) {
                CorrectOrCheckGUI.this.dispose();
                new CorrectOrCheckGUI(user, assignment);
            } else {
                CorrectOrCheckGUI.this.dispose();
                new SubmissionGUI(user, assignment);
            }
        });

        scoresLayout.show(scoresPanel, "concreteScoresCard");
        if (submission.getScores() == -1) {
            concreteScores.setText("No scores yet");
        } else {
            concreteScores.setText(Integer.toString(submission.getScores()));
        }

        setContentPane(mainPanel);
        setSize(675, 600);
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
        mainPanel.setLayout(new GridLayoutManager(7, 4, new Insets(0, 0, 0, 0), -1, -1));
        final JToolBar toolBar1 = new JToolBar();
        mainPanel.add(toolBar1, new GridConstraints(0, 0, 1, 4, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(-1, 20), null, 0, false));
        backButton = new JButton();
        backButton.setBackground(new Color(-2104859));
        backButton.setEnabled(true);
        Font backButtonFont = this.$$$getFont$$$("JetBrains Mono", Font.BOLD, 18, backButton.getFont());
        if (backButtonFont != null) backButton.setFont(backButtonFont);
        backButton.setForeground(new Color(-15526864));
        backButton.setText("Back");
        toolBar1.add(backButton);
        final Spacer spacer1 = new Spacer();
        toolBar1.add(spacer1);
        clearButton = new JButton();
        clearButton.setBackground(new Color(-2104859));
        Font clearButtonFont = this.$$$getFont$$$("JetBrains Mono", Font.BOLD, 16, clearButton.getFont());
        if (clearButtonFont != null) clearButton.setFont(clearButtonFont);
        clearButton.setForeground(new Color(-15526864));
        clearButton.setText("Clear");
        toolBar1.add(clearButton);
        operationButton = new JButton();
        operationButton.setBackground(new Color(-2104859));
        Font operationButtonFont = this.$$$getFont$$$("JetBrains Mono", Font.BOLD, 16, operationButton.getFont());
        if (operationButtonFont != null) operationButton.setFont(operationButtonFont);
        operationButton.setForeground(new Color(-15526864));
        operationButton.setText("Operate");
        toolBar1.add(operationButton);
        submissionContentLabel = new JLabel();
        Font submissionContentLabelFont = this.$$$getFont$$$("Droid Sans Mono", Font.PLAIN, 16, submissionContentLabel.getFont());
        if (submissionContentLabelFont != null) submissionContentLabel.setFont(submissionContentLabelFont);
        submissionContentLabel.setText("Evaluation:");
        mainPanel.add(submissionContentLabel, new GridConstraints(1, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        cardPanel = new JPanel();
        cardPanel.setLayout(new CardLayout(0, 0));
        cardPanel.setBackground(new Color(-15526864));
        mainPanel.add(cardPanel, new GridConstraints(2, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(-1, 150), new Dimension(150, 150), new Dimension(-1, 200), 0, false));
        assignmentContent = new JTextArea();
        assignmentContent.setBackground(new Color(-6837066));
        Font assignmentContentFont = this.$$$getFont$$$("Monaco", Font.PLAIN, 14, assignmentContent.getFont());
        if (assignmentContentFont != null) assignmentContent.setFont(assignmentContentFont);
        assignmentContent.setText("");
        cardPanel.add(assignmentContent, "assignmentCard");
        submissionsPanel = new JScrollPane();
        cardPanel.add(submissionsPanel, "submissionCard");
        submissionTable = new JTable();
        submissionTable.setBackground(new Color(-6837066));
        submissionTable.setForeground(new Color(-15526864));
        submissionsPanel.setViewportView(submissionTable);
        noContentLabel = new JLabel();
        Font noContentLabelFont = this.$$$getFont$$$("Droid Sans Mono Slashed", Font.BOLD | Font.ITALIC, 20, noContentLabel.getFont());
        if (noContentLabelFont != null) noContentLabel.setFont(noContentLabelFont);
        noContentLabel.setHorizontalAlignment(0);
        noContentLabel.setHorizontalTextPosition(0);
        noContentLabel.setText("No content");
        cardPanel.add(noContentLabel, "nullCard");
        cardLabel = new JLabel();
        Font cardLabelFont = this.$$$getFont$$$("Droid Sans Mono", Font.PLAIN, 16, cardLabel.getFont());
        if (cardLabelFont != null) cardLabel.setFont(cardLabelFont);
        cardLabel.setText("Card content");
        mainPanel.add(cardLabel, new GridConstraints(1, 0, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        evaluationPanel = new JPanel();
        evaluationPanel.setLayout(new CardLayout(0, 0));
        evaluationPanel.setBackground(new Color(-15526864));
        evaluationPanel.setForeground(new Color(-6253482));
        mainPanel.add(evaluationPanel, new GridConstraints(2, 3, 3, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(250, 300), new Dimension(300, 350), null, 0, false));
        evaluationContent = new JTextArea();
        evaluationContent.setBackground(new Color(-3618616));
        Font evaluationContentFont = this.$$$getFont$$$("Monaco", Font.PLAIN, 14, evaluationContent.getFont());
        if (evaluationContentFont != null) evaluationContent.setFont(evaluationContentFont);
        evaluationContent.setForeground(new Color(-15526864));
        evaluationPanel.add(evaluationContent, "Card1");
        nullLabel = new JLabel();
        nullLabel.setBackground(new Color(-4474633));
        Font nullLabelFont = this.$$$getFont$$$("Droid Sans Mono Slashed", Font.BOLD | Font.ITALIC, 20, nullLabel.getFont());
        if (nullLabelFont != null) nullLabel.setFont(nullLabelFont);
        nullLabel.setForeground(new Color(-2104859));
        nullLabel.setHorizontalAlignment(0);
        nullLabel.setHorizontalTextPosition(0);
        nullLabel.setText("No content");
        evaluationPanel.add(nullLabel, "Card2");
        submissionLabel = new JLabel();
        Font submissionLabelFont = this.$$$getFont$$$("Droid Sans Mono", Font.PLAIN, 16, submissionLabel.getFont());
        if (submissionLabelFont != null) submissionLabel.setFont(submissionLabelFont);
        submissionLabel.setText("Submission content:");
        mainPanel.add(submissionLabel, new GridConstraints(3, 0, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(125, 13), null, 0, false));
        mainScoresPanel = new JPanel();
        mainScoresPanel.setLayout(new GridLayoutManager(1, 3, new Insets(10, 10, 10, 10), -1, -1));
        mainScoresPanel.setBackground(new Color(-6837066));
        mainScoresPanel.setForeground(new Color(-1727412));
        mainPanel.add(mainScoresPanel, new GridConstraints(5, 3, 2, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, new Dimension(-1, 40), 0, false));
        scoresLabel = new JLabel();
        scoresLabel.setBackground(new Color(-6837066));
        Font scoresLabelFont = this.$$$getFont$$$("Droid Sans Mono", Font.PLAIN, 18, scoresLabel.getFont());
        if (scoresLabelFont != null) scoresLabel.setFont(scoresLabelFont);
        scoresLabel.setForeground(new Color(-15526864));
        scoresLabel.setHorizontalAlignment(0);
        scoresLabel.setHorizontalTextPosition(0);
        scoresLabel.setText("Scores:");
        mainScoresPanel.add(scoresLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(50, -1), null, 0, false));
        scoresPanel = new JPanel();
        scoresPanel.setLayout(new CardLayout(0, 0));
        scoresPanel.setForeground(new Color(-2238126));
        mainScoresPanel.add(scoresPanel, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, new Dimension(-1, 50), 0, false));
        scoresSpinner = new JSpinner();
        Font scoresSpinnerFont = this.$$$getFont$$$("Droid Sans Mono", Font.BOLD, 16, scoresSpinner.getFont());
        if (scoresSpinnerFont != null) scoresSpinner.setFont(scoresSpinnerFont);
        scoresSpinner.setInheritsPopupMenu(true);
        scoresSpinner.setMaximumSize(new Dimension(120, 75));
        scoresSpinner.setMinimumSize(new Dimension(100, 25));
        scoresSpinner.setPreferredSize(new Dimension(100, 50));
        scoresSpinner.setRequestFocusEnabled(true);
        scoresPanel.add(scoresSpinner, "selectedScoresCard");
        concreteScores = new JLabel();
        concreteScores.setEnabled(true);
        Font concreteScoresFont = this.$$$getFont$$$("Droid Sans Mono", Font.BOLD, 16, concreteScores.getFont());
        if (concreteScoresFont != null) concreteScores.setFont(concreteScoresFont);
        concreteScores.setHorizontalAlignment(0);
        concreteScores.setHorizontalTextPosition(0);
        concreteScores.setText("");
        scoresPanel.add(concreteScores, "concreteScoresCard");
        final Spacer spacer2 = new Spacer();
        mainScoresPanel.add(spacer2, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        downloadButton = new JButton();
        downloadButton.setBackground(new Color(-2104859));
        Font downloadButtonFont = this.$$$getFont$$$("JetBrains Mono", Font.BOLD, 16, downloadButton.getFont());
        if (downloadButtonFont != null) downloadButton.setFont(downloadButtonFont);
        downloadButton.setForeground(new Color(-15526864));
        downloadButton.setText("Download");
        mainPanel.add(downloadButton, new GridConstraints(6, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        submissionPanel = new JPanel();
        submissionPanel.setLayout(new BorderLayout(0, 0));
        submissionPanel.setBackground(new Color(-13947600));
        submissionPanel.setForeground(new Color(-4474633));
        mainPanel.add(submissionPanel, new GridConstraints(4, 0, 2, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(-1, 150), null, 0, false));
        submissionPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLoweredBevelBorder(), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        submissionContent = new JTextArea();
        submissionContent.setBackground(new Color(-6837066));
        submissionContent.setFocusCycleRoot(true);
        Font submissionContentFont = this.$$$getFont$$$("Monaco", Font.PLAIN, 14, submissionContent.getFont());
        if (submissionContentFont != null) submissionContent.setFont(submissionContentFont);
        submissionContent.setForeground(new Color(-15526864));
        submissionContent.setLineWrap(true);
        submissionContent.setWrapStyleWord(true);
        submissionPanel.add(submissionContent, BorderLayout.CENTER);
        checkButton = new JButton();
        checkButton.setBackground(new Color(-2104859));
        Font checkButtonFont = this.$$$getFont$$$("JetBrains Mono", Font.BOLD, 16, checkButton.getFont());
        if (checkButtonFont != null) checkButton.setFont(checkButtonFont);
        checkButton.setForeground(new Color(-15526864));
        checkButton.setText("Check");
        mainPanel.add(checkButton, new GridConstraints(6, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        downloadLabel = new JLabel();
        Font downloadLabelFont = this.$$$getFont$$$("Droid Sans Mono", Font.PLAIN, 12, downloadLabel.getFont());
        if (downloadLabelFont != null) downloadLabel.setFont(downloadLabelFont);
        downloadLabel.setHorizontalAlignment(4);
        downloadLabel.setHorizontalTextPosition(4);
        downloadLabel.setText("Download the uploaded file: ");
        mainPanel.add(downloadLabel, new GridConstraints(6, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont) {
        if (currentFont == null) return null;
        String resultName;
        if (fontName == null) {
            resultName = currentFont.getName();
        } else {
            Font testFont = new Font(fontName, Font.PLAIN, 10);
            if (testFont.canDisplay('a') && testFont.canDisplay('1')) {
                resultName = fontName;
            } else {
                resultName = currentFont.getName();
            }
        }
        Font font = new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
        boolean isMac = System.getProperty("os.name", "").toLowerCase(Locale.ENGLISH).startsWith("mac");
        Font fontWithFallback = isMac ? new Font(font.getFamily(), font.getStyle(), font.getSize()) : new StyleContext().getFont(font.getFamily(), font.getStyle(), font.getSize());
        return fontWithFallback instanceof FontUIResource ? fontWithFallback : new FontUIResource(fontWithFallback);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }

}
