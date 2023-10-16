import dao.impl.UserDao;
import domain.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import util.InstanceUtil;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class DBTest {
    private UserDao userDao;

    @Before
    public void setUp() {
        userDao = new UserDao();
    }

    @After
    public void tearDown() {
        userDao = null;
    }

    @Test
    public void testThatCreateUserCanInsertRecordToUserTable(){
        User user = InstanceUtil.getTestUserInstance();
        assertTrue(userDao.createUser(user));
        assertTrue(userDao.deleteUserByUsername(user.getUsername()));
    }

    @Test
    public void testThatUserInfoCanBeFetched(){
        User user = InstanceUtil.getTestUserInstance();

        assertTrue(userDao.createUser(user));
        assertEquals(true, userDao.isUserExists(user.getUsername()));
        assertTrue(userDao.deleteUserByUsername(user.getUsername()));
    }

    @Test
    public void testThatUpUserUsernameCanBeUpdated(){
        User user = InstanceUtil.getTestUserInstance();

        assertTrue(userDao.createUser(user));
        User updatedUser = userDao.updateUserUsername(user, "Test change username");
        assertEquals("Test change username", updatedUser.getUsername());
        assertTrue(userDao.deleteUserByUsername(user.getUsername()));
    }
}
