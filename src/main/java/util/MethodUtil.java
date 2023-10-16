package util;

import constants.ExportConstants;
import domain.Course;
import domain.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

@Slf4j
public class MethodUtil {
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
            FileOutputStream outputStream = new FileOutputStream(filePath);
            workbook.write(outputStream);
            workbook.close();
            log.info("Excel file exported successfully at: " + filePath);
            return true;
        } catch (IOException e) {
            log.error("Error exporting");
            return false;
        }
    }
}
