package com.jpmc.midascore.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class UserRecord {

    @Id
    @GeneratedValue()
    private long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private float balance;

    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL)
    private List<TransactionRecord> sentTransactions = new ArrayList<>(); // Initialize here

    @OneToMany(mappedBy = "recipient", cascade = CascadeType.ALL)
    private List<TransactionRecord> receivedTransactions = new ArrayList<>(); // Initialize here

    // Protected no-args constructor (required by JPA)
    protected UserRecord() {
        // No need to initialize lists here since they're initialized above
    }

    // Constructor for manual creation
    public UserRecord(String name, float balance) {
        this.name = name;
        this.balance = balance;
        // Lists are already initialized, so no action needed
    }

    // Getters and setters (unchanged)
    @Override
    public String toString() {
        return String.format("User[id=%d, name='%s', balance='%f'", id, name, balance);
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public float getBalance() { return balance; }
    public void setBalance(float balance) { this.balance = balance; }

    public List<TransactionRecord> getSentTransactions() { return sentTransactions; }
    public List<TransactionRecord> getReceivedTransactions() { return receivedTransactions; }
}
