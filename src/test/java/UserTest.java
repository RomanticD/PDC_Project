import dao.UserService;
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
public class UserTest {
    private UserService userService;

    @Before
    public void setUp() {
        userService = new UserDao();
    }

    @After
    public void tearDown() {
        userService = null;
    }

    @Test
    public void testThatCreateUserCanInsertRecordToUserTable(){
        User user = InstanceUtil.getTestUserInstance();
        assertTrue(userService.createUser(user));
        assertTrue(userService.deleteUserByUsername(user.getUsername()));
    }

    @Test
    public void testThatUserInfoCanBeFetched(){
        User user = InstanceUtil.getTestUserInstance();

        assertTrue(userService.createUser(user));
        assertEquals(true, userService.isUserExists(user.getUsername()));
        assertTrue(userService.deleteUserByUsername(user.getUsername()));
    }

    @Test
    public void testThatUpUserUsernameCanBeUpdated(){
        User user = InstanceUtil.getTestUserInstance();

        assertTrue(userService.createUser(user));
        User updatedUser = userService.updateUserUsername(user, "Test change username");
        assertEquals("Test change username", updatedUser.getUsername());
        assertTrue(userService.deleteUserByUsername(user.getUsername()));
    }
}
