package gui;
import lombok.extern.slf4j.Slf4j;
import util.FrameUtil;
import util.MethodUtil;

import javax.swing.*;
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
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

        chooseSourceButton.addActionListener(e -> {
            int returnValue = fileChooser.showOpenDialog(UploadGUI.this);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                if (selectedFile.isDirectory()) {
                    sourceField.setText(selectedFile.getPath());
                } else {
                    try {
                        sourceField.setText(selectedFile.getCanonicalPath());
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
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
                    FrameUtil.showErrorDialog("File size exceeds the limit (100MB). Please choose a smaller file.");
                } else {
                    Path target = Paths.get(targetPath);
                    String fileName = sourceFile.getFileName().toString();
                    target = target.resolve(fileName);
                    if (Files.isDirectory(sourceFile)) {
                        MethodUtil.copyDirectory(sourcePath, target.toString());
                    } else {
                        try {
                            if (!Files.exists(target)) {
                                Files.createDirectories(target);
                            }
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                        Files.copy(sourceFile, target, StandardCopyOption.REPLACE_EXISTING);
                    }
                    FrameUtil.showSuccessDialog("Upload Success!");
                }
            } catch (IOException ex) {
                log.error("Upload Failed");
                FrameUtil.showErrorDialog("Upload Failed: " + ex.getMessage());
            }
        });

        add(sourceField);
        add(chooseSourceButton);
        add(targetField);
        add(copyButton);
    }
}

