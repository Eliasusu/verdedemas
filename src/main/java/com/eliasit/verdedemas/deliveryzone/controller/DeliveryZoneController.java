package com.eliasit.verdedemas.deliveryzone.controller;

import java.util.List;
import java.util.logging.Logger;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eliasit.verdedemas.deliveryzone.entity.DeliveryZone;
import com.eliasit.verdedemas.deliveryzone.service.DeliveryZoneService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping("/api/delivery-zones")
@RequiredArgsConstructor
public class DeliveryZoneController {
    private Logger log = Logger.getLogger(DeliveryZoneController.class.getName());

    private final DeliveryZoneService deliveryZoneService;

    @GetMapping()
    public List<DeliveryZone> list() {
        log.info("Listando Delivery Zone actives");
        return deliveryZoneService.listActive();
    }
    
}
