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
public class UsuarioRequestDTO {

    private String nome;
    private String email;
    private String senha;
    private List<EnderecoRequestDTO> enderecos;
    private List<TelefoneRequestDTO> telefones;
}
