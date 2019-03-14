package ch.uzh.ifi.seal.soprafs19.service;

import ch.uzh.ifi.seal.soprafs19.entity.User;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class RestTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String getBaseUrl() {
        return "http://localhost:" + port;
    }

    @Test
    public void testCreateUser() {
        User testUser = new User();
        testUser.setName("testName");
        testUser.setUsername("testUsername");
        testUser.setPassword("1234");
        testUser.setBirthday(new Date());

        ResponseEntity<User> response = restTemplate.postForEntity(getBaseUrl() + "/users", testUser, User.class);
        Assert.assertSame(HttpStatus.CREATED, response.getStatusCode());

        User user = response.getBody();
        Assert.assertNotNull(user);
        Assert.assertNotNull(user.getToken());
        Assert.assertNotNull(user.getCreationDate());
    }

    @Test
    public void testCreateUserAgain() {
        User testUser1 = new User();
        testUser1.setName("testName");
        testUser1.setUsername("testUsername");
        testUser1.setPassword("1234");
        testUser1.setBirthday(new Date());

        restTemplate.postForEntity(getBaseUrl() + "/users", testUser1, User.class);

        User testUser = new User();
        testUser.setName("testName");
        testUser.setUsername("testUsername");
        testUser.setPassword("1234");
        testUser.setBirthday(new Date());

        ResponseEntity<Object> response = restTemplate.postForEntity(getBaseUrl() + "/users", testUser, Object.class);
        Assert.assertSame(HttpStatus.CONFLICT, response.getStatusCode());
    }

    @Test
    public void giveUserProfile() {
        User testUser1 = new User();
        testUser1.setName("testName");
        testUser1.setUsername("testUsername");
        testUser1.setPassword("1234");
        testUser1.setBirthday(new Date());

        testUser1 = restTemplate.postForEntity(getBaseUrl() + "/users", testUser1, User.class).getBody();

        ResponseEntity<User> response = restTemplate.getForEntity(getBaseUrl() + "/users/" + testUser1.getId() + "?token=" + testUser1.getToken(), User.class);
        Assert.assertSame(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void giveUserProfileWithInvalidId() {
        User testUser1 = new User();
        testUser1.setName("testName");
        testUser1.setUsername("testUsername");
        testUser1.setPassword("1234");
        testUser1.setBirthday(new Date());

        testUser1 = restTemplate.postForEntity(getBaseUrl() + "/users", testUser1, User.class).getBody();

        ResponseEntity<Object> response = restTemplate.getForEntity(getBaseUrl() + "/users/" + "4423" + "?token=" + testUser1.getToken(), Object.class);
        Assert.assertSame(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void updateUserProfile() {
        User testUser1 = new User();
        testUser1.setName("testName");
        testUser1.setUsername("testUsername");
        testUser1.setPassword("1234");
        testUser1.setBirthday(new Date());

        testUser1 = restTemplate.postForEntity(getBaseUrl() + "/users", testUser1, User.class).getBody();

        User userUpdate = new User();
        userUpdate.setName("testName");
        userUpdate.setUsername("otherUsername");
        userUpdate.setPassword("1234");
        userUpdate.setBirthday(new Date());

        restTemplate.put(getBaseUrl() + "/users/" + testUser1.getId() + "?token=" + testUser1.getToken(), userUpdate, User.class);

        testUser1 = restTemplate.getForEntity(getBaseUrl() + "/users/" + testUser1.getId() + "?token=" + testUser1.getToken(), User.class).getBody();
        Assert.assertEquals(testUser1.getUsername(), userUpdate.getUsername());
    }
}