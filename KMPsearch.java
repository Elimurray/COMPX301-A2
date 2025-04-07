import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class KMPsearch {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("Usage: java kmpSearch \"target\" [filename.txt]");
            System.exit(1);
        }

        String pattern = args[0];
        Map<Character, int[]> kmpTable = buildkmpTable(pattern);

        if (args.length == 1) {
            printkmpTable(pattern, kmpTable);
        } else {
            String filename = args[1];
            searchInFile(pattern, kmpTable, filename);
        }
    }

    private static Map<Character, int[]> buildkmpTable(String pattern) {
        Map<Character, int[]> kmp = new HashMap<>();
        Set<Character> uniqueChars = new HashSet<>();
        
        // Identify all unique characters in pattern
        for (char c : pattern.toCharArray()) {
            uniqueChars.add(c);
        }

        // Initialize kmp table for each unique character
        for (char c : uniqueChars) {
            kmp.put(c, new int[pattern.length()]);
        }

        // Build the kmp table
        for (int state = 0; state < pattern.length(); state++) {
            for (char c : uniqueChars) {
                if (c == pattern.charAt(state)) {
                    // Match case: move to next state
                    kmp.get(c)[state] = state + 1;
                } else {
                    // Mismatch case: find the longest prefix that is also a suffix
                    int restartState = 0;
                    String current = pattern.substring(0, state) + c;
                    for (int k = 1; k <= state; k++) {
                        if (pattern.startsWith(current.substring(k))) {
                            restartState = state + 1 - k;
                            break;
                        }
                    }
                    kmp.get(c)[state] = restartState;
                }
            }
        }

        return kmp;
    }

    private static void printkmpTable(String pattern, Map<Character, int[]> kmpTable) {
        // Print header row
        System.out.print("*");
        for (int i = 0; i < pattern.length(); i++) {
            System.out.print("," + pattern.charAt(i));
        }
        System.out.println();

        // Print rows for each character in pattern (sorted)
        ArrayList<Character> sortedChars = new ArrayList<>(kmpTable.keySet());
        Collections.sort(sortedChars);
        
        for (char c : sortedChars) {
            System.out.print(c);
            for (int state = 0; state < pattern.length(); state++) {
                int skip = kmpTable.get(c)[state];
                System.out.print("," + (state + 1 - skip));
            }
            System.out.println();
        }

        // Print wildcard row
        System.out.print("*");
        for (int state = 0; state < pattern.length(); state++) {
            System.out.print("," + (state + 1));
        }
        System.out.println();
    }

    private static void searchInFile(String pattern, Map<Character, int[]> kmpTable, String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            int lineNumber = 0;

            while ((line = br.readLine()) != null) {
                lineNumber++;
                ArrayList<Integer> occurrences = kmpSearch(line, pattern, kmpTable);
                
                for (int pos : occurrences) {
                    System.out.println((pos + 1) + " " + line); 
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
    }

    private static ArrayList<Integer> kmpSearch(String text, String pattern, Map<Character, int[]> kmpTable) {
        ArrayList<Integer> occurrences = new ArrayList<>();
        int n = text.length();
        int m = pattern.length();
        int currentState = 0;

        for (int i = 0; i < n; i++) {
            char c = text.charAt(i);
            int[] transitions = kmpTable.getOrDefault(c, new int[m]);
            
            if (currentState < m) {
                currentState = transitions[currentState];
            } else {
                currentState = 0;
            }

            if (currentState == m) {
                // Pattern found
                occurrences.add(i - m + 1);
                currentState = 0; // Reset for next search
            }
        }

        return occurrences;
    }
}