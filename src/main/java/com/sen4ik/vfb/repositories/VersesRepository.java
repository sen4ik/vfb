package com.sen4ik.vfb.repositories;

import com.sen4ik.vfb.entities.Verse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.Optional;

@Repository("versesRepository")
public interface VersesRepository extends JpaRepository<Verse, Integer> {
    Optional<Verse> findById(Integer id);
    Optional<Verse> findByDate(Date date);
}
