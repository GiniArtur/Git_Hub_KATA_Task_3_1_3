package com.artur.sprinboot.springboot.service;

import com.artur.sprinboot.springboot.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.security.Principal;
import java.util.List;

@Service
public class PageAttributeServiceImpl implements PageAttributeService {
    private final UserServiceImpl userServiceImpl;
    private final RoleServiceImpl roleServiceImpl;

    @Autowired
    public PageAttributeServiceImpl(UserServiceImpl userServiceImpl, RoleServiceImpl roleServiceImpl) {
        this.userServiceImpl = userServiceImpl;
        this.roleServiceImpl = roleServiceImpl;
    }

    @Override
    public void addMainPageAttributes(Principal principal, Model model) {
        User user = userServiceImpl.getAuthenticatedUser(principal.getName());
        List<String> roles = roleServiceImpl.getUserRoles(user);
        model.addAttribute("authUser", user);
        model.addAttribute("userRoles", roles);
        model.addAttribute("listRoles", roleServiceImpl.findAll());
        model.addAttribute("users", userServiceImpl.getAll());
        model.addAttribute("updatingUser", model.getAttribute("updatingUser") != null ? model.getAttribute("updatingUser") : new User());
        model.addAttribute("newUser", model.getAttribute("newUser") != null ? model.getAttribute("newUser") : new User());
        model.addAttribute("deletingUser", model.getAttribute("deletingUser") != null ? model.getAttribute("deletingUser") : new User());
    }
}


