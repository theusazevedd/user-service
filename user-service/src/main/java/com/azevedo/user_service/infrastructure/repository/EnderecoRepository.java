package com.azevedo.user_service.infrastructure.repository;


import com.azevedo.user_service.infrastructure.entity.Endereco;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnderecoRepository extends JpaRepository<Endereco, Long> {
}
