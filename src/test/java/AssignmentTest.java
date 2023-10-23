import dao.AssignmentDaoInterface;
import dao.CourseDaoInterface;
import dao.impl.AssignmentDao;
import dao.impl.CourseDao;
import dao.impl.UserDao;
import domain.Assignment;
import domain.Course;
import domain.User;
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
public class AssignmentTest {
    private AssignmentDaoInterface assignmentDao;

    @Before
    public void setUp(){
        assignmentDao = new AssignmentDao();
    }

    @After
    public void tearDown() {
        assignmentDao = null;
    }

    @Test
    public void testThatInsertAssignmentCanInsertRecordToAssignmentsTable() throws ParseException {
        Assignment assignment = InstanceUtil.getTestAssignmentInstance();
        assertTrue(assignmentDao.insertAssignment(assignment));
        assertTrue(assignmentDao.deleteAssignment(assignment));
    }
}
