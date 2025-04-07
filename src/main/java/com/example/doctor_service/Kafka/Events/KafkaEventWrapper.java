package com.example.doctor_service.Kafka.Events;

public class KafkaEventWrapper {
    private String eventType;
    private Object payload;

    public KafkaEventWrapper(String eventType, Object payload) {
        this.eventType = eventType;
        this.payload = payload;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public Object getPayload() {
        return payload;
    }

    public void setPayload(Object payload) {
        this.payload = payload;
    }
}
