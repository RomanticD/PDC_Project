package gui;



import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

import static constants.ExportConstants.UPLOADED_TO_PATH;


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

        chooseSourceButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int returnValue = fileChooser.showOpenDialog(UploadGUI.this);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    if (selectedFile.isDirectory()) {
                        sourceField.setText(selectedFile.getPath()); // 用户选择了目录
                    } else {
                        try {
                            sourceField.setText(selectedFile.getCanonicalPath()); // 用户选择了文件
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
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
                    JOptionPane.showMessageDialog(UploadGUI.this, "File size exceeds the limit (100MB). Please choose a smaller file.", "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    Path target = Paths.get(targetPath);
                    // 检查目标是否是目录，如果是目录，则使用源文件的文件名构建目标路径
                    //if (Files.isDirectory(target)) {
                        String fileName = sourceFile.getFileName().toString();
                        target = target.resolve(fileName);
                    //}

                    if (Files.isDirectory(sourceFile)) {
                        copyDirectory(sourcePath, target.toString());
                    } else {
                        Files.copy(sourceFile, target, StandardCopyOption.REPLACE_EXISTING);
                    }
                    JOptionPane.showMessageDialog(UploadGUI.this, "Upload Success!", "Success", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
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

            JOptionPane.showMessageDialog(this, "Upload Success!", "success", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Upload Failed!" + e.getMessage(), "error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

}

