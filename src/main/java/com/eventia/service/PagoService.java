package com.eventia.service;

import com.eventia.model.Pago;
import com.eventia.repository.PagoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PagoService {

    private final PagoRepository pagoRepository;

    public Pago savePago(Pago pago) {
        return pagoRepository.save(pago);
    }

    public Optional<Pago> findById(Long id) {
        return pagoRepository.findById(id);
    }

    public List<Pago> findAll() {
        return pagoRepository.findAll();
    }

    public void deleteById(Long id) {
        pagoRepository.deleteById(id);
    }
}