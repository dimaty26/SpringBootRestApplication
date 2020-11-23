package com.zmeevsky.springrest.controller;

import com.zmeevsky.springrest.dto.SaveUserRequest;
import com.zmeevsky.springrest.dto.SaveUserResponse;
import com.zmeevsky.springrest.dto.UpdateUserRequest;
import com.zmeevsky.springrest.dto.UpdateUserResponse;
import com.zmeevsky.springrest.entity.Role;
import com.zmeevsky.springrest.entity.User;
import com.zmeevsky.springrest.service.RoleService;
import com.zmeevsky.springrest.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/admin")
public class AdminRestController {

    private final UserService userService;
    private final RoleService roleService;

    @Autowired
    public AdminRestController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping()
    public ResponseEntity<List<User>> getUsers() {
        return ResponseEntity.ok(userService.getUsers());
    }

    @GetMapping("/{userId}")
    public ResponseEntity<User> getUser(@PathVariable int userId) {
        return ResponseEntity.ok(userService.getUser(userId));
    }

    @PostMapping()
    public ResponseEntity<SaveUserResponse> addUser(@Valid @RequestBody SaveUserRequest user) {

        userService.saveUser(
                new User(user.getFirstName(), user.getLastName(), user.getEmail(), user.getPassword(), user.getRoles()));

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping("/update")
    public ResponseEntity<UpdateUserResponse> updateUser(@RequestBody UpdateUserRequest user) {

        Set<Role> userRoles = new HashSet<>();

        // не делают запрос в цикле
        user.getRoles().forEach(w -> userRoles.add(roleService.findById(w.getId())));

        userService.updateUser(
                new User(user.getId(),
                        user.getFirstName(),
                        user.getLastName(),
                        user.getEmail(),
                        user.getPassword(),
                        userRoles));

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<User> deleteUser(@PathVariable int userId) {

        userService.deleteUser(userId);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
