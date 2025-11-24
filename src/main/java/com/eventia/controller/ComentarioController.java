package com.eventia.controller;

import com.eventia.dto.ComentarioRequest;
import com.eventia.model.Comentario;
import com.eventia.service.ComentarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comentarios")
@RequiredArgsConstructor
public class ComentarioController {

    private final ComentarioService comentarioService;

    @GetMapping
    public List<Comentario> getAllComentarios() {
        return comentarioService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Comentario> getComentarioById(@PathVariable Long id) {
        return comentarioService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

 //  comentarios de una propiedad
    @GetMapping("/propiedad/{propiedadId}")
    public List<Comentario> getComentariosByPropiedad(@PathVariable Long propiedadId) {
        return comentarioService.findByPropiedad(propiedadId);
    }

    @PostMapping
    public ResponseEntity<Comentario> createComentario(@RequestBody ComentarioRequest request) {
        Comentario comentario = comentarioService.crearComentario(request);
        return ResponseEntity.ok(comentario);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComentario(@PathVariable Long id) {
        comentarioService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}