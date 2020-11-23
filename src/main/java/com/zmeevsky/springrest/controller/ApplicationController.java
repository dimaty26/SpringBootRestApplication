package com.zmeevsky.springrest.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/users")
public class ApplicationController {

    @GetMapping("/user")
    public String getUserPage() {
        return "user-page";
    }

    @GetMapping("/admin")
    public String getAdminPage() {
        return "admin-page";
    }
}
