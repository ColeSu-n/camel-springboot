package com.example.demo.controller;
import java.util.HashMap;

import org.apache.camel.ProducerTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.HashMap;
import com.example.demo.entity.MessageRequest;

@RestController
@RequestMapping("/kafka")
public class KafkaController {

    @Autowired
    private ProducerTemplate producerTemplate;

    @PostMapping("/send")
    public String sendMessage(@RequestBody MessageRequest message) {
        // 动态构建 Kafka URI
        String uri = String.format("kafka:%s?brokers=%s", message.getTopic(), message.getKafkaCluster());

        // 设置消息头和消息体
        Map<String,Object> hashMap = new HashMap<String,Object>();
        hashMap.put("kafkaCluster", message.getKafkaCluster());
        hashMap.put("topic", message.getTopic());
        hashMap.put("callbackUrl", message.getCallbackUrl());
        producerTemplate.sendBodyAndHeaders(uri, message.getMessage(), hashMap);
        return "Message sent to Kafka cluster: " + message.getKafkaCluster() + ", topic: " + message.getTopic();
    }
}
