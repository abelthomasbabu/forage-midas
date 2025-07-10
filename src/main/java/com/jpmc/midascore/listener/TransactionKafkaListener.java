package com.jpmc.midascore.listener;

import com.jpmc.midascore.foundation.Transaction;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class TransactionKafkaListener {

    private static final Logger log = LoggerFactory.getLogger(TransactionKafkaListener.class);

    private final List<Transaction> receivedTransactions = Collections.synchronizedList(new ArrayList<>());

    @KafkaListener(topics = "${general.kafka-topic}", groupId = "midas-core-group", containerFactory = "kafkaListenerContainerFactory")
    public void listen(Transaction transaction) {
        log.info("Received Transaction: {}", transaction);
        receivedTransactions.add(transaction);
    }

    public List<Transaction> getReceivedTransactions() {
        return receivedTransactions;
    }

    public void clearReceivedTransactions() {
        receivedTransactions.clear();
    }
}