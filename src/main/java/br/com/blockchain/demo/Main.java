package br.com.blockchain.demo;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Main {

    public static List<Block> blockchain = new ArrayList<Block>();
    public static int tamanoPrefixo = 3;
    public static String prefixo = new String(new char[tamanoPrefixo]).replace('\0', '0');

    public static void main(String[] args) {
//        minerarV1();
        minerarV2();
        testarMerkle();
    }

    private static void minerarV2() {
        Blockchain<Transaction> chain1 = new Blockchain<>();

        chain1.add(new Transaction("A")).add(new Transaction("B")).add(new Transaction("C"));

        Blockchain<Transaction> chain2 = chain1.duplicate();

        chain1.add(new Transaction("D"));

        System.out.printf("Chain 1: %s%n", chain1.getHead().getHash());
        System.out.printf("Chain 2: %s%n", chain2.getHead().getHash());
        System.out.printf("sincronizado ?: %s%n", chain1.getHead().getHash().equals(chain2.getHead().getHash()));

        chain2.add(new Transaction("D"));

        System.out.printf("Chain 1: %s%n", chain1.getHead().getHash());
        System.out.printf("Chain 2: %s%n", chain2.getHead().getHash());
        System.out.printf("sincronizado: %s%n", chain1.getHead().getHash().equals(chain2.getHead().getHash()));

//        assertTrue(chain1.blockChainHash().equals(chain2.blockChainHash()));

        System.out.println("Current Chain Head Transactions: ");
        for (BlockV2 block : chain1.chain) {
            for (Object tx : block.getTransactions()) {
                System.out.println("\t" + tx);
            }
        }

        BlockV2 headBlock = chain1.getHead();
        List<Transaction> merkleTree = headBlock.merkle();
//        assertTrue(headBlock.getMerkleRoot().equals(merkleTree.get(merkleTree.size() - 1)));

        // Validate block chain
//        assertTrue(chain1.validate());
        System.out.printf("Chain is Valid: %s%n", chain1.validate());
    }

    private static void minerarV1() {
        var genesis = new Block("Genesis", "0", new Date().getTime());
        genesis.minerar(tamanoPrefixo);
        blockchain.add(genesis);

        var b1 = new Block("B1", genesis.getHash(), new Date().getTime());
        b1.minerar(tamanoPrefixo);
        blockchain.add(b1);

        var b2 = new Block("B2", b1.getHash(), new Date().getTime());
        b1.minerar(tamanoPrefixo);
        blockchain.add(b2);

        blockchain.forEach(System.out::println);
        validarBlockChain();
    }

    public static void validarBlockChain() {
        boolean flag = true;
        for (int i = 0; i < blockchain.size(); i++) {
            String previousHash = i == 0 ? "0"
                    : blockchain.get(i - 1)
                    .getHash();
            flag = blockchain.get(i)
                    .getHash()
                    .equals(blockchain.get(i).calcularHash())
                    && previousHash.equals(blockchain.get(i)
                    .getPreviousHash())
                    && blockchain.get(i)
                    .getHash()
                    .substring(0, tamanoPrefixo)
                    .equals(prefixo);
            if (!flag)
                break;
        }

        System.out.println("blockchain é " + (!flag ? "valida" : "invalida"));
    }

    private static void testarMerkle() {

        Blockchain<Transaction> chain1 = new Blockchain<>();

        chain1.add(new Transaction("A")).add(new Transaction("B")).add(new Transaction("C")).add(new Transaction("D"));

        BlockV2<Transaction> block = chain1.getHead();

        System.out.println("Hask merkle :" + block.merkle());

        // pegando transacao do bloco
        Transaction tx = block.getTransactions().get(0);

        // valida transacoes do bloco
        var valido = block.validTransaction();
        System.out.println("transacoes sao " + (valido ? "validas" : "invalidas"));

        tx.setValue("Z");// falsificando transacao

        var novaValidacao = block.validTransaction();

        System.out.println("transacoes sao " + (novaValidacao ? "validas" : "invalidas"));

    }
}
