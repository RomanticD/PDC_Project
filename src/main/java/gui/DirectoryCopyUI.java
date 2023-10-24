package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

import static constants.ExportConstants.UPLOADED_TO_PATH;

public class DirectoryCopyUI extends JFrame {
    private JButton chooseSourceButton, copyButton;
    private JTextField sourceField, targetField;
    private JFileChooser fileChooser;


    public DirectoryCopyUI() {
        setTitle("目录拷贝");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 150);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(4, 1));

        sourceField = new JTextField();
        sourceField.setBorder(BorderFactory.createTitledBorder("源目录"));
        sourceField.setEditable(false);

        targetField = new JTextField();
        targetField.setBorder(BorderFactory.createTitledBorder("目标目录"));
        targetField.setText(UPLOADED_TO_PATH);

        chooseSourceButton = new JButton("选择源目录");
        copyButton = new JButton("开始拷贝");

        fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        chooseSourceButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int returnValue = fileChooser.showOpenDialog(DirectoryCopyUI.this);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    sourceField.setText(fileChooser.getSelectedFile().getPath());
                }
            }
        });

        copyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String sourcePath = sourceField.getText();
                String targetPath = targetField.getText();
                copyDirectory(sourcePath, targetPath);
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
            // 创建目标目录（如果不存在）
            if (!Files.exists(targetDirectory)) {
                Files.createDirectories(targetDirectory);
            }

            // 复制目录及其内容
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

            JOptionPane.showMessageDialog(this, "目录复制完成！", "成功", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "目录复制失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
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

