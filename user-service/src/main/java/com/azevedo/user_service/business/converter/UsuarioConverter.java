package com.azevedo.user_service.business.converter;

import com.azevedo.user_service.business.dto.EnderecoRequestDTO;
import com.azevedo.user_service.business.dto.EnderecoResponseDTO;
import com.azevedo.user_service.business.dto.TelefoneRequestDTO;
import com.azevedo.user_service.business.dto.TelefoneResponseDTO;
import com.azevedo.user_service.business.dto.UsuarioRequestDTO;
import com.azevedo.user_service.business.dto.UsuarioResponseDTO;
import com.azevedo.user_service.infrastructure.entity.Endereco;
import com.azevedo.user_service.infrastructure.entity.Telefone;
import com.azevedo.user_service.infrastructure.entity.Usuario;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UsuarioConverter {


    // USUARIO -> ENTITY
    public Usuario paraUsuario(UsuarioRequestDTO dto) {

        Usuario usuario = Usuario.builder()
                .nome(dto.getNome())
                .email(dto.getEmail())
                .senha(dto.getSenha())
                .build();


        // Endereços
        if (dto.getEnderecos() != null) {
            List<Endereco> enderecos = dto.getEnderecos().stream()
                    .map(enderecoDTO -> {
                        Endereco endereco = paraEndereco(enderecoDTO);
                        endereco.setUsuario(usuario); // ⭐ vínculo correto
                        return endereco;
                    })
                    .toList();

            usuario.setEnderecos(enderecos);
        }

        // Telefones
        if (dto.getTelefones() != null) {
            List<Telefone> telefones = dto.getTelefones().stream()
                    .map(telefoneDTO -> {
                        Telefone telefone = paraTelefone(telefoneDTO);
                        telefone.setUsuario(usuario); // ⭐ vínculo correto
                        return telefone;
                    })
                    .toList();

            usuario.setTelefones(telefones);
        }

        return usuario;
    }


    // ENTITY -> DTO
    public UsuarioResponseDTO paraUsuarioResponseDTO(Usuario usuario) {
        return UsuarioResponseDTO.builder()
                .nome(usuario.getNome())
                .email(usuario.getEmail())
                .enderecos(paraListaEnderecoResponseDTO(usuario.getEnderecos()))
                .telefones(paraListaTelefoneResponseDTO(usuario.getTelefones()))
                .build();
    }


    // ENDERECO
    public Endereco paraEndereco(EnderecoRequestDTO dto) {
        return Endereco.builder()
                .rua(dto.getRua())
                .numero(dto.getNumero())
                .complemento(dto.getComplemento())
                .cidade(dto.getCidade())
                .estado(dto.getEstado())
                .cep(dto.getCep())
                .build();
    }

    public EnderecoResponseDTO paraEnderecoResponseDTO(Endereco endereco) {
        return EnderecoResponseDTO.builder()
                .id(endereco.getId())
                .rua(endereco.getRua())
                .numero(endereco.getNumero())
                .complemento(endereco.getComplemento())
                .cidade(endereco.getCidade())
                .estado(endereco.getEstado())
                .cep(endereco.getCep())
                .build();
    }


    // TELEFONE
    public Telefone paraTelefone(TelefoneRequestDTO dto) {
        return Telefone.builder()
                .numero(dto.getNumero())
                .ddd(dto.getDdd())
                .build();
    }

    public TelefoneResponseDTO paraTelefoneResponseDTO(Telefone telefone) {
        return TelefoneResponseDTO.builder()
                .id(telefone.getId())
                .numero(telefone.getNumero())
                .ddd(telefone.getDdd())
                .build();
    }


    // LISTAS (NULL SAFE)
    public List<EnderecoResponseDTO> paraListaEnderecoResponseDTO(List<Endereco> lista) {
        if (lista == null) return List.of();
        return lista.stream().map(this::paraEnderecoResponseDTO).toList();
    }

    public List<TelefoneResponseDTO> paraListaTelefoneResponseDTO(List<Telefone> lista) {
        if (lista == null) return List.of();
        return lista.stream().map(this::paraTelefoneResponseDTO).toList();
    }


    // UPDATE
    public Usuario updateUsuario(UsuarioRequestDTO dto, Usuario entity) {

        if (dto.getNome() != null) entity.setNome(dto.getNome());
        if (dto.getEmail() != null) entity.setEmail(dto.getEmail());
        if (dto.getSenha() != null) entity.setSenha(dto.getSenha());

        return entity;
    }

    public Endereco updateEndereco(EnderecoRequestDTO dto, Endereco entity) {

        if (dto.getRua() != null) entity.setRua(dto.getRua());
        if (dto.getCidade() != null) entity.setCidade(dto.getCidade());
        if (dto.getCep() != null) entity.setCep(dto.getCep());
        if (dto.getNumero() != null) entity.setNumero(dto.getNumero());
        if (dto.getEstado() != null) entity.setEstado(dto.getEstado());
        if (dto.getComplemento() != null) entity.setComplemento(dto.getComplemento());

        return entity;
    }

    public Telefone updateTelefone(TelefoneRequestDTO dto, Telefone entity) {

        if (dto.getDdd() != null) entity.setDdd(dto.getDdd());
        if (dto.getNumero() != null) entity.setNumero(dto.getNumero());

        return entity;
    }


    // CADASTRO INDIVIDUAL
    public Endereco paraEnderecoEntity(EnderecoRequestDTO dto, Usuario usuario) {
        Endereco endereco = paraEndereco(dto);
        endereco.setUsuario(usuario);
        return endereco;
    }

    public Telefone paraTelefoneEntity(TelefoneRequestDTO dto, Usuario usuario) {
        Telefone telefone = paraTelefone(dto);
        telefone.setUsuario(usuario);
        return telefone;
    }
}