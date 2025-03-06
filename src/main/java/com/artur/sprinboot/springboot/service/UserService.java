package com.artur.sprinboot.springboot.service;

import com.artur.sprinboot.springboot.model.User;
import org.springframework.validation.BindingResult;

import java.util.List;

public interface UserService {
    List<User> getAll();

    boolean add(User user);

    User readUser(long id);

    void delete(long id);

    User findByEmail(String email);

    boolean updateUser(User user, BindingResult bindingResult);

    User getAuthenticatedUser(String email);
}