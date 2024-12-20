package com.example.demo.entity;

import java.util.Map;
public class MessageRequest {

    private Map<String,Object> message;
    private String topic;
    private String kafkaCluster;
    private String callbackUrl;

    // Getters and Setters

    public Map<String,Object> getMessage() {
        return message;
    }

    public void setMessage(Map<String,Object> message) {
        this.message = message;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getKafkaCluster() {
        return kafkaCluster;
    }

    public void setKafkaCluster(String kafkaCluster) {
        this.kafkaCluster = kafkaCluster;
    }

    public String getCallbackUrl() {
        return callbackUrl;
    }

    public void setCallbackUrl(String callbackUrl) {
        this.callbackUrl = callbackUrl;
    }
}
