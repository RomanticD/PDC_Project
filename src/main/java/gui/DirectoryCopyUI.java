package gui;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

import static constants.ExportConstants.UPLOADED_TO_PATH;

public class DirectoryCopyUI extends JFrame {
    private JButton chooseSourceButton, copyButton;
    private JTextField sourceField, targetField;
    private JFileChooser fileChooser;


    public DirectoryCopyUI() {
        setTitle("Upload your file");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 180);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(4, 1));

        sourceField = new JTextField();
        sourceField.setBorder(BorderFactory.createTitledBorder("Your file path"));
        sourceField.setEditable(false);

        targetField = new JTextField();
        targetField.setBorder(BorderFactory.createTitledBorder("File will be uploaded to"));
        targetField.setText(UPLOADED_TO_PATH);
        targetField.setEditable(false);

        chooseSourceButton = new JButton("Choose Your File");
        copyButton = new JButton("Upload");

        fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        chooseSourceButton.addActionListener(e -> {
            int returnValue = fileChooser.showOpenDialog(DirectoryCopyUI.this);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                sourceField.setText(fileChooser.getSelectedFile().getPath());
            }
        });

        copyButton.addActionListener(e -> {
            String sourcePath = sourceField.getText();
            String targetPath = targetField.getText();
            copyDirectory(sourcePath, targetPath);
        });

        add(sourceField);
        add(chooseSourceButton);
        add(targetField);
        add(copyButton);
    }

    public void copyDirectory(String sourcePath, String targetPath) {
        Path sourceDirectory = Paths.get(sourcePath);
        Path targetDirectory = Paths.get(targetPath);

        try {
            if (!Files.exists(targetDirectory)) {
                Files.createDirectories(targetDirectory);
            }

            Files.walkFileTree(sourceDirectory, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    Path targetDir = targetDirectory.resolve(sourceDirectory.relativize(dir));
                    Files.createDirectories(targetDir);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Path targetFile = targetDirectory.resolve(sourceDirectory.relativize(file));
                    Files.copy(file, targetFile, StandardCopyOption.REPLACE_EXISTING);
                    return FileVisitResult.CONTINUE;
                }
            });

            JOptionPane.showMessageDialog(this, "Upload Success!", "success", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Upload Failed!" + e.getMessage(), "error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            DirectoryCopyUI ui = new DirectoryCopyUI();
            ui.setVisible(true);
        });
    }
}

