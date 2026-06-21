package com.hotel.ms_notificaciones.repositorio;


import com.hotel.ms_notificaciones.entity.NotificacionEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NotificacionRepository extends JpaRepository<NotificacionEntity, Long> {

    List<NotificacionEntity> findByEstado(String estado);
}