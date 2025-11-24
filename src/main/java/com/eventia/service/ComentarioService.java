package com.eventia.service;

import com.eventia.dto.ComentarioRequest;
import com.eventia.model.Comentario;
import com.eventia.model.Propiedad;
import com.eventia.model.Usuario;
import com.eventia.repository.ComentarioRepository;
import com.eventia.repository.PropiedadRepository;
import com.eventia.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ComentarioService {

    private final ComentarioRepository comentarioRepository;
    private final UsuarioRepository usuarioRepository;
    private final PropiedadRepository propiedadRepository;

    public Comentario crearComentario(ComentarioRequest request) {

        Usuario usuario = usuarioRepository.findById(request.getUsuario_id())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Propiedad propiedad = propiedadRepository.findById(request.getPropiedad_id())
                .orElseThrow(() -> new RuntimeException("Propiedad no encontrada"));

        Comentario comentario = new Comentario();
        comentario.setContenido(request.getContenido());
        comentario.setCalificacion(request.getCalificacion());
        comentario.setUsuario(usuario);
        comentario.setPropiedad(propiedad);

        return comentarioRepository.save(comentario);
    }

    public java.util.List<Comentario> findAll() {
        return comentarioRepository.findAll();
    }

    public java.util.Optional<Comentario> findById(Long id) {
        return comentarioRepository.findById(id);
    }

    public void deleteById(Long id) {
        comentarioRepository.deleteById(id);
    }

    public java.util.List<Comentario> findByPropiedad(Long propiedadId) {
        return comentarioRepository.findByPropiedadId(propiedadId);
    }
}