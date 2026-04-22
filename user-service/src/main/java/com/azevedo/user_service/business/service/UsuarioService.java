package com.azevedo.user_service.business.service;

import com.azevedo.user_service.business.converter.UsuarioConverter;
import com.azevedo.user_service.business.dto.UsuarioDTO;
import com.azevedo.user_service.infrastructure.entity.Usuario;
import com.azevedo.user_service.infrastructure.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioConverter usuarioConverter;

    public UsuarioDTO salvaUsuario(UsuarioDTO usuarioDTO) {
        Usuario usuario = usuarioConverter.paraUsuario(usuarioDTO);
        usuario = usuarioRepository.save(usuario);
        return usuarioConverter.paraUsuarioDTO(usuario);
    }

}
