package com.azevedo.user_service.infrastructure.repository;


import com.azevedo.user_service.infrastructure.entity.Telefone;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TelefoneRepository extends JpaRepository<Telefone, Long> {
}
