package com.eventia.service;

import com.eventia.model.Propiedad;
import com.eventia.model.Usuario;              // <<< NUEVO
import com.eventia.repository.PropiedadRepository;
import com.eventia.repository.UsuarioRepository; // <<< NUEVO
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PropiedadService {

    private final PropiedadRepository propiedadRepository;
    private final UsuarioRepository usuarioRepository; // <<< NUEVO

    public Propiedad savePropiedad(Propiedad propiedad) {
        return propiedadRepository.save(propiedad);
    }

    public Optional<Propiedad> findById(Long id) {
        return propiedadRepository.findById(id);
    }

    public List<Propiedad> findAll() {
        return propiedadRepository.findAll();
    }

    public boolean existsById(Long id) {
        return propiedadRepository.existsById(id);
    }

    public void deleteById(Long id) {
        propiedadRepository.deleteById(id);
    }

    public List<Propiedad> findByPropietarioEmail(String email) {
        Usuario propietario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Propietario no encontrado con email: " + email));
        return propiedadRepository.findByPropietarioId(propietario.getId());
    }
}