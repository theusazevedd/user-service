package com.azevedo.user_service.business.service;

import com.azevedo.user_service.business.converter.UsuarioConverter;
import com.azevedo.user_service.business.dto.EnderecoRequestDTO;
import com.azevedo.user_service.business.dto.EnderecoResponseDTO;
import com.azevedo.user_service.business.dto.TelefoneRequestDTO;
import com.azevedo.user_service.business.dto.TelefoneResponseDTO;
import com.azevedo.user_service.business.dto.UsuarioRequestDTO;
import com.azevedo.user_service.business.dto.UsuarioResponseDTO;
import com.azevedo.user_service.infrastructure.entity.Endereco;
import com.azevedo.user_service.infrastructure.entity.Telefone;
import com.azevedo.user_service.infrastructure.entity.Usuario;
import com.azevedo.user_service.infrastructure.exceptions.ConflictException;
import com.azevedo.user_service.infrastructure.exceptions.ResourceNotFoundException;
import com.azevedo.user_service.infrastructure.repository.EnderecoRepository;
import com.azevedo.user_service.infrastructure.repository.TelefoneRepository;
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
    private final EnderecoRepository enderecoRepository;
    private final TelefoneRepository telefoneRepository;

    public UsuarioResponseDTO salvaUsuario(UsuarioRequestDTO usuarioDTO) {
        validarEmailDisponivel(usuarioDTO.getEmail());
        usuarioDTO.setSenha(passwordEncoder.encode(usuarioDTO.getSenha()));
        Usuario usuario = usuarioConverter.paraUsuario(usuarioDTO);
        usuario = usuarioRepository.save(usuario);
        return usuarioConverter.paraUsuarioResponseDTO(usuario);
    }

    private void validarEmailDisponivel(String email) {
        if (usuarioRepository.existsByEmail(email)) {
            throw new ConflictException("Email já cadastrado " + email);
        }
    }

    public UsuarioResponseDTO buscaUsuarioPorEmail(String email) {
        try {
            return usuarioConverter.paraUsuarioResponseDTO(
                    usuarioRepository.findByEmail(email)
                            .orElseThrow(
                                    () -> new ResourceNotFoundException("Email não encontrado " + email)
                            )
            );
        } catch (ResourceNotFoundException e) {
            throw new ResourceNotFoundException("Email não encontrado " + email);
        }


    }

    public void deletaUsuarioPorEmail(String email) {
        usuarioRepository.deleteByEmail(email);
    }

    public UsuarioResponseDTO atualizaDadosUsuario(String token, UsuarioRequestDTO dto) {
        String email = jwtUtil.extractUsername(token.substring(7)); // buscar email do usuario através do token (tirar a obrigatoriedade do email))
        dto.setSenha(dto.getSenha() != null ? passwordEncoder.encode(dto.getSenha()) : null);
        Usuario usuarioEntity = usuarioRepository.findByEmail(email).orElseThrow(() ->
                new ResourceNotFoundException("Email não localizado"));

        Usuario usuario = usuarioConverter.updateUsuario(dto, usuarioEntity); // mesclou dados que recebemos na requisição DTO com os dados do banco.
        return usuarioConverter.paraUsuarioResponseDTO(usuarioRepository.save(usuario));


    }

    public EnderecoResponseDTO atualizaEndereco(Long idEndereco, EnderecoRequestDTO enderecoDTO) {
        Endereco entity = enderecoRepository.findById(idEndereco).orElseThrow(() ->
                new ResourceNotFoundException("Id não encontrado " + idEndereco));

        Endereco endereco = usuarioConverter.updateEndereco(enderecoDTO, entity);
        return usuarioConverter.paraEnderecoResponseDTO(enderecoRepository.save(endereco));

    }

    public TelefoneResponseDTO atualizaTelefone(Long idTelefone, TelefoneRequestDTO telefoneDTO) {
        Telefone entity = telefoneRepository.findById(idTelefone).orElseThrow(() ->
                new ResourceNotFoundException("Id não encontrado " + idTelefone));

        Telefone telefone = usuarioConverter.updateTelefone(telefoneDTO, entity);
        return usuarioConverter.paraTelefoneResponseDTO(telefoneRepository.save(telefone));


    }

    public EnderecoResponseDTO cadastraEndereco(String token, EnderecoRequestDTO dto) {
        String email = jwtUtil.extractUsername(token.substring(7));
        Usuario usuario = usuarioRepository.findByEmail(email).orElseThrow(() ->
                new ResourceNotFoundException("Email não localizado " + email));

        Endereco endereco = usuarioConverter.paraEnderecoEntity(dto, usuario);
        Endereco enderecoEntity = enderecoRepository.save(endereco);
        return usuarioConverter.paraEnderecoResponseDTO(enderecoEntity);
    }

    public TelefoneResponseDTO cadastraTelefone(String token, TelefoneRequestDTO dto) {
        String email = jwtUtil.extractUsername(token.substring(7));
        Usuario usuario = usuarioRepository.findByEmail(email).orElseThrow(() ->
                new ResourceNotFoundException("Email não localizado " + email));

        Telefone telefone = usuarioConverter.paraTelefoneEntity(dto, usuario);
        Telefone telefoneEntity = telefoneRepository.save(telefone);
        return usuarioConverter.paraTelefoneResponseDTO(telefoneEntity);
    }


}
