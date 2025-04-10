package com.example.smart_room.service;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import org.eclipse.paho.client.mqttv3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@Service
public class AdafruitMqttSubscriberService implements MqttCallback {

    private static final Logger logger = Logger.getLogger(AdafruitMqttSubscriberService.class.getName());

    @Value("${adafruit.mqtt.broker}")
    private String brokerUrl;

    @Value("${adafruit.mqtt.username}")
    private String username;

    @Value("${adafruit.mqtt.key}")
    private String key;

    @Value("${adafruit.mqtt.feeds}")
    private String feeds; // Danh sách feed

    private MqttClient client;
    private final DatabaseReference firebaseDbRef;

    public AdafruitMqttSubscriberService(FirebaseDatabase firebaseDatabase) {
        this.firebaseDbRef = firebaseDatabase.getReference("feeds");
    }

    @PostConstruct
    public void init() {
        try {
            client = new MqttClient(brokerUrl, MqttClient.generateClientId());
            MqttConnectOptions options = new MqttConnectOptions();
            options.setUserName(username);
            options.setPassword(key.toCharArray());
            client.setCallback(this);
            client.connect(options);

            // Subscribe tất cả các feed trong danh sách
            List<String> feedList = Arrays.asList(feeds.split(","));
            for (String feed : feedList) {
                String topic = username + "/feeds/" + feed.trim();
                client.subscribe(topic);
                logger.info("Subscribed to MQTT topic: " + topic);
            }
        } catch (MqttException e) {
            logger.severe("MQTT connection error: " + e.getMessage());
        }
    }

    @Override
    public void connectionLost(Throwable cause) {
        logger.severe("MQTT connection lost: " + cause.getMessage());
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) {
        String payload = new String(message.getPayload());
        String feedKey = topic.substring(topic.lastIndexOf("/") + 1); // Lấy feedKey từ topic
        logger.info("MQTT message received for " + feedKey + ": " + payload);

        // Khi có tin nhắn mới, cập nhật Firebase
        firebaseDbRef.child(feedKey).setValueAsync(Map.of(
                "value", payload,
                "timestamp", LocalDateTime.now().toString()));
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        // Không cần xử lý cho subscriber
    }
}
