package br.com.blockchain.demo;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class Blockchain<T extends TX> {

    public static final int BLOCK_SIZE = 10;
    public List<BlockV2<T>> chain = new ArrayList<>();

    public Blockchain() {
        chain.add(newBlockV2()); // genesis
    }

    public Blockchain(List<BlockV2<T>> blocks) {
        this();
        chain = blocks;
    }

    public BlockV2<T> getHead() {

        BlockV2<T> result;
        if (this.chain.size() > 0) {
            result = this.chain.get(this.chain.size() - 1);
        } else {
            throw new RuntimeException("No BlockV2's have been added to chain...");
        }

        return result;
    }

    public void addAndValidateBlockV2(BlockV2<T> block) { // validacao

        BlockV2<T> current = block;
        for (int i = chain.size() - 1; i >= 0; i--) {
            BlockV2<T> b = chain.get(i);
            if (b.getHash().equals(current.getPreviousHash())) {
                current = b;
            } else {

                throw new RuntimeException("BlockV2 Invalid");
            }

        }

        this.chain.add(block);

    }

    public boolean validate() {

        String previousHash = chain.get(0).getHash();
        for (BlockV2<T> block : chain) {
            String currentHash = block.getHash();
            if (!currentHash.equals(previousHash)) {
                return false;
            }

            previousHash = currentHash;

        }

        return true;

    }

    public BlockV2<T> newBlockV2() {
        int count = chain.size();
        String previousHash = "root";

        if (count > 0)
            previousHash = blockChainHash();

        BlockV2<T> block = new BlockV2<T>();

        block.setTimeStamp(System.currentTimeMillis());
        block.setIndex(count);
        block.setPreviousHash(previousHash);
        return block;
    }

    public Blockchain<T> add(T transaction) {

        if (chain.size() == 0) {
            this.chain.add(newBlockV2());
        }

        if (getHead().getTransactions().size() >= BLOCK_SIZE) {
            this.chain.add(newBlockV2());
        }

        getHead().add(transaction);

        return this;
    }

    public void DeleteAfterIndex(int index) {
        if (index >= 0) {
            Predicate<BlockV2<T>> predicate = b -> chain.indexOf(b) >= index;
            chain.removeIf(predicate);
        }
    }

    public Blockchain<T> duplicate() {
        List<BlockV2<T>> clonedChain = new ArrayList<>();
        Consumer<BlockV2> consumer = (b) -> clonedChain.add(b.duplicate());
        chain.forEach(consumer);
        return new Blockchain<T>(clonedChain);
    }

    public List<BlockV2<T>> getChain() {
        return chain;
    }

    public void setChain(List<BlockV2<T>> chain) {
        this.chain = chain;
    }

    public String blockChainHash() {
        return getHead().getHash();
    }
}
