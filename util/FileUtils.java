package util;

import java.util.ArrayList;
import lib.BinaryIn;
import lib.BinaryOut;
public class FileUtils {
    public static char[] readFile(String filename) {
        BinaryIn in = new BinaryIn(filename);
        ArrayList<Character> chars = new ArrayList<Character>();
        while (!in.isEmpty()) {
            chars.add(in.readChar());
        }
        char[] input = new char[chars.size()];
        for (int i = 0; i < input.length; i += 1) {
            input[i] = chars.get(i);
        }
        return input;
    }

    public static void writeCharArray(String filename, char[] chars) {
        BinaryOut out = new BinaryOut(filename);
        for (char c : chars) {
            out.write(c);
        }
        out.close();
    }
}
