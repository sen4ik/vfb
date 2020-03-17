package com.sen4ik.vfb.services.currentUser;

import com.sen4ik.vfb.entities.User;
import com.sen4ik.vfb.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CurrentUserDetailsServices implements UserDetailsService {

    private final UserService userService;

    @Autowired
    public CurrentUserDetailsServices(UserService userService) {
        this.userService = userService;
    }

    @Override
    public User loadUserByUsername(String email) throws UsernameNotFoundException {
        log.debug("Authenticating user with email={}", email.replaceFirst("@.*", "@***"));
        User user = userService.getUserByEmail(email);
                if(user == null){
                    throw new UsernameNotFoundException(String.format("User with email=%s was not found", email));
                }
        return user;
    }

}
