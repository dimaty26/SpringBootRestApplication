package com.zmeevsky.springmvc.controller;

import com.zmeevsky.springmvc.dao.RoleDao;
import com.zmeevsky.springmvc.entity.Role;
import com.zmeevsky.springmvc.entity.User;
import com.zmeevsky.springmvc.exception.UserAlreadyExistsException;
import com.zmeevsky.springmvc.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final RoleDao roleDao;

    @Autowired
    public UserController(UserService userService, RoleDao roleDao) {
        this.userService = userService;
        this.roleDao = roleDao;
    }

    @GetMapping("/user")
    public String getUserPage(Principal principal, Model model) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Set<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).collect(Collectors.toSet());

        StringBuilder sb = new StringBuilder();
        for (String role : roles) {
            sb.append(role).append(" ");
        }

        model.addAttribute("username", principal.getName());
        model.addAttribute("roleSet", new String(sb));

        String userName = principal.getName();

        User user = userService.findByUsername(userName);

        model.addAttribute("user", user);

        return "user-page";
    }

    @GetMapping("/admin")
    public String getAdminPage(Principal principal, Model model) {

        List<User> users = userService.getUsers();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Set<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).collect(Collectors.toSet());

        StringBuilder sb = new StringBuilder();
        for (String role : roles) {
            sb.append(role).append(" ");
        }

        String userName = principal.getName();
        User user = userService.findByUsername(userName);

        model.addAttribute("user", user);
        model.addAttribute("userList", users);
        model.addAttribute("username", userName);
        model.addAttribute("roleSet", new String(sb));

        return "admin-page";
    }

    @GetMapping("/findOne")
    @ResponseBody
    public User findOne(Integer userId) {
        return userService.getUser(userId);
    }

    @GetMapping("/show-form-for-add")
    public String showFormForAdd(Model model) {

        model.addAttribute("user", new User());
        model.addAttribute("roles", roleDao.getAll());

        return "user-form";
    }

    @PostMapping("/saveUser")
    public String saveUser(@RequestParam("firstName") String firstName,
                           @RequestParam("lastName") String lastName,
                           @RequestParam("email") String email,
                           @RequestParam("password") String password,
                           @RequestParam("roles") String[] roleIds,
                           Model model) {

        Set<Role> roleSet = new HashSet<>();
        for (String roleId : roleIds) {
            if (!"".equals(roleId)) {
                roleSet.add(roleDao.getOne(Integer.parseInt(roleId)));
            }
        }

        try {
            userService.saveUser(new User(firstName, lastName, email, password, roleSet));
        } catch (UserAlreadyExistsException e) {
            model.addAttribute("existedEmail", email);
        }
        return "redirect:/users/admin";
    }

    @GetMapping("/show-form-for-update")
    public String showFormForUpdate(@RequestParam("userId") int id, Model model) {

        model.addAttribute("user", userService.getUser(id));

        return "update-form";
    }

    @PostMapping("/update-user")
    public String updateUser(User user) {

        userService.updateUser(user);

        return "redirect:/users/admin";
    }

    @GetMapping("/delete/{id}")
    public String deleteCustomer(@PathVariable("id") int id) {

        userService.deleteUser(id);

        return "redirect:/users/admin";
    }

    @GetMapping("/search")
    public String searchUsers(@RequestParam("theSearchName") String theSearchName, Model model) {

        List<User> users = userService.searchUsers(theSearchName);

        model.addAttribute("users", users);

        return "users";
    }
}
