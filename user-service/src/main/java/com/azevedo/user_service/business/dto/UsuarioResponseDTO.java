package com.azevedo.user_service.business.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UsuarioResponseDTO {

    private String nome;
    private String email;
    private List<EnderecoResponseDTO> enderecos;
    private List<TelefoneResponseDTO> telefones;
}
