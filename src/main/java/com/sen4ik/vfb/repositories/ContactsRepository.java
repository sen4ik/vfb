package com.sen4ik.vfb.repositories;

import com.sen4ik.vfb.entities.ContactsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository("contactsRepository")
public interface ContactsRepository extends JpaRepository<ContactsEntity, Integer> {
    Optional<ContactsEntity> findById(Long id);
}
