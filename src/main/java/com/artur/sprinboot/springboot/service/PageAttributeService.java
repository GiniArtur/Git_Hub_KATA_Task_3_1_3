package com.artur.sprinboot.springboot.service;

import org.springframework.ui.Model;

import java.security.Principal;

public interface PageAttributeService {
    void addMainPageAttributes(Principal principal, Model model);

}
