package com.jpmc.midascore.listener;

import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Incentive;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class TransactionKafkaListener {
    private static final Logger log = LoggerFactory.getLogger(TransactionKafkaListener.class);

    private final UserRepository userRepository;
    private final RestTemplate restTemplate;

    private static final String INCENTIVE_API_URL = "http://localhost:8080/incentive";

    @Autowired
    public TransactionKafkaListener(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.restTemplate = new RestTemplate();
    }

    @KafkaListener(topics = "${general.kafka-topic}", groupId = "midas-core-group")
    public void listen(Transaction transaction) {
        log.info("Received Transaction: {}", transaction);

        // Validate transaction
        UserRecord sender = userRepository.findById(transaction.getSenderId()).orElse(null);
        UserRecord recipient = userRepository.findById(transaction.getRecipientId()).orElse(null);

        if (sender == null || recipient == null) {
            log.warn("Invalid sender/recipient IDs. Transaction discarded.");
            return;
        }

        if (sender.getBalance() < transaction.getAmount()) {
            log.warn("Insufficient balance. Transaction discarded.");
            return;
        }

        // Call Incentive API
        Incentive incentive = restTemplate.postForObject(
                INCENTIVE_API_URL,
                transaction,
                Incentive.class
        );

        float incentiveAmount = incentive != null ? incentive.getAmount() : 0f;
        log.info("Received incentive amount: {}", incentiveAmount);

        // Update balances (sender pays amount, recipient gets amount + incentive)
        sender.setBalance(sender.getBalance() - transaction.getAmount());
        recipient.setBalance(recipient.getBalance() + transaction.getAmount() + incentiveAmount);

        // Save updated users
        userRepository.save(sender);
        userRepository.save(recipient);
    }
}