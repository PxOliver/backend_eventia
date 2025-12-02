package com.eventia.controller;

import com.eventia.dto.PerfilUpdateRequest;
import com.eventia.model.Usuario;
import com.eventia.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/perfil")
@RequiredArgsConstructor
public class PerfilController {

    private final UsuarioRepository usuarioRepository;

    // Obtener datos del usuario autenticado
    @GetMapping
    public ResponseEntity<?> getMiPerfil() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElse(null);

        if (usuario == null) {
            return ResponseEntity.notFound().build();
        }

        // Puedes devolver el usuario completo o un DTO; aquí lo dejo simple
        Map<String, Object> resp = new HashMap<>();
        resp.put("id", usuario.getId());
        resp.put("nombre", usuario.getNombre());
        resp.put("apellido", usuario.getApellido());
        resp.put("email", usuario.getEmail());
        resp.put("rol", usuario.getRol());
        resp.put("fotoPerfil", usuario.getFotoPerfil());

        return ResponseEntity.ok(resp);
    }

    // Actualizar nombre / apellido / fotoPerfil
    @PutMapping
    public ResponseEntity<?> actualizarPerfil(@RequestBody PerfilUpdateRequest req) {
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElse(null);

        if (usuario == null) {
            return ResponseEntity.notFound().build();
        }

        if (req.getNombre() != null && !req.getNombre().isBlank()) {
            usuario.setNombre(req.getNombre());
        }
        if (req.getApellido() != null && !req.getApellido().isBlank()) {
            usuario.setApellido(req.getApellido());
        }
        if (req.getFotoPerfil() != null && !req.getFotoPerfil().isBlank()) {
            usuario.setFotoPerfil(req.getFotoPerfil());
        }

        usuarioRepository.save(usuario);

        Map<String, Object> resp = new HashMap<>();
        resp.put("id", usuario.getId());
        resp.put("nombre", usuario.getNombre());
        resp.put("apellido", usuario.getApellido());
        resp.put("email", usuario.getEmail());
        resp.put("rol", usuario.getRol());
        resp.put("fotoPerfil", usuario.getFotoPerfil());

        return ResponseEntity.ok(resp);
    }

    // Subir archivo de foto de perfil
    @PostMapping("/avatar")
    public ResponseEntity<Map<String, String>> uploadAvatar(@RequestParam("file") MultipartFile file) {

        Map<String, String> resp = new HashMap<>();

        if (file.isEmpty()) {
            resp.put("error", "Archivo vacío");
            return ResponseEntity.badRequest().body(resp);
        }

        try {
            String uploadDir = "uploads/avatars";

            Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(uploadPath);

            String originalFilename = file.getOriginalFilename();
            String extension = "";

            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }

            String fileName = java.util.UUID.randomUUID() + extension;
            Path filePath = uploadPath.resolve(fileName);

            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .build()
                    .toUriString();

            String fileUrl = baseUrl + "/uploads/avatars/" + fileName;

            resp.put("url", fileUrl);
            return ResponseEntity.ok(resp);

        } catch (IOException e) {
            e.printStackTrace();
            resp.put("error", "Error al guardar archivo");
            return ResponseEntity.internalServerError().body(resp);
        }
    }
}