package com.eliasit.verdedemas.shared.util;

public enum OrderStatus {
    PENDING,              // Acaba de crearse
    SENT_TO_WHATSAPP,     // Link enviado a WhatsApp
    CONFIRMED,            // Confirmado por vendedor
    PREPARING,            // En preparación
    DISPATCHED,           // En camino
    DELIVERED,            // Entregado
    CANCELLED             // Cancelado
}