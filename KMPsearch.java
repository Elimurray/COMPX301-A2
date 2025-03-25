import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class KMPsearch {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("Usage: java KMPsearch \"target\" [filename.txt]");
            System.exit(1);
        }

        String pattern = args[0];
        int[] skipTable = buildSkipTable(pattern);

        if (args.length == 1) {
            // Print skip table only
            printSkipTable(pattern, skipTable);
        } else {
            // Search in file
            String filename = args[1];
            searchInFile(pattern, skipTable, filename);
        }
    }

    private static int[] buildSkipTable(String pattern) {
        int[] skipTable = new int[pattern.length()];
        skipTable[0] = 0;
        int len = 0;
        int i = 1;

        while (i < pattern.length()) {
            if (pattern.charAt(i) == pattern.charAt(len)) {
                len++;
                skipTable[i] = len;
                i++;
            } else {
                if (len != 0) {
                    len = skipTable[len - 1];
                } else {
                    skipTable[i] = 0;
                    i++;
                }
            }
        }
        return skipTable;
    }

    private static void printSkipTable(String pattern, int[] skipTable) {
        // Print pattern row
        System.out.print("*");
        for (char c : pattern.toCharArray()) {
            System.out.print("," + c);
        }
        System.out.println();

        // Get unique characters in pattern, sorted
        Set<Character> uniqueChars = new HashSet<>();
        for (char c : pattern.toCharArray()) {
            uniqueChars.add(c);
        }
        ArrayList<Character> sortedChars = new ArrayList<>(uniqueChars);
        Collections.sort(sortedChars);

        // Print rows for each character in pattern
        for (char c : sortedChars) {
            System.out.print(c);
            for (int i = 0; i < pattern.length(); i++) {
                if (pattern.charAt(i) == c) {
                    System.out.print("," + (i + 1));
                } else {
                    System.out.print("," + skipTable[i]);
                }
            }
            System.out.println();
        }

        // Print row for characters not in pattern
        System.out.print("*");
        for (int i = 0; i < pattern.length(); i++) {
            System.out.print("," + (i + 1));
        }
        System.out.println();
    }

    private static void searchInFile(String pattern, int[] skipTable, String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            int lineNumber = 0;

            while ((line = br.readLine()) != null) {
                lineNumber++;
                ArrayList<Integer> occurrences = kmpSearch(line, pattern, skipTable);
                
                for (int pos : occurrences) {
                    System.out.println(pos + " " + line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
    }

    private static ArrayList<Integer> kmpSearch(String text, String pattern, int[] skipTable) {
        ArrayList<Integer> occurrences = new ArrayList<>();
        int n = text.length();
        int m = pattern.length();
        int i = 0; // index for text
        int j = 0; // index for pattern

        while (i < n) {
            if (pattern.charAt(j) == text.charAt(i)) {
                i++;
                j++;
            }

            if (j == m) {
                // Pattern found
                occurrences.add(i - j + 1); // +1 for 1-based indexing
                j = skipTable[j - 1];
            } else if (i < n && pattern.charAt(j) != text.charAt(i)) {
                if (j != 0) {
                    j = skipTable[j - 1];
                } else {
                    i++;
                }
            }
        }

        return occurrences;
    }
}