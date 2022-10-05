package br.com.blockchain.demo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

public class Minerador<T extends TX> {

    List<T> transactionPool = new ArrayList<T>();
    Blockchain<T> chain = null;

    public Minerador(Blockchain chain) {
        this.chain = chain;
    }

    public void minerar(T tx) {
        transactionPool.add(tx);
        if (transactionPool.size() > Blockchain.BLOCK_SIZE) {
            createBlockAndApplyToChain();
        }
    }

    private void createBlockAndApplyToChain() {

        BlockV2 block = chain.newBlockV2();
        block.setPreviousHash(chain.getHead().getHash());
        block.setHash(proofOfWork(block));
        chain.addAndValidateBlockV2(block);
        transactionPool = new ArrayList<T>();
    }

    private String proofOfWork(BlockV2 block) {

        String nonceKey = block.getNonce();
        long nonce = 0;
        boolean nonceFound = false;
        String nonceHash = "";

        String serializedData = null;
        try {
            serializedData = new ObjectMapper().writeValueAsString(transactionPool);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        String message = block.getTimeStamp() + block.getIndex() + block.getMerkleRoot() + serializedData
                + block.getPreviousHash();

        while (!nonceFound) {

            nonceHash = Util.generateHash(message + nonce);
            nonceFound = nonceHash.substring(0, nonceKey.length()).equals(nonceKey);
            nonce++;

        }

        return nonceHash;

    }
}
