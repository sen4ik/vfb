package com.sen4ik.vfb.repositories;

import com.sen4ik.vfb.entities.SentMessagesLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository("sentMessagesLogRepository")
public interface SentMessagesLogRepository extends JpaRepository<SentMessagesLogEntity, Integer> {
    Optional<SentMessagesLogEntity> findById(Long id);
}
