package com.sen4ik.vfb.services;

import com.sen4ik.vfb.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class AuditorAwareImpl implements AuditorAware<String> {

    protected UserService userService;

    @Autowired
    public void setTicketService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Optional<String> getCurrentAuditor() {
        Object user = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (user instanceof String){
            return Optional.of(userService.findById(1).getId() + ""); // TODO:
        } else {
            return Optional.of(((User) user).getUsername());
        }
    }
}
