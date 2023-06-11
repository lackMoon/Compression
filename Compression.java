import lib.Archive;
import lib.BinaryTrie;
import lib.BitSequence;
import lib.Match;
import util.FileUtils;
import util.ObjectReader;
import util.ObjectWriter;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Compression {
    public static final File CWD = new File(System.getProperty("user.dir"));
    private static final int R = 65535;
    private static final String COMPRESSION_SUFFIX = "sz";
    public static int[] buildFrequencyTable(char[] inputSymbols) {
        int maxValue = 0;
        int[] table = new int[R];
        for (char symbol : inputSymbols) {
            table[symbol]++;
            if (symbol > maxValue) {
                maxValue = symbol;
            }
        }
        int[] frequencyTable = new int[maxValue + 1];
        System.arraycopy(table, 0, frequencyTable, 0, maxValue + 1);
        return frequencyTable;
    }
    public static void encode(String compressionName, String[] fileNames) {
        ArrayList<Archive> archives = new ArrayList<>();
        for (String fileName : fileNames) {
            //Read the file as 8 bit symbols.
            char[] inputSymbols = FileUtils.readFile(fileName);
            Integer symbolNums = inputSymbols.length;
            //Build frequency table.
            int[] frequencyTable = buildFrequencyTable(inputSymbols);
            //Use frequency table to construct a binary decoding trie.
            BinaryTrie trie = new BinaryTrie(frequencyTable);
            //Use binary trie to create lookup table for encoding.
            BitSequence[] lookupTable = trie.buildLookupTable();
            //Create a list of bitsequences.
            List<BitSequence> bitSequences = new ArrayList<>();
            // For each 8 bit symbol:
            //    Lookup that symbol in the lookup table.
            //    Add the appropriate bit sequence to the list of bitsequences.
            for (char symbol : inputSymbols) {
                bitSequences.add(lookupTable[symbol]);
            }
            //Assemble all bit sequences into one huge bit sequence.
            BitSequence bitSequence = BitSequence.assemble(bitSequences);
            //Write the archive object to the compression file.
            Archive archive = new Archive(fileName, trie, symbolNums, bitSequence);
            archives.add(archive);
        }
        ObjectWriter objectWriter = new ObjectWriter(compressionName + "." + COMPRESSION_SUFFIX);
        objectWriter.writeObject(archives);
    }

    public static void decode(String compressionFile, String path) {
        ObjectReader objectReader = new ObjectReader(compressionFile);
        Object src = objectReader.readObject();
        File targetPath = CWD;
        ArrayList<Archive> archives = (ArrayList<Archive>) src;
        if (!Objects.isNull(path)) {
            targetPath = new File(CWD + "/" + path);
            if (!targetPath.exists()) {
                targetPath.mkdir();
            }
        }
        for (Archive archive : archives) {
            //Read the Huffman coding trie.
            BinaryTrie binaryTrie = archive.getTrie();
            //read the number of symbols.
            Integer symbolNums = archive.getSymbolNums();
            char[] outputSymbols = new char[symbolNums];
            //Read the massive bit sequence corresponding to the original txt.
            BitSequence bitSequence = archive.getBitSequence();
            //Repeat until there are no more symbols:
            //    Perform a longest prefix match on the massive sequence.
            //    Record the symbol in some data structure.
            //    Create a new bit sequence containing the remaining unmatched bits.
            for (int i = 0; i < symbolNums; i++) {
                Match match = binaryTrie.longestPrefixMatch(bitSequence);
                outputSymbols[i] = match.getSymbol();
                int length = match.getSequence().length();
                bitSequence = bitSequence.allButFirstNBits(length);
            }
            //Write the symbols in some data structure to the specified file.
            FileUtils.writeCharArray(targetPath + "/" + archive.getSourceFileName(), outputSymbols);
        }
    }

    public static void errorMsg(String msg) {
        System.out.println(msg);
        System.exit(0);
    }

    public static int validateLength(String[] args) {
        int length = args.length;
        if (length < 2) {
            errorMsg("Incorrect operands.");
        }
        return length;
    }

    public static void validateFiles(String[] args) {
        for (String arg : args) {
            String suffix = "";
            int index = arg.lastIndexOf('.');
            if(index > 0) {
                suffix = arg.substring(index + 1);
            }
            if (suffix.equals(COMPRESSION_SUFFIX)) {
                errorMsg("Cannot compress the compressed files");
            }
        }
    }
    public static void main(String[] args) {
        int argLength = validateLength(args);
        String firstArg = args[0];
        if (firstArg.equals(COMPRESSION_SUFFIX)) {
            String[] files = Arrays.copyOfRange(args, 2, argLength);
            validateFiles(files);
            encode(args[1], files);
        } else if (firstArg.equals("un" + COMPRESSION_SUFFIX)) {
            File file = new File(args[1]);
            if (!file.exists()) {
                errorMsg("The specified compression Files is not exists.");
            }
            decode(args[1], args[2]);
        } else {
            errorMsg("No command with that name exists.");
        }
    }
}
