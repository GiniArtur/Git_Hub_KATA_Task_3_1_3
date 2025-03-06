package com.artur.sprinboot.springboot.init;

import com.artur.sprinboot.springboot.model.Role;
import com.artur.sprinboot.springboot.model.User;
import com.artur.sprinboot.springboot.repository.RoleRepository;
import com.artur.sprinboot.springboot.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UsersInitializer implements CommandLineRunner {
    private final RoleRepository roleRepository;
    private final UserService userService;

    @Autowired
    public UsersInitializer(RoleRepository roleRepository, UserService userService) {
        this.roleRepository = roleRepository;
        this.userService = userService;
    }

    @Override
    public void run(String... args) throws Exception {
        Role roleUser = roleRepository.findByName("ROLE_USER")
                .orElseGet(() -> roleRepository.save(new Role("ROLE_USER")));
        Role roleAdmin = roleRepository.findByName("ROLE_ADMIN")
                .orElseGet(() -> roleRepository.save(new Role("ROLE_ADMIN")));
        userService.add(new User("artur@artur.ru", "artur", 25, "artur", List.of(roleUser)));
        userService.add(new User("marat@marat.ru", "marat", 22, "marat", List.of(roleAdmin)));
    }
}