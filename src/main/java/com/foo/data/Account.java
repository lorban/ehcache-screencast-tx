package com.foo.data;

import java.io.Serializable;
import java.math.BigDecimal;

public class Account implements Serializable {

    private String id;
    private double balance;


    public Account() {
    }

    public Account(String id, double initialBalance) {
        this.id = id;
        this.balance = initialBalance;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }


    @Override
    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != getClass()) {
            return false;
        }
        Account other = (Account) obj;
        return id.equals(other.id) && balance == other.balance;
    }

    @Override
    public int hashCode() {
        return id.hashCode() + (int) balance;
    }

    @Override
    public String toString() {
        return "account #" + id + " with balance " + balance;
    }
}
