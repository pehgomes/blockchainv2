package br.com.blockchain.demo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class BlockV2<E extends TX> {

    public long timeStamp;
    private int index;
    private List<E> transactions = new ArrayList<E>();
    public Map<String, E> transactionsMap = new HashMap<>();
    private String hash;
    private String previousHash;
    private String merkleRoot;
    private String nonce = "0000";

    public BlockV2<E> add(E tx) {
        transactions.add(tx);
        transactionsMap.put(tx.hash(), tx);
        computeMerkleRoot();
        computeHash();
        return this;
    }

    public void computeMerkleRoot() {
        List<String> treeList = merkle();
        setMerkleRoot(treeList.get(treeList.size() - 1));
    }

    public BlockV2<E> duplicate() {
        BlockV2<E> clone = new BlockV2();
        clone.setIndex(this.getIndex());
        clone.setPreviousHash(this.getPreviousHash());
        clone.setMerkleRoot(this.getMerkleRoot());
        clone.setTimeStamp(this.getTimeStamp());

        List<E> clonedtx = new ArrayList<E>();
        Consumer<E> consumer = clonedtx::add;
        this.getTransactions().forEach(consumer);
        clone.setTransactions(clonedtx);

        return clone;
    }

    public boolean validTransaction() {
        List<String> tree = merkle();
        String root = tree.get(tree.size() - 1);
        return root.equals(this.getMerkleRoot());
    }

    public List<String> merkle() {
        ArrayList<String> tree = new ArrayList<>();
        for (E t : transactions) {
            tree.add(t.hash());
        }
        int levelOffset = 0;
        for (int levelSize = transactions.size(); levelSize > 1; levelSize = (levelSize + 1) / 2) {
            for (int left = 0; left < levelSize; left += 2) {
                int right = Math.min(left + 1, levelSize - 1);
                String tleft = tree.get(levelOffset + left);
                String tright = tree.get(levelOffset + right);
                tree.add(Util.generateHash(tleft + tright));
            }
            levelOffset += levelSize;
        }
        return tree;
    }

    public void computeHash() {
        String serializedData = null;
        try {
            serializedData = new ObjectMapper().writeValueAsString(transactions);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        setHash(Util.generateHash(timeStamp + index + merkleRoot + serializedData + nonce + previousHash));
    }

    public String getHash() {

        if (hash == null) {
            computeHash();
        }

        return hash;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public List<E> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<E> transactions) {
        this.transactions = transactions;
    }

    public Map<String, E> getTransactionsMap() {
        return transactionsMap;
    }

    public void setTransactionsMap(Map<String, E> transactionsMap) {
        this.transactionsMap = transactionsMap;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getPreviousHash() {
        return previousHash;
    }

    public void setPreviousHash(String previousHash) {
        this.previousHash = previousHash;
    }

    public String getMerkleRoot() {
        return merkleRoot;
    }

    public void setMerkleRoot(String merkleRoot) {
        this.merkleRoot = merkleRoot;
    }

    public String getNonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }
}
