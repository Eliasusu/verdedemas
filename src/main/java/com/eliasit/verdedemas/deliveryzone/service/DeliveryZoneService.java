package com.eliasit.verdedemas.deliveryzone.service;

import java.util.List;
import java.util.logging.Logger;

import org.springframework.stereotype.Service;

import com.eliasit.verdedemas.deliveryzone.entity.DeliveryZone;
import com.eliasit.verdedemas.deliveryzone.repository.DeliveryZoneRepository;
import com.eliasit.verdedemas.shared.exception.ResourceNotFoundException;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class DeliveryZoneService {
    private final Logger log = Logger.getLogger(DeliveryZoneService.class.getName());

    private final DeliveryZoneRepository deliveryZoneRepository;

    @SuppressWarnings("null")
    public DeliveryZone getZoneById(Long id){
        log.info("Entrando a: getZoneById");

        return deliveryZoneRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Zona de entrega no encontrada: " + id));

    }

    public List<DeliveryZone> listActive(){
        return deliveryZoneRepository.findByIsActiveTrue();
    }
}
