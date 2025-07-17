package com.jpmc.midascore.foundation;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Incentive {
    private float amount;

    @JsonProperty("amount")
    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }
}