package com.hotel.huespedes.repository;

import com.hotel.huespedes.entity.HuespedEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HuespedRepository extends JpaRepository<HuespedEntity, String> {
    Optional<HuespedEntity> findByRut(String rut);
}
