package test;

import domain.User;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import static util.FrameUtil.handleDownloadAction;
import static util.MethodUtil.getUserUploadedFilePath;

@Slf4j
public class DownloadButtonTest {
    public static void main(String[] args) {
        String[] filePaths = getUserUploadedFilePath(new User());
        for (String path : filePaths) {
            log.info(path);
        }

        JPanel panel = new JPanel();
        JButton downloadButton = new JButton();
        downloadButton.setText("download");
        downloadButton.addActionListener(handleDownloadAction(filePaths));
        panel.add(downloadButton);
        JFrame frame = new JFrame();
        frame.add(panel);
        frame.setTitle("test");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.setSize(500, 370);
    }
}
