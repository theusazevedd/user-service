package com.azevedo.user_service.controller;

import com.azevedo.user_service.business.dto.EnderecoRequestDTO;
import com.azevedo.user_service.business.dto.EnderecoResponseDTO;
import com.azevedo.user_service.business.dto.TelefoneRequestDTO;
import com.azevedo.user_service.business.dto.TelefoneResponseDTO;
import com.azevedo.user_service.business.dto.UsuarioRequestDTO;
import com.azevedo.user_service.business.dto.UsuarioResponseDTO;
import com.azevedo.user_service.business.service.UsuarioService;
import com.azevedo.user_service.infrastructure.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usuario")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> salvaUsuario(@RequestBody UsuarioRequestDTO usuarioDTO) {
        return ResponseEntity.ok(usuarioService.salvaUsuario(usuarioDTO));
    }


    @PostMapping("/login")
    public String login(@RequestBody UsuarioRequestDTO usuarioDTO) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(usuarioDTO.getEmail(),
                        usuarioDTO.getSenha())
        );
        return "Bearer " + jwtUtil.generateToken(authentication.getName());
    }

    @GetMapping
    public ResponseEntity<UsuarioResponseDTO> buscaUsuarioPorEmail(@RequestParam("email") String email) {
        return ResponseEntity.ok(usuarioService.buscaUsuarioPorEmail(email));

    }

    @DeleteMapping("/{email}")
    public ResponseEntity<Void> deleteUsuarioPorEmail(@PathVariable String email) {
        usuarioService.deletaUsuarioPorEmail(email);
        return ResponseEntity.ok().build();
    }

    @PutMapping
    public ResponseEntity<UsuarioResponseDTO> atualizaDadosUsuario(@RequestBody UsuarioRequestDTO dto,
                                                           @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(usuarioService.atualizaDadosUsuario(token, dto));
    }

    @PutMapping("/endereco")
    public ResponseEntity<EnderecoResponseDTO> atualizaEndereco(@RequestBody EnderecoRequestDTO dto,
                                                        @RequestParam("id") Long id) {

        return ResponseEntity.ok(usuarioService.atualizaEndereco(id, dto));
    }

    @PutMapping("/telefone")
    public ResponseEntity<TelefoneResponseDTO> atualizaTelefone(@RequestBody TelefoneRequestDTO dto,
                                                        @RequestParam("id") Long id) {

        return ResponseEntity.ok(usuarioService.atualizaTelefone(id, dto));
    }

    @PostMapping("/endereco")
    public ResponseEntity<EnderecoResponseDTO> cadastraEndereco(@RequestBody EnderecoRequestDTO dto,
                                                        @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(usuarioService.cadastraEndereco(token, dto));
    }

    @PostMapping("/telefone")
    public ResponseEntity<TelefoneResponseDTO> cadastraTelefone(@RequestBody TelefoneRequestDTO dto,
                                                        @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(usuarioService.cadastraTelefone(token, dto));
    }


}
