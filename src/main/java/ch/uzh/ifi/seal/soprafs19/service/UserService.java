package ch.uzh.ifi.seal.soprafs19.service;

import ch.uzh.ifi.seal.soprafs19.constant.UserStatus;
import ch.uzh.ifi.seal.soprafs19.entity.User;
import ch.uzh.ifi.seal.soprafs19.entity.UserUpdate;
import ch.uzh.ifi.seal.soprafs19.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class UserService {

    private final Logger log = LoggerFactory.getLogger(UserService.class);


    private final UserRepository userRepository;


    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Iterable<User> getUsers() {
        return this.userRepository.findAll();
    }

    public User getUser(String username){
        return this.userRepository.findByUsername(username);
    }

    public User getUserByToken(String token){
        return this.userRepository.findByToken(token);
    }

    public User getUserById(Long id){
        Optional<User> user = this.userRepository.findById(id);
        if (user.isPresent()) {
            return user.get();
        }
        return null;
    }

    public void loginUser(User user) {
        user.setStatus(UserStatus.ONLINE);
        userRepository.save(user);
    }

    public void logoutUser(User user) {
        user.setStatus(UserStatus.OFFLINE);
        userRepository.save(user);
    }

    public User createUser(User newUser) {
        newUser.setToken(UUID.randomUUID().toString());
        newUser.setCreationDate(new Date());
        newUser.setStatus(UserStatus.OFFLINE);
        userRepository.save(newUser);
        System.out.println("Created Information for User: " + newUser);
        return newUser;
    }

    public void updateUser(User user, User newUser) {
        user.setUsername(newUser.getUsername());
        user.setBirthday(newUser.getBirthday());
        userRepository.save(user);
    }

}
