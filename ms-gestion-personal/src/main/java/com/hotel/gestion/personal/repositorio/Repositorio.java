package com.hotel.gestion.personal.repositorio;

import com.hotel.gestion.personal.entity.PersonalEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface Repositorio extends JpaRepository<PersonalEntity, String> {

    Optional<PersonalEntity> findByRut(String rut);
}