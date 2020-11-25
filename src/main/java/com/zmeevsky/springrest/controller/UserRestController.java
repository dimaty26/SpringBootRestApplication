package com.zmeevsky.springrest.controller;

import com.zmeevsky.springrest.entity.User;
import com.zmeevsky.springrest.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/users")
public class UserRestController {

    private final UserService userService;

    @Autowired
    public UserRestController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping()
    public ResponseEntity<User> getUser(Principal principal) {

        String username = principal.getName();

        User user = userService.findByUsername(username);

        return ResponseEntity.ok(userService.getUser(user.getId()));
    }
}
