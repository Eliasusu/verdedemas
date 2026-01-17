package com.eliasit.verdedemas.shared.entity;

public enum DeliveryDay {
    FRIDAY_PM("Viernes tarde", "17:00-20:00"),
    SATURDAY_AM("Sábado mañana", "09:00-13:00"),
    SATURDAY_PM("Sábado tarde", "15:00-19:00");
    
    private final String displayName;
    private final String timeRange;
    
    DeliveryDay(String displayName, String timeRange) {
        this.displayName = displayName;
        this.timeRange = timeRange;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getTimeRange() {
        return timeRange;
    }
}
