package lib;

import pq.ExtrinsicPQ;
import pq.MinHeap;

import java.io.Serializable;

public class BinaryTrie implements Serializable {
    private BinaryNode root;

    private int tableLength;
    private class BinaryNode implements Serializable {

        private char item;
        private int frequency;
        private BinaryNode leftChild;
        private BinaryNode rightChild;

        private BinaryNode(char item, int frequency) {
            this(item, frequency, null, null);
        }

        private BinaryNode(char item, int frequency, BinaryNode leftChild, BinaryNode rightChild) {
            this.item = item;
            this.frequency = frequency;
            this.leftChild = leftChild;
            this.rightChild = rightChild;
        }

        private BinaryNode getChild(int bit) {
            return bit == 0 ? this.leftChild : this.rightChild;
        }

        private boolean isLeafNode() {
            return (this.leftChild == null) && (this.rightChild == null);
        }

        public char getItem() {
            return item;
        }

        public void setItem(char item) {
            this.item = item;
        }

        public int getFrequency() {
            return frequency;
        }

        public void setFrequency(int frequency) {
            this.frequency = frequency;
        }

    }
    public BinaryTrie(int[] frequencyTable) {
        ExtrinsicPQ<BinaryNode> minPQ = new MinHeap<>();
        this.tableLength = frequencyTable.length;
        for (char c = 0; c < this.tableLength; c++) {
            if (frequencyTable[c] > 0) {
                int frequency = frequencyTable[c];
                BinaryNode node = new BinaryNode(c, frequency);
                minPQ.insert(node, frequency);
            }
        }
        while(minPQ.size() > 1) {
            BinaryNode leftChild = minPQ.remove();
            BinaryNode rightChild = minPQ.remove();
            int parentFrequency = leftChild.getFrequency() + rightChild.getFrequency();
            BinaryNode parent = new BinaryNode('\0', parentFrequency, leftChild, rightChild);
            minPQ.insert(parent, parentFrequency);
        }
        root = minPQ.remove();
    }
    public Match longestPrefixMatch(BitSequence querySequence) {
        char symbol = '\0';
        StringBuilder sequence = new StringBuilder();
        int length = querySequence.length();
        BinaryNode current = root;
        for (int i = 0; i < length; i++) {
            int bit = querySequence.bitAt(i);
            current = current.getChild(bit);
            char item = current.getItem();
            sequence.append(bit);
            if (current.isLeafNode()) {
                symbol = item;
                break;
            }
        }
        return new Match(new BitSequence(sequence.toString()), symbol);
    }
    public BitSequence[] buildLookupTable() {
        BitSequence[] lookupTable = new BitSequence[this.tableLength];
        buildLookupTable(lookupTable, root, "");
        return lookupTable;
    }

    private void buildLookupTable(BitSequence[] lookupTable, BinaryNode node, String sequence) {
        if (node.isLeafNode()) {
            char item = node.getItem();
            lookupTable[item] = new BitSequence(sequence.toString());
        } else {
            buildLookupTable(lookupTable, node.leftChild, sequence + "0");
            buildLookupTable(lookupTable, node.rightChild, sequence + "1");
        }
    }

}