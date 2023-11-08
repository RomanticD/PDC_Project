package util;

import constants.ExportConstants;
import domain.Course;
import domain.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class MethodUtil {
    /**
     * Check the strength of a password based on various criteria.
     * @param password The password string to be evaluated.
     * @return An integer representing the password strength: 1 (weak), 2 (moderate), 3 (strong).
     */
    public static int checkPasswordStrength(String password) {
        int score = 0;
        if (password.length() >= 8) {
            score++;
        }
        boolean hasUppercase = !password.equals(password.toLowerCase());
        if (hasUppercase) {
            score++;
        }
        boolean hasLowercase = !password.equals(password.toUpperCase());
        if (hasLowercase) {
            score++;
        }
        boolean hasDigit = password.matches(".*\\d.*");
        if (hasDigit) {
            score++;
        }
        boolean hasSpecialChar = !password.matches("[A-Za-z0-9]*");
        if (hasSpecialChar) {
            score++;
        }
        if (score < 3) {
            return 1;
        } else if (score < 5) {
            return 2;
        } else {
            return 3;
        }
    }

    /**
     * Export a list of courses to an Excel file.
     * @param courseList The list of courses to be exported.
     * @param user The user associated with the exported data.
     * @return True if the export is successful, false otherwise.
     */
    public static boolean exportCoursesToExcel(List<Course> courseList, User user) {
        // create sheet
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Course Data");

        // Columns name
        Row headerRow = sheet.createRow(0);
        String[] columns = {"Course ID", "Course Name", "Course Description", "Instructor", "Deadline"};
        for (int i = 0; i < columns.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
        }

        // fill with data
        int rowNum = 1;
        for (Course course : courseList) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(course.getCourseID());
            row.createCell(1).setCellValue(course.getCourseName());
            row.createCell(2).setCellValue(course.getCourseDescription());
            row.createCell(3).setCellValue(course.getInstructor());
            row.createCell(4).setCellValue(course.getDeadLine());
        }

        Font headerFont = workbook.createFont();
        headerFont.setFontHeightInPoints((short) 24);
        headerFont.setBold(true);

        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);

        for (Cell cell : headerRow) {
            cell.setCellStyle(headerCellStyle);
        }

        Font contentFont = workbook.createFont();
        contentFont.setFontHeightInPoints((short) 22);

        CellStyle contentCellStyle = workbook.createCellStyle();
        CreationHelper createHelper = workbook.getCreationHelper();
        contentCellStyle.setDataFormat(
                createHelper.createDataFormat().getFormat("yyyy-MM-dd HH:mm:ss"));
        contentCellStyle.setFont(contentFont);
        contentCellStyle.setAlignment(HorizontalAlignment.LEFT);

        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            for (Cell cell : row) {
                cell.setCellStyle(contentCellStyle);
            }
        }

        for (int i = 0; i < columns.length; i++) {
            sheet.autoSizeColumn(i);
        }

        CellStyle courseIdStyle = workbook.createCellStyle();
        Font courseIdFont = workbook.createFont();
        courseIdFont.setFontHeightInPoints((short) 22);
        courseIdStyle.setFont(courseIdFont);
        courseIdStyle.setAlignment(HorizontalAlignment.LEFT);

        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            Cell cell = row.getCell(0);
            cell.setCellStyle(courseIdStyle);
        }

        sheet.autoSizeColumn(0);

        // save file
        try {
            String filePath = ExportConstants.EXPORT_COURSE_TO_PATH + "/user_" + user.getUserId() + "_ExportedCourses.xlsx";
            File file = new File(filePath);
            file.getParentFile().mkdirs();// if path does not exist, then create one
            FileOutputStream outputStream = new FileOutputStream(file);
            workbook.write(outputStream);
            workbook.close();
            log.info("Excel file exported successfully at: " + filePath);
            return true;
        } catch (IOException e) {
            log.error("Error exporting");
            return false;
        }

    }

    /**
     * Delete the cache of a user, if it exists.
     * @param user The user whose cache is to be deleted.
     * @return True if the cache is successfully deleted, false otherwise.
     */
    public static boolean deleteUserCache(User user) {
        String filePath = ExportConstants.EXPORT_COURSE_TO_PATH + "/user_" + user.getUserId() + "_ExportedCourses.xlsx";
        File file = new File(filePath);

        if (file.exists()) {
            if (file.delete()) {
                return true;
            } else {
                log.error("Failed to delete the file.");
                return false;
            }
        } else {
            log.warn("File does not exist.");
            return false;
        }
    }

    /**
     * Copies a directory from the source path to the target path. This method recursively copies all the files and directories
     * within the source directory to the target directory.
     *
     * @param sourcePath the source directory path to be copied
     * @param targetPath the target directory path where the source directory will be copied
     */
    public static void copyDirectory(String sourcePath, String targetPath) {
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

            FrameUtil.showSuccessDialog("Upload Success!");
        } catch (IOException e) {
            FrameUtil.showErrorDialog("Upload Failed: " + e.getMessage());
            log.error("Upload Failed");
        }
    }

    /**
     * Retrieves the file paths of the uploaded files for the specified user.
     * The file paths are generated based on the user's ID and prefixed with "user_{userId}_uploadedFile_".
     * The method searches for files in the "src/main/resources/UploadedFile/" directory.
     * If the directory does not exist or is not a directory, an empty array is returned.
     * @param user the User object for whom the uploaded file paths are retrieved
     * @return an array of file paths for the uploaded files belonging to the specified user
     */
    public static String[] getUserUploadedFilePath(User user) {
        log.info("getting user uploaded file path....");
        String fileLocatedAt = "src/main/resources/UploadedFile/";
        String fileNamePrefix = "user_" + user.getUserId() + "_" + "uploadedFile_";

        File directory = new File(fileLocatedAt);
        if (!directory.exists() || !directory.isDirectory()) {
            return new String[0];
        }

        List<String> filePaths = new ArrayList<>();
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.getName().startsWith(fileNamePrefix)) {
                    filePaths.add(file.getAbsolutePath());
                }
            }
        }
        return filePaths.toArray(new String[0]);
    }
}
