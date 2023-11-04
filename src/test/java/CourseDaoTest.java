import service.CourseService;
import service.dao.CourseDao;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import util.InstanceUtil;

import java.text.ParseException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class CourseDaoTest {
    private CourseService courseService;
    @Before
    public void setUp() {
        courseService = new CourseDao();
    }

    @After
    public void tearDown() {
        courseService = null;
    }

    @Test
    public void testThatCourseCanBeAdded() throws ParseException{
        domain.Course course = InstanceUtil.getTestCourseInstance();
        assertTrue(courseService.newCourse(course));
        assertTrue(courseService.deleteCourse(course));
    }

    @Test
    public void testInsertCourse() {
        // 编写测试用例来测试插入课程的功能
        // 可以模拟输入参数，调用相应的方法，然后使用断言来检查结果
        // 例如：
        // yourInstance.insertCourse("CourseName", "Description", "Instructor", "2023-12-31");

        // 使用断言来验证期望的结果
        // assertTrue(yourInstance.doesCourseExist("CourseName"));
    }

    @Test
    public void testModifyCourseName() {
        // 编写测试用例来测试修改课程名称的功能
        // 可以模拟输入参数，调用相应的方法，然后使用断言来检查结果
        // 例如：
        // yourInstance.modifyCourseName("CourseName", "NewCourseName");

        // 使用断言来验证期望的结果
        // assertEquals("NewCourseName", yourInstance.getCourseName("CourseName"));
    }

    @Test
    public void testDeleteCourse() {
        // 编写测试用例来测试删除课程的功能
        // 可以模拟输入参数，调用相应的方法，然后使用断言来检查结果
        // 例如：
        // yourInstance.deleteCourse("CourseName");

        // 使用断言来验证期望的结果
        // assertFalse(yourInstance.doesCourseExist("CourseName"));
    }
}
