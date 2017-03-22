package com.paspanaassarasee.spiltbill;

/**
 * Created by paspanaassarasee on 2/26/17.
 */

public class Transaction {

    private String name;
    private String amount;

    public Transaction(){

    }

    public Transaction(String name, String amount){
        this.name = name;
        this.amount = amount;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
