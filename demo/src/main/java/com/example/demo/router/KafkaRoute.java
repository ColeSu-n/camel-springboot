package com.example.demo.router;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.kafka.KafkaConstants;
import org.apache.camel.component.kafka.KafkaManualCommit;
import org.apache.commons.codec.binary.StringUtils;
import org.springframework.stereotype.Component;
import java.util.Properties;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;

@Component
public class KafkaRoute extends RouteBuilder {
    @Value("${kafka.cluster1.brokers}")
    private String kafkaCluster1Brokers;

    @Value("${kafka.cluster2.brokers}")
    private String kafkaCluster2Brokers;
    @Value("${kafka.cluster2.manualOffset}")
    private long cluster2ManualOffset; // 从配置中获取手动指定的偏移量
    @Value("${kafka.cluster1.topicPattern}")
    private String kafkaCluster1TopicPattern;
    @Value("${kafka.cluster1.manualOffset}")
    private long cluster1ManualOffset; // 从配置中获取手动指定的偏移量
    @Value("${kafka.cluster2.topicPattern}")
    private String kafkaCluster2TopicPattern;
    @Override
    public void configure() throws Exception {
        // 监听 Kafka 集群1 中的所有 Topic
        from("kafka:" + kafkaCluster1TopicPattern + "?brokers=" + kafkaCluster1Brokers+"&groupId=test&allowManualCommit=true&autoOffsetReset=latest&autoCommitEnable=false&maxPollRecords=3&seekTo=0")
            .log("Received message from Kafka Cluster 1, Topic: ${header.topic} ")
            .process(exchange -> {
                exchange.getIn().getHeaders().forEach((key, value) -> {
                    System.out.println("Header: " + key + " = " + value);
                });
                // 获取消息的主题和分区
                String topic = exchange.getIn().getHeader(KafkaConstants.TOPIC, String.class);
                
                String offset = exchange.getIn().getHeader(KafkaConstants.OFFSET, String.class);
                String topicPartition = exchange.getIn().getHeader(KafkaConstants.PARTITION, String.class);

                    // 定义新的偏移量和元数据
                String message = exchange.getIn().getBody(String.class);
                String callbackUrl = exchange.getIn().getHeader("callbackUrl", String.class);
                // 转发消息
                exchange.getContext().createProducerTemplate().sendBodyAndHeader( callbackUrl, message, "Content-Type", "application/json");
                // KafkaManualCommit manual =
                //     exchange.getIn().getHeader(KafkaConstants.MANUAL_COMMIT, KafkaManualCommit.class);
                // if (manual != null) {
                //     manual.commitSync();
                // }
            });


        // 监听 Kafka 集群2 中的所有 Topic
        from("kafka:" + kafkaCluster2TopicPattern + "?brokers=" + kafkaCluster2Brokers+"&groupId=test")
        .log("Received message from Kafka Cluster 2, Topic: ${header.topic} ")
            .process(exchange -> {
                // 获取消息的主题和分区
                String topic = exchange.getIn().getHeader(KafkaConstants.TOPIC, String.class);
                String offset = exchange.getIn().getHeader(KafkaConstants.OFFSET, String.class);
                String topicPartition = exchange.getIn().getHeader(KafkaConstants.PARTITION, String.class);
                String message = exchange.getIn().getBody(String.class);
                String callbackUrl = exchange.getIn().getHeader("callbackUrl", String.class);
                // 转发消息
                exchange.getContext().createProducerTemplate().sendBodyAndHeader( callbackUrl, message, "Content-Type", "application/json");
            });
    }
}
