package ch.uzh.ifi.seal.soprafs19.service;

import ch.uzh.ifi.seal.soprafs19.Application;
import ch.uzh.ifi.seal.soprafs19.constant.UserStatus;
import ch.uzh.ifi.seal.soprafs19.controller.UserController;
import ch.uzh.ifi.seal.soprafs19.entity.User;
import ch.uzh.ifi.seal.soprafs19.exceptions.AuthenticationException;
import ch.uzh.ifi.seal.soprafs19.exceptions.NotFoundException;
import ch.uzh.ifi.seal.soprafs19.repository.UserRepository;
import ch.uzh.ifi.seal.soprafs19.service.UserService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.Date;
import java.util.Iterator;

/**
 * Test class for the UserResource REST resource.
 *
 * @see UserService
 */
@WebAppConfiguration
@RunWith(SpringRunner.class)
@SpringBootTest(classes= Application.class)
public class UserServiceTest {


    @Qualifier("userRepository")
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserController userController;

    @Autowired
    private UserService userService;

    @Test
    public void createUser() {
        userRepository.deleteAll();
        Assert.assertNull(userRepository.findByUsername("testUsername"));

        User testUser = new User();
        testUser.setName("testName");
        testUser.setUsername("testUsername");
        testUser.setPassword("1234");
        testUser.setBirthday(new Date());

        User createdUser = userService.createUser(testUser);

        Assert.assertNotNull(createdUser.getToken());
        Assert.assertEquals(createdUser.getStatus(),UserStatus.OFFLINE);
        Assert.assertEquals(createdUser, userRepository.findByToken(createdUser.getToken()));
    }

    @Test
    public void getUsers() {
        userRepository.deleteAll();
        Assert.assertNull(userRepository.findByUsername("testUsername"));

        User testUser = new User();
        testUser.setName("testName");
        testUser.setUsername("testUsername");
        testUser.setPassword("1234");
        testUser.setBirthday(new Date());
        userService.createUser(testUser);


        Iterable<User> users = userService.getUsers();
        Iterator<User> iter = users.iterator();
        Assert.assertEquals(iter.next(), testUser);
    }

    @Test
    public void getUser() {
        userRepository.deleteAll();
        Assert.assertNull(userRepository.findByUsername("testUsername"));

        User testUser = new User();
        testUser.setName("testName");
        testUser.setUsername("testUsername");
        testUser.setPassword("1234");
        testUser.setBirthday(new Date());
        userService.createUser(testUser);
        User createdUser = userService.getUser("testUsername");

        Assert.assertNotNull(createdUser.getToken());
        Assert.assertEquals(createdUser, userRepository.findByToken(createdUser.getToken()));

    }

    @Test
    public void loginValidUser() {
        userRepository.deleteAll();
        Assert.assertNull(userRepository.findByUsername("testUsername"));

        User testUser = new User();
        testUser.setName("testName");
        testUser.setUsername("testUsername");
        testUser.setBirthday(new Date());
        testUser.setPassword("password1234");
        User local = userService.createUser(testUser);

        userService.loginUser(testUser);
        Assert.assertEquals(local, testUser);
        Assert.assertEquals(local.getStatus(),UserStatus.ONLINE);
        Assert.assertNotNull(local.getToken());
    }

    @Test(expected = AuthenticationException.class)
    public void loginInvalidUsername() {
        userRepository.deleteAll();

        User testUser = new User();
        testUser.setName("testName");
        testUser.setUsername("testUsername");
        testUser.setBirthday(new Date());
        testUser.setPassword("password1234");
        userService.createUser(testUser);
        userController.login("otherName", "password1234");
    }

    @Test(expected = AuthenticationException.class)
    public void loginInvalidPassword() {
        userRepository.deleteAll();
        Assert.assertNull(userRepository.findByUsername("testUsername"));

        User testUser = new User();
        testUser.setName("testName");
        testUser.setUsername("testUsername");
        testUser.setBirthday(new Date());
        testUser.setPassword("password1234");
        userService.createUser(testUser);


        userController.login("testUsername", "otherPassword");
    }

    @Test
    public void logoutValidToken() {
        userRepository.deleteAll();
        Assert.assertNull(userRepository.findByUsername("testUsername"));

        User testUser = new User();
        testUser.setName("testName");
        testUser.setUsername("testUsername");
        testUser.setBirthday(new Date());
        testUser.setPassword("password1234");
        User local = userService.createUser(testUser);

        userService.loginUser(local);
        userService.logoutUser(local);
        Assert.assertEquals(userRepository.findByUsername("testUsername").getStatus(), UserStatus.OFFLINE);
    }

    @Test(expected = AuthenticationException.class)
    public void logoutInvalidToken() {
        userRepository.deleteAll();
        Assert.assertNull(userRepository.findByUsername("testUsername"));

        User testUser = new User();
        testUser.setName("testName");
        testUser.setUsername("testUsername");
        testUser.setBirthday(new Date());
        testUser.setPassword("password1234");
        User local = userService.createUser(testUser);
        userController.logout(local.getId(), "wrongToken");
    }

    @Test
    public void getValidUserId() {
        userRepository.deleteAll();
        Assert.assertNull(userRepository.findByUsername("testUsername"));

        User testUser = new User();
        testUser.setName("testName");
        testUser.setUsername("testUsername");
        testUser.setBirthday(new Date());
        testUser.setPassword("password1234");
        User local = userService.createUser(testUser);

        Assert.assertEquals(userService.getUserById(local.getId()), local);
    }

    @Test(expected = NotFoundException.class)
    public void getInvalidUserId() {
        userRepository.deleteAll();
        Long id = 1L;
        userService.getUserById(id);
    }

    @Test
    public void updateValidUser() {
        userRepository.deleteAll();
        Assert.assertNull(userRepository.findByUsername("testUsername"));

        User testUser = new User();
        testUser.setName("testName");
        testUser.setUsername("testUsername");
        testUser.setBirthday(new Date());
        testUser.setPassword("password1234");
        User local = userService.createUser(testUser);
        userService.loginUser(local);

        User updatedUser = new User();
        updatedUser.setName("testNameUpdate");
        updatedUser.setUsername("testUsernameUpdate");
        updatedUser.setBirthday(new Date());
        updatedUser.setPassword("password1234");


        userController.updateUser(updatedUser, local.getId(), local.getToken());

        Assert.assertEquals(userService.getUserById(local.getId()).getUsername(), updatedUser.getUsername());

    }

    @Test(expected = NotFoundException.class)

    public void updateUserInvalidId() {
        userRepository.deleteAll();
        Assert.assertNull(userRepository.findByUsername("testUsername"));

        User testUser = new User();
        testUser.setName("testName");
        testUser.setUsername("testUsername");
        testUser.setBirthday(new Date());
        testUser.setPassword("password1234");
        User local = userService.createUser(testUser);
        userService.loginUser(local);

        User updatedUser = new User();
        updatedUser.setName("testNameUpdate");
        updatedUser.setUsername("testUsernameUpdate");
        updatedUser.setBirthday(new Date());
        updatedUser.setPassword("password1234");

        Long id = 12L;


        userController.updateUser(updatedUser, id, local.getToken());

        Assert.assertEquals(userService.getUserById(local.getId()).getUsername(), updatedUser.getUsername());

    }



}
