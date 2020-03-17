package com.sen4ik.vfb.services.currentUser;

import com.sen4ik.vfb.entities.CurrentUser;
import com.sen4ik.vfb.entities.User;

public interface CurrentUserService {

//    boolean canAccessUser(CurrentUser currentUser, Long userId);

//    boolean canAccessAssignments(CurrentUser currentUser, Long accountId);

    User getCurrentUser();
}
