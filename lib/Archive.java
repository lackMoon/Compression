package lib;

import java.io.Serializable;

public class Archive implements Serializable {
    private String sourceFileName;
    private BinaryTrie trie;
    private Integer symbolNums;
    private BitSequence bitSequence;
    public Archive(String sourceFileName, BinaryTrie trie,
                    Integer symbolNums, BitSequence bitSequence) {
        this.sourceFileName = sourceFileName;
        this.trie = trie;
        this.symbolNums = symbolNums;
        this.bitSequence = bitSequence;
    }

    public String getSourceFileName() {
        return sourceFileName;
    }

    public BinaryTrie getTrie() {
        return trie;
    }

    public Integer getSymbolNums() {
        return symbolNums;
    }

    public BitSequence getBitSequence() {
        return bitSequence;
    }
}
