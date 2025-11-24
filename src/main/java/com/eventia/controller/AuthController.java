package com.eventia.controller;

import com.eventia.dto.*;
import com.eventia.model.Usuario;
import com.eventia.repository.UsuarioRepository;
import com.eventia.service.EmailService;
import com.eventia.service.JwtService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private EmailService emailService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegistroRequest req) {

        Usuario u = new Usuario();
        u.setNombre(req.getNombre());
        u.setApellido(req.getApellido());
        u.setEmail(req.getEmail().trim().toLowerCase());
        u.setPassword(passwordEncoder.encode(req.getPassword()));
        u.setRol(Usuario.Rol.valueOf(req.getRol().toUpperCase()));
        u.setVerificado(false);

        String token = UUID.randomUUID().toString();
        u.setVerificationToken(token);

        usuarioRepository.save(u);

        // Enviar email
        emailService.sendVerificationEmail(u.getEmail(), token);

        return ResponseEntity.ok("REGISTRADO");
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyAccount(@RequestBody Map<String, String> body) {

        String token = body.get("token");

        if (token == null) {
            return ResponseEntity.badRequest().body("INVALIDO");
        }

        Usuario u = usuarioRepository.findByVerificationToken(token).orElse(null);

        if (u == null) {
            return ResponseEntity.badRequest().body("INVALIDO");
        }

        u.setVerificado(true);
        u.setVerificationToken(null);
        usuarioRepository.save(u);

        String jwt = jwtService.generateToken(u.getEmail());

        Map<String, Object> resp = new HashMap<>();
        resp.put("id", u.getId());
        resp.put("token", jwt);
        resp.put("rol", u.getRol().name());
        resp.put("nombre", u.getNombre());

        return ResponseEntity.ok(resp);
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<String> resendVerification(@RequestParam("email") String email) {

        if (email == null || email.isBlank()) {
            return ResponseEntity.badRequest().body("NO_EXISTE");
        }

        String normalizedEmail = email.trim().toLowerCase();

        Usuario u = usuarioRepository.findByEmail(normalizedEmail).orElse(null);

        if (u == null) {
            return ResponseEntity.badRequest().body("NO_EXISTE");
        }

        if (u.isVerificado()) {
            return ResponseEntity.badRequest().body("YA_VERIFICADO");
        }

        String token = UUID.randomUUID().toString();
        u.setVerificationToken(token);
        usuarioRepository.save(u);

        emailService.sendVerificationEmail(u.getEmail(), token);

        return ResponseEntity.ok("REENVIADO");
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest req) {

        // Autenticar email + password
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        req.getEmail(),
                        req.getPassword()));

        Usuario u = usuarioRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!u.isVerificado()) {
            throw new RuntimeException("NO_VERIFICADO");
        }

        String token = jwtService.generateToken(u.getEmail());

        return new AuthResponse(
                u.getId(),
                token,
                u.getRol().name(),
                u.getNombre());
    }
}