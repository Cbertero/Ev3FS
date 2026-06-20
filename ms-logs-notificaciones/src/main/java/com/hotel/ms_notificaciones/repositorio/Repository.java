package com.hotel.ms_notificaciones.repositorio;


import com.hotel.ms_notificaciones.entity.NotificacionEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface Repository extends JpaRepository<NotificacionEntity, Long> {

    List<NotificacionEntity> findByEstado(String estado);
}