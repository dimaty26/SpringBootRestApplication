package com.zmeevsky.springmvc.controller;

import com.zmeevsky.springmvc.entity.User;
import com.zmeevsky.springmvc.exception.UserNotFoundException;
import com.zmeevsky.springmvc.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class UserRestController {

    private UserService userService;

    @Autowired
    public UserRestController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public List<User> getUsers() {
        return userService.getUsers();
    }

    @GetMapping("/users/{userId}")
    public User getUser(@PathVariable int userId) {
        User user = userService.getUser(userId);

        if (user == null) {
            throw new UserNotFoundException("User not found with id: " + userId);
        }

        return user;
    }

    @PostMapping("/users")
    public User addUser(@RequestBody User user) {
        userService.saveUser(user);
        return user;
    }

    @PutMapping("/users")
    public User updateUser(@RequestBody User user) {
        userService.updateUser(user);
        return user;
    }

    @DeleteMapping("/users/{userId}")
    public String deleteUser(@PathVariable int userId) {
        User user = userService.getUser(userId);

        if (user == null) {
            throw new UserNotFoundException("User not found with id: " + userId);
        }

        userService.deleteUser(userId);

        return "Deleted user id: " + userId;
    }
}
