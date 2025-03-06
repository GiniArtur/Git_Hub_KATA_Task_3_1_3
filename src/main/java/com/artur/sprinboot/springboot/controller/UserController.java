package com.artur.sprinboot.springboot.controller;

import com.artur.sprinboot.springboot.model.Role;
import com.artur.sprinboot.springboot.model.User;
import com.artur.sprinboot.springboot.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/user")
    public String showUser(@RequestParam(name = "id", required = false) Long id, Model model, Principal principal) {
        User user;
        if (id != null) {
            user = userService.readUser(id);
        } else {
            String email = principal.getName();
            user = userService.findByEmail(email);
        }
        List<String> roles = user.getRoles().stream()
                .map(Role::getName)
                .map(role -> role.split("_")[1])
                .toList();
        model.addAttribute("authUser", user);
        model.addAttribute("userRoles", roles);
        return "/user/user";
    }

    @GetMapping("/login")
    public String welcomeToPage() {
        return "login";
    }
}


