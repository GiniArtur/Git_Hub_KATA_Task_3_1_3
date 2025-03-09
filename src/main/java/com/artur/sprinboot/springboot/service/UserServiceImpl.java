package com.artur.sprinboot.springboot.service;

import com.artur.sprinboot.springboot.model.Role;
import com.artur.sprinboot.springboot.model.User;
import com.artur.sprinboot.springboot.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleServiceImpl roleServiceImpl;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, @Lazy PasswordEncoder passwordEncoder, RoleServiceImpl roleServiceImpl) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleServiceImpl = roleServiceImpl;
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getAll() {
        return userRepository.findAll();
    }

    @Override
    @Transactional
    public boolean add(User user) {
        User userFromDB = userRepository.findByEmail(user.getEmail());
        if (userFromDB != null) {
            return false;
        }
        if(user.getRoles() == null || user.getRoles().isEmpty()) {
            throw new IllegalArgumentException("User must have at least one role");

        }
        List<Role> userRoles = user.getRoles().stream()
                .map(role -> roleServiceImpl.findByName(role.getName()).orElseThrow(() ->
                        new RuntimeException("Role not found: " + role.getName())))
                .collect(Collectors.toList());
        user.setRoles(userRoles);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public User readUser(long id) {                                                 //Находим по ID пользователя
        return userRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("User with id = " + id + " not exist"));
    }

    @Override
    @Transactional
    public void delete(long id) {
        User user = userRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Such user not exists"));
        userRepository.delete(user);
    }

    @Override
    @Transactional(readOnly = true)
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    @Transactional
    public boolean updateUser(User user, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return false;
        }
        User userFromDB = null;
        userFromDB = userRepository.findByEmail(user.getEmail());
        if (userFromDB == null) {
            log.debug("No user found for email: " + user.getEmail());
        }
        if (userFromDB != null && (userFromDB.getId() != user.getId())) {
            bindingResult.rejectValue("email", "email.exists", "This email already exists");
            log.error("Email already exists");
        }

        if ((user.getPassword() != null) && (!user.getPassword().isEmpty())) {
            Objects.requireNonNull(userFromDB).setPassword(passwordEncoder.encode(user.getPassword()));
        }
        userFromDB.setRoles(user.getRoles());
        userFromDB.setName(user.getName());
        userFromDB.setAge(user.getAge());
        userRepository.save(userFromDB);
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public User getAuthenticatedUser(String email) {
        return userRepository.findByEmail(email);
    }
}