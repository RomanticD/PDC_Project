package gui;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import dao.SubmissionDaoInterface;
import domain.Assignment;
import domain.Submission;
import domain.User;
import dao.impl.SubmissionDao;
import util.FrameUtil;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.text.*;
import java.util.List;
import java.util.Date;
import java.util.Locale;

import static util.FrameUtil.getRoundedBorder;

public class SubmissionGUI extends JFrame {
    private JPanel submissionPanel;
    private JToolBar toolBar1;
    private JButton backButton;
    private JButton clearButton;
    private JButton submitButton;
    private JPanel mainPanel;
    private JLabel assignmentLabel;
    private JLabel programmeLabel;
    private JTextArea submissionContent;
    private JTextArea assignmentContent;
    private JTable submissionTable;
    private JLabel historyLabel;
    private JPanel assignmentCard;
    private JLabel nullContentLabel;
    private JButton checkHistoryButton;
    private JButton uploadButton;
    private JLabel explainLabel;

    public SubmissionGUI(User user, Assignment assignment) {
        SubmissionDaoInterface submissionDao = new SubmissionDao();


        // The setting of assignmentLabel and assignmentCard, which is shown for assignment content or No content.
        assignmentLabel.setText(assignment.getAssignmentName());
        assignmentCard.setBorder(getRoundedBorder());
        CardLayout cardLayout = (CardLayout) assignmentCard.getLayout();
        if (assignment.getAssignmentContent() == null || assignment.getAssignmentContent().isEmpty()) {
            cardLayout.show(assignmentCard, "nullContentCard");
        } else {
            cardLayout.show(assignmentCard, "contentCard");
            assignmentContent.setText(assignment.getAssignmentContent());
        }
        assignmentContent.setEditable(false);


        // The four main function buttons.
        backButton.addActionListener(e -> {
            SubmissionGUI.this.dispose();
            new SelectAssignmentGUI(user);
        });

        uploadButton.addActionListener(FrameUtil.uploadAction());

        clearButton.addActionListener(e -> submissionContent.setText(""));

        submitButton.addActionListener(e -> {
            Submission newSubmission = Submission.builder()
                    .submissionContent(submissionContent.getText())
                    .assignmentID(assignment.getAssignmentID())
                    .studentID(user.getUserId())
                    .build();

            if (submissionDao.insertSubmission(newSubmission)) {
                FrameUtil.showConfirmation(SubmissionGUI.this, "Submit successfully!");
            } else {
                FrameUtil.showConfirmation(SubmissionGUI.this, "Something wrong!");
            }
            new SubmissionGUI(user, assignment);
        });


        // The setting of the history table
        DefaultTableModel tableModel = new DefaultTableModel();
        submissionTable.setModel(tableModel);
        tableModel.addColumn("Order");
        tableModel.addColumn("Time");
        tableModel.addColumn("Status");
        TableColumn orderColumn = submissionTable.getColumnModel().getColumn(0);
        orderColumn.setPreferredWidth(5);
        orderColumn = submissionTable.getColumnModel().getColumn(1);
        orderColumn.setPreferredWidth(80);

        String[] rowName = {
                "Order",
                "Time",
                "Status"
        };
        tableModel.addRow(rowName);

        List<Submission> submissionList = submissionDao.getSubmissionsOfOneAssignmentAndStudent(assignment.getAssignmentID(), user.getUserId());
        for (Submission submission : submissionList) {
            Object[] rowData = {
                    submission.getSubmissionOrder(),
                    submission.getSubmissionTime(),
                    submission.getSubmissionStatus()

            };
            tableModel.addRow(rowData);
        }
        TableColumn timeColumn = submissionTable.getColumnModel().getColumn(1);
        timeColumn.setCellRenderer(new TimestampRenderer());

        checkHistoryButton.addActionListener(e -> {
            int selectedRow = submissionTable.getSelectedRow();
            if (selectedRow >= 1) {
                SubmissionGUI.this.dispose();
                int submissionOrder = (int) tableModel.getValueAt(selectedRow, 0);
                Submission submission = submissionDao.getSubmissionFromTwoIDsAndOrder(assignment.getAssignmentID(), user.getUserId(), submissionOrder);

                new CorrectOrCheckGUI(user, assignment, submission);
            } else {
                FrameUtil.showConfirmation(SubmissionGUI.this, "You haven't select any submission!");
                new SubmissionGUI(user, assignment);
            }
        });


        setContentPane(submissionPanel);
        setTitle("Submit your assignment or manage your history");
        setSize(750, 600);
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
        submissionPanel = new JPanel();
        submissionPanel.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        toolBar1 = new JToolBar();
        submissionPanel.add(toolBar1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(-1, 20), null, 0, false));
        backButton = new JButton();
        backButton.setBackground(new Color(-2104859));
        Font backButtonFont = this.$$$getFont$$$("JetBrains Mono", Font.BOLD, 16, backButton.getFont());
        if (backButtonFont != null) backButton.setFont(backButtonFont);
        backButton.setForeground(new Color(-15526864));
        backButton.setText("Back");
        toolBar1.add(backButton);
        explainLabel = new JLabel();
        Font explainLabelFont = this.$$$getFont$$$("Droid Sans Mono Dotted", Font.PLAIN, 14, explainLabel.getFont());
        if (explainLabelFont != null) explainLabel.setFont(explainLabelFont);
        explainLabel.setForeground(new Color(-2238126));
        explainLabel.setText(" Write your assignment, or upload files.");
        toolBar1.add(explainLabel);
        final Spacer spacer1 = new Spacer();
        toolBar1.add(spacer1);
        uploadButton = new JButton();
        uploadButton.setBackground(new Color(-2104859));
        Font uploadButtonFont = this.$$$getFont$$$("JetBrains Mono", Font.BOLD, 16, uploadButton.getFont());
        if (uploadButtonFont != null) uploadButton.setFont(uploadButtonFont);
        uploadButton.setForeground(new Color(-15526864));
        uploadButton.setText("Upload");
        toolBar1.add(uploadButton);
        clearButton = new JButton();
        clearButton.setBackground(new Color(-2104859));
        Font clearButtonFont = this.$$$getFont$$$("JetBrains Mono", Font.BOLD, 16, clearButton.getFont());
        if (clearButtonFont != null) clearButton.setFont(clearButtonFont);
        clearButton.setForeground(new Color(-15526864));
        clearButton.setText("Clear");
        toolBar1.add(clearButton);
        submitButton = new JButton();
        submitButton.setBackground(new Color(-2104859));
        Font submitButtonFont = this.$$$getFont$$$("JetBrains Mono", Font.BOLD, 16, submitButton.getFont());
        if (submitButtonFont != null) submitButton.setFont(submitButtonFont);
        submitButton.setForeground(new Color(-15526864));
        submitButton.setText("Submit");
        toolBar1.add(submitButton);
        mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayoutManager(4, 4, new Insets(0, 0, 0, 0), -1, -1));
        submissionPanel.add(mainPanel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        assignmentLabel = new JLabel();
        Font assignmentLabelFont = this.$$$getFont$$$("Droid Sans Mono", Font.PLAIN, 16, assignmentLabel.getFont());
        if (assignmentLabelFont != null) assignmentLabel.setFont(assignmentLabelFont);
        assignmentLabel.setText("Assignment name");
        mainPanel.add(assignmentLabel, new GridConstraints(0, 0, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        programmeLabel = new JLabel();
        Font programmeLabelFont = this.$$$getFont$$$("Droid Sans Mono", Font.PLAIN, 16, programmeLabel.getFont());
        if (programmeLabelFont != null) programmeLabel.setFont(programmeLabelFont);
        programmeLabel.setText("Your submission contents:");
        mainPanel.add(programmeLabel, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(305, 17), null, 0, false));
        submissionTable = new JTable();
        submissionTable.setBackground(new Color(-11482559));
        Font submissionTableFont = this.$$$getFont$$$("Droid Sans Mono", Font.PLAIN, 14, submissionTable.getFont());
        if (submissionTableFont != null) submissionTable.setFont(submissionTableFont);
        submissionTable.setForeground(new Color(-15526864));
        mainPanel.add(submissionTable, new GridConstraints(3, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(150, 150), null, 0, false));
        historyLabel = new JLabel();
        Font historyLabelFont = this.$$$getFont$$$("Droid Sans Mono", Font.PLAIN, 16, historyLabel.getFont());
        if (historyLabelFont != null) historyLabel.setFont(historyLabelFont);
        historyLabel.setText("Your history:");
        mainPanel.add(historyLabel, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        assignmentCard = new JPanel();
        assignmentCard.setLayout(new CardLayout(0, 0));
        assignmentCard.setForeground(new Color(-4474633));
        mainPanel.add(assignmentCard, new GridConstraints(1, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(150, 250), new Dimension(-1, 250), 0, false));
        nullContentLabel = new JLabel();
        Font nullContentLabelFont = this.$$$getFont$$$("Droid Sans Mono Slashed", Font.BOLD | Font.ITALIC, 20, nullContentLabel.getFont());
        if (nullContentLabelFont != null) nullContentLabel.setFont(nullContentLabelFont);
        nullContentLabel.setHorizontalAlignment(0);
        nullContentLabel.setHorizontalTextPosition(0);
        nullContentLabel.setText("No content");
        assignmentCard.add(nullContentLabel, "nullContentCard");
        assignmentContent = new JTextArea();
        assignmentContent.setBackground(new Color(-4474633));
        Font assignmentContentFont = this.$$$getFont$$$("Monaco", Font.PLAIN, 14, assignmentContent.getFont());
        if (assignmentContentFont != null) assignmentContent.setFont(assignmentContentFont);
        assignmentContent.setForeground(new Color(-16777216));
        assignmentCard.add(assignmentContent, "contentCard");
        checkHistoryButton = new JButton();
        checkHistoryButton.setBackground(new Color(-2104859));
        Font checkHistoryButtonFont = this.$$$getFont$$$("JetBrains Mono", Font.BOLD, 16, checkHistoryButton.getFont());
        if (checkHistoryButtonFont != null) checkHistoryButton.setFont(checkHistoryButtonFont);
        checkHistoryButton.setForeground(new Color(-15526864));
        checkHistoryButton.setHorizontalAlignment(0);
        checkHistoryButton.setHorizontalTextPosition(0);
        checkHistoryButton.setText("Check");
        mainPanel.add(checkHistoryButton, new GridConstraints(2, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        mainPanel.add(spacer2, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        submissionContent = new JTextArea();
        submissionContent.setBackground(new Color(-11737629));
        Font submissionContentFont = this.$$$getFont$$$("Monaco", Font.PLAIN, 14, submissionContent.getFont());
        if (submissionContentFont != null) submissionContent.setFont(submissionContentFont);
        submissionContent.setForeground(new Color(-15526864));
        submissionContent.setText("");
        mainPanel.add(submissionContent, new GridConstraints(1, 3, 3, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(225, 17), null, 0, false));
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
        return submissionPanel;
    }

    public static class TimestampRenderer extends DefaultTableCellRenderer {
        private final SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd HH:mm:ss");

        @Override
        protected void setValue(Object value) {
            if (value instanceof Date) {
                value = sdf.format(value); // Format the timestamp as desired
            }
            super.setValue(value);
        }
    }
}
