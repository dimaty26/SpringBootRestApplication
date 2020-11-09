package com.zmeevsky.springrest.controller;

import com.zmeevsky.springrest.entity.User;
import com.zmeevsky.springrest.exception.ExceptionUtils;
import com.zmeevsky.springrest.exception.ValidationException;
import com.zmeevsky.springrest.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserRestController {

    private final UserService userService;
    private final MessageSource messageSource;

    @Autowired
    public UserRestController(UserService userService, MessageSource messageSource) {
        this.userService = userService;
        this.messageSource = messageSource;
    }

    @GetMapping()
    public List<User> getUsers() {
        return userService.getUsers();
    }

    @GetMapping("/{userId}")
    public User getUser(@PathVariable int userId) {
        return userService.getUser(userId);
    }

    @PostMapping()
    public ResponseEntity<User> addUser(@Valid @RequestBody User user,
                                        BindingResult bindingResult) throws ValidationException {

        if (bindingResult.hasErrors()) {
            ExceptionUtils.invalid(bindingResult, messageSource, ExceptionUtils.path());
        }

        userService.saveUser(user);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping()
    public ResponseEntity<User> updateUser(@RequestBody User user,
                                           BindingResult bindingResult) throws ValidationException {

        if (bindingResult.hasErrors()) {
            ExceptionUtils.invalid(bindingResult, messageSource, ExceptionUtils.path());
        }

        userService.saveUser(user);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<User> deleteUser(@PathVariable int userId) {

        userService.deleteUser(userId);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
