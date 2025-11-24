package com.eventia.controller;

import com.eventia.model.Propiedad;
import com.eventia.model.Usuario;
import com.eventia.repository.UsuarioRepository;
import com.eventia.service.PropiedadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

@RestController
@RequestMapping("/api/propiedades")
@RequiredArgsConstructor
public class PropiedadController {

    private final PropiedadService propiedadService;
    private final UsuarioRepository usuarioRepository;

    @GetMapping
    public List<Propiedad> listarTodas() {
        return propiedadService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Propiedad> obtenerPorId(@PathVariable Long id) {
        return propiedadService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/mis-propiedades")
    @PreAuthorize("hasRole('PROPIETARIO')")
    public ResponseEntity<List<Propiedad>> listarMisPropiedades() {

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        List<Propiedad> lista = propiedadService.findByPropietarioEmail(email);
        return ResponseEntity.ok(lista);
    }

    @PostMapping
    @PreAuthorize("hasRole('PROPIETARIO')")
    public ResponseEntity<Propiedad> crearPropiedad(@RequestBody Propiedad propiedad) {

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        Usuario propietario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Propietario no encontrado con email: " + email));

        propiedad.setPropietario(propietario);

        Propiedad guardada = propiedadService.savePropiedad(propiedad);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardada);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('PROPIETARIO')")
    public ResponseEntity<Propiedad> actualizarPropiedad(
            @PathVariable Long id,
            @RequestBody Propiedad cambios) {

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        Usuario propietario = usuarioRepository.findByEmail(email)
                .orElse(null);

        if (propietario == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Optional<Propiedad> opt = propiedadService.findById(id);
        if (opt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Propiedad existente = opt.get();

        // Sólo el dueño puede editar
        if (existente.getPropietario() != null &&
                !existente.getPropietario().getId().equals(propietario.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // Si la propiedad no tenía propietario (datos antiguos), se lo asignamos ahora
        if (existente.getPropietario() == null) {
            existente.setPropietario(propietario);
        }

        existente.setNombre(cambios.getNombre());
        existente.setTipo(cambios.getTipo());
        existente.setCapacidad(cambios.getCapacidad());
        existente.setPrecio(cambios.getPrecio());
        existente.setImagen(cambios.getImagen());
        existente.setDescripcion(cambios.getDescripcion());

        Propiedad guardada = propiedadService.savePropiedad(existente);
        return ResponseEntity.ok(guardada);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('PROPIETARIO') or hasRole('ADMIN')")
    public ResponseEntity<Void> eliminarPropiedad(@PathVariable Long id) {

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        Optional<Propiedad> opt = propiedadService.findById(id);
        if (opt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Propiedad prop = opt.get();

        if (prop.getPropietario() != null &&
                !prop.getPropietario().getEmail().equalsIgnoreCase(email)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        propiedadService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/imagen")
    @PreAuthorize("hasRole('PROPIETARIO')")
    public ResponseEntity<Map<String, String>> uploadImagen(@RequestParam("file") MultipartFile file) {

        Map<String, String> resp = new HashMap<>();

        if (file.isEmpty()) {
            resp.put("error", "Archivo vacío");
            return ResponseEntity.badRequest().body(resp);
        }

        try {
            String uploadDir = "uploads";

            Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(uploadPath);

            String originalFilename = file.getOriginalFilename();
            String extension = "";

            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }

            String fileName = UUID.randomUUID() + extension;
            Path filePath = uploadPath.resolve(fileName);

            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // 👉 construir URL base según el contexto (Render, localhost, etc)
            String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .build()
                    .toUriString();

            String fileUrl = baseUrl + "/uploads/" + fileName;

            resp.put("url", fileUrl);
            return ResponseEntity.ok(resp);

        } catch (IOException e) {
            e.printStackTrace();
            resp.put("error", "Error al guardar archivo");
            return ResponseEntity.internalServerError().body(resp);
        }
    }
}