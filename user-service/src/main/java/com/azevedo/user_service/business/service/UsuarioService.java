package com.azevedo.user_service.business.service;

import com.azevedo.user_service.business.converter.UsuarioConverter;
import com.azevedo.user_service.business.dto.UsuarioDTO;
import com.azevedo.user_service.infrastructure.entity.Usuario;
import com.azevedo.user_service.infrastructure.exceptions.ConflictException;
import com.azevedo.user_service.infrastructure.exceptions.ResourceNotFoundException;
import com.azevedo.user_service.infrastructure.repository.UsuarioRepository;
import com.azevedo.user_service.infrastructure.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioConverter usuarioConverter;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public UsuarioDTO salvaUsuario(UsuarioDTO usuarioDTO) {
        validarEmailDisponivel(usuarioDTO.getEmail());
        usuarioDTO.setSenha(passwordEncoder.encode(usuarioDTO.getSenha()));
        Usuario usuario = usuarioConverter.paraUsuario(usuarioDTO);
        usuario = usuarioRepository.save(usuario);
        return usuarioConverter.paraUsuarioDTO(usuario);
    }

    private void validarEmailDisponivel(String email) {
        if (usuarioRepository.existsByEmail(email)) {
            throw new ConflictException("Email já cadastrado " + email);
        }
    }

    public Usuario buscaUsuarioPorEmail(String email) {
        return usuarioRepository.findByEmail(email).orElseThrow(() ->
                new ResourceNotFoundException("Email não encontrado " + email));
    }

    public void deletaUsuarioPorEmail(String email) {
        usuarioRepository.deleteByEmail(email);
    }

    public UsuarioDTO atualizaDadosUsuario(String token, UsuarioDTO dto) {
        String email = jwtUtil.extractUsername(token.substring(7)); // buscar email do usuario através do token (tirar a obrigatoriedade do email))
        dto.setSenha(dto.getSenha() != null ? passwordEncoder.encode(dto.getSenha()) : null);
        Usuario usuarioEntity = usuarioRepository.findByEmail(email).orElseThrow(() ->
                new ResourceNotFoundException("Email não localizado"));

        Usuario usuario = usuarioConverter.updateUsuario(dto, usuarioEntity); // mesclou dados que recebemos na requisição DTO com os dados do banco.
        return usuarioConverter.paraUsuarioDTO(usuarioRepository.save(usuario));


    }


}
