package gui;



import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

import static constants.ExportConstants.UPLOADED_TO_PATH;

@Slf4j
public class UploadGUI extends JFrame {
    private JButton chooseSourceButton, copyButton;
    private JTextField sourceField, targetField;
    private JFileChooser fileChooser;


    public UploadGUI() {
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
        fileChooser.setFileSelectionMode(JFileChooser.APPROVE_OPTION);
        FileFilter filter = new FileNameExtensionFilter("Text and Word Documents", "txt", "docx", "pdf", "pptx", "xlsx");
        fileChooser.setFileFilter(filter);

        chooseSourceButton.addActionListener(e -> {
            int returnValue = fileChooser.showOpenDialog(UploadGUI.this);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                if (selectedFile.isDirectory()) {
                    sourceField.setText(selectedFile.getPath());
                } else {
                    sourceField.setText(selectedFile.getParent());
                }
            }
        });


        copyButton.addActionListener(e -> {
            String sourcePath = sourceField.getText();
            String targetPath = targetField.getText();

            try {
                Path sourceFile = Paths.get(sourcePath);

                long fileSize = Files.size(sourceFile);
                long maxFileSize = 100 * 1024 * 1024; // 100MB

                if (fileSize > maxFileSize) {
                    JOptionPane.showMessageDialog(UploadGUI.this, "File size exceeds the limit (100MB). Please choose a smaller file.", "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    if (Files.isDirectory(sourceFile)) {
                        copyDirectory(sourcePath, targetPath);
                    } else {
                        Path targetFile = Paths.get(targetPath);
                        Files.copy(sourceFile, targetFile, StandardCopyOption.REPLACE_EXISTING);
                    }
                    JOptionPane.showMessageDialog(UploadGUI.this, "Upload Success!", "Success", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                log.error(ex.getMessage());
                JOptionPane.showMessageDialog(UploadGUI.this, "Upload Failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
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
            log.info("copy success....");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}

