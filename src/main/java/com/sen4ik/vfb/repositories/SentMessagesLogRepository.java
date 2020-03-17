package com.sen4ik.vfb.repositories;

import com.sen4ik.vfb.entities.SentMessagesLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository("sentMessagesLogRepository")
public interface SentMessagesLogRepository extends JpaRepository<SentMessagesLog, Integer> {
    Optional<SentMessagesLog> findById(Integer id);
}
