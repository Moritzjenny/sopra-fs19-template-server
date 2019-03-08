package ch.uzh.ifi.seal.soprafs19.controller;

import ch.uzh.ifi.seal.soprafs19.constant.UserStatus;
import ch.uzh.ifi.seal.soprafs19.entity.User;
import ch.uzh.ifi.seal.soprafs19.exceptions.AuthenticationException;
import ch.uzh.ifi.seal.soprafs19.exceptions.ConflictException;
import ch.uzh.ifi.seal.soprafs19.repository.UserRepository;
import ch.uzh.ifi.seal.soprafs19.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
public class UserController {

    private final UserService service;

    UserController(UserService service) {
        this.service = service;
    }

    @GetMapping("/users")
    Iterable<User> all() {
        return service.getUsers();
    }


    @GetMapping("/users/{username}")
    User login(@PathVariable String username, @RequestParam String pw) {
        User user = service.getUser(username);
        if (user != null && user.getPassword().equals(pw)) {
            return user;
        }
        throw new AuthenticationException("wrong password for user " + username);
    }


    @PostMapping("/users")
    User createUser(@RequestBody User newUser) {
        User user = service.getUser(newUser.getUsername());
        if (user != null){
            throw new ConflictException("add User failed because username already exists");
        }
        return this.service.createUser(newUser);
    }
}
