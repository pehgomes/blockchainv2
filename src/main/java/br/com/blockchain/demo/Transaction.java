package br.com.blockchain.demo;

public class Transaction implements TX {

    private String hash;
    private String value;

    public String hash() {
        return hash;
    }

    public Transaction(String value) {
        this.hash = Util.generateHash(value);
        this.setValue(value);
    }


    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.hash = Util.generateHash(value);
        this.value = value;
    }

    public String toString() {
        return this.hash + " : " + this.getValue();
    }
}
