import domain.Course;
import domain.User;
import org.apache.poi.ss.usermodel.*;
import org.junit.Test;
import org.mockito.Mockito;
import util.InstanceUtil;
import util.MethodUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static util.MethodUtil.deleteUserCache;

public class ExportTest {
    @Test
    public void testThatExportCoursesToExcelMethodCanOutputFile() throws Exception {
        User user = InstanceUtil.getTestUserInstance();
        Course course = InstanceUtil.getTestCourseInstance();

        // Create a mock Workbook, Sheet, and Row
        Workbook workbook = Mockito.mock(Workbook.class);
        Sheet sheet = Mockito.mock(Sheet.class);
        Row row = Mockito.mock(Row.class);

        // Mock the necessary method calls
        Mockito.when(workbook.createSheet(Mockito.anyString())).thenReturn(sheet);
        Mockito.when(sheet.createRow(Mockito.anyInt())).thenReturn(row);

        // Create sample data
        List<Course> courseList = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = dateFormat.parse("2023-10-20 18:00:00");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        courseList.add(course);

        // Test the method
        boolean result = MethodUtil.exportCoursesToExcel(courseList, user);

        // Verify the result
        assertTrue(result);
        assertTrue(deleteUserCache(user));
    }
}
