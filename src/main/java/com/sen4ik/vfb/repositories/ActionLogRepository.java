package com.sen4ik.vfb.repositories;

import com.sen4ik.vfb.entities.ActionLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository("actionLogRepository")
public interface ActionLogRepository extends JpaRepository<ActionLog, Integer> {
    Optional<ActionLog> findById(Integer id);
    Optional<ActionLog> findByUserId(Integer userId);
    Optional<ActionLog> findByVerseId(Integer verseId);
    Optional<ActionLog> findByTo(String to);
    Optional<ActionLog> findByFrom(String from);
}
