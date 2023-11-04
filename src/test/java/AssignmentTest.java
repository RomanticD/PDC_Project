import service.AssignmentService;
import service.dao.AssignmentDao;
import domain.Assignment;
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
    private AssignmentService assignmentService;

    @Before
    public void setUp(){
        assignmentService = new AssignmentDao();
    }

    @After
    public void tearDown() {
        assignmentService = null;
    }

    @Test
    public void testThatInsertAssignmentCanInsertRecordToAssignmentsTable() throws ParseException {
        Assignment assignment = InstanceUtil.getTestAssignmentInstance();
        assertTrue(assignmentService.insertAssignment(assignment));
        assertTrue(assignmentService.deleteAssignment(assignment));
    }
}
