package com.artur.sprinboot.springboot.controller;

import com.artur.sprinboot.springboot.model.User;
import com.artur.sprinboot.springboot.service.PageAttributeService;
import com.artur.sprinboot.springboot.service.RoleService;
import com.artur.sprinboot.springboot.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.logging.Logger;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final RoleService roleService;
    private final UserService userService;
    private final PageAttributeService pageAttributeService;
    private static final Logger logger = Logger.getLogger(AdminController.class.getName());

    @Autowired
    public AdminController(RoleService roleService, UserService userService, PageAttributeService pageAttributeService) {
        this.roleService = roleService;
        this.userService = userService;
        this.pageAttributeService = pageAttributeService;
    }

    @GetMapping("/users")
    public String getUsers(Principal principal, Model model) {
        pageAttributeService.addMainPageAttributes(principal, model);
        return "admin/user-list";
    }

    @PostMapping("/users")
    public String createUser(@ModelAttribute("newUser") @Valid User newUser, BindingResult bindingResult, Model model) {
        try {
            userService.add(newUser);
            return "redirect:/admin/users";
        } catch (IllegalArgumentException e) {
            bindingResult.rejectValue("roles", "roles.invalid", "Please enter a valid role");
            model.addAttribute("error", "Please enter a valid role");
            return "admin/user-list";
        }
    }

    @GetMapping("/user-update")
    public String showFormForUpdate(@RequestParam("id") long id, Model model) {
        model.addAttribute("user", userService.readUser(id));
        model.addAttribute("listRoles", roleService.findAll());
        return "/admin/edit-user";
    }

    @PatchMapping("/users/edit")
    public String editUser(@ModelAttribute("updatingUser") @Valid User updatingUser, BindingResult bindingResult, Model model, Principal principal) {
        if (updatingUser == null) {
            logger.warning("User is null");
            model.addAttribute("error", "User data is missing");
            return "admin/user-list";
        }
        if (!userService.updateUser(updatingUser, bindingResult)) {
            logger.warning("User update failed : {}");
            pageAttributeService.addMainPageAttributes(principal, model);
            model.addAttribute("hasErrors", true);
            return "admin/user-list";
        }
        return "redirect:/admin/users";
    }

    @DeleteMapping("/users/delete")
    public String deleteUser(@ModelAttribute("deletingUser") User user) {
        if (userService.readUser(user.getId()) != null) {
            userService.delete(user.getId());
        }
        return "redirect:/admin/users";
    }
}