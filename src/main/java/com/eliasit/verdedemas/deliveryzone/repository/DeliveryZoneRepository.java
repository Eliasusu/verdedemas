package com.eliasit.verdedemas.deliveryzone.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eliasit.verdedemas.deliveryzone.entity.DeliveryZone;

public interface DeliveryZoneRepository extends JpaRepository<DeliveryZone, Long> {
    List<DeliveryZone> findByIsActiveTrue();
}
