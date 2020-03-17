package com.sen4ik.vfb.services.currentUser;

import com.sen4ik.vfb.entities.User;
import com.sen4ik.vfb.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CurrentUserServiceImpl implements CurrentUserService {

    @Qualifier("userRepository")
    @Autowired
    private UserRepository userRepository;

//    @Override
//    public boolean canAccessUser(CurrentUser currentUser, Long userId) {
//        LOGGER.debug("Checking if user={} has access to user={}", currentUser, userId);
//        return currentUser != null
//                && (currentUser.getUser().getRole().toString().equals("ADMIN") || currentUser.getId().equals(userId));
//    }

//    @Override
//    public boolean canAccessAssignments(CurrentUser currentUser, Long accountId) {
//        LOGGER.debug("Checking if user={} has access to company={}", currentUser, accountId);
//        return currentUser != null
//                && (currentUser.getUser().getRole().toString().equals("ADMIN") || currentUser.getUser().getAccount().getId().equals(accountId));
//    }

    @Override
    public User getCurrentUser() {
        return userRepository.findByEmail(Optional.of(((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername()).get());
    }

}
