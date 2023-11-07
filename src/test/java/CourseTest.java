import domain.Course;
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
public class CourseTest {
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
    public void testThatCourseNameCanBeModified() throws ParseException {
        Course course = InstanceUtil.getTestCourseInstance();
        assertTrue(courseService.newCourse(course));
        Course updateCourse = courseService.updateInstructor(course,"Test1");
        assertEquals("Test1",updateCourse.getInstructor());
    }
}
