package com.hotel.habitaciones.repository;

import com.hotel.habitaciones.entity.HabitacionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HabitacionRepository extends JpaRepository<HabitacionEntity, Long> {
    List<HabitacionEntity> findByEstado(String estado);
}
