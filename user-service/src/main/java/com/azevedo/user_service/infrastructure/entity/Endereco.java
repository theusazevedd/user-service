package com.azevedo.user_service.infrastructure.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "endereco")
@Builder
public class Endereco {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String rua;
    private Long numero;
    private String complemento;
    private String cidade;

    @Column(length = 2)
    private String estado;

    @Column(length = 9)
    private String cep;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

}
