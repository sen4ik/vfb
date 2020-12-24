package com.sen4ik.vfb.repositories;

import com.sen4ik.vfb.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findById(Integer id);
    User findByEmail(String email);
    User findByConfirmationToken(String confirmationToken);
}
