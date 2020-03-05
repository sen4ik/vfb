package com.sen4ik.vfb.repositories;

import com.sen4ik.vfb.entities.VersesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.Optional;

@Repository("versesRepository")
public interface VersesRepository extends JpaRepository<VersesEntity, Integer> {
    Optional<VersesEntity> findById(Integer id);
    Optional<VersesEntity> findByDate(Date date);
}
