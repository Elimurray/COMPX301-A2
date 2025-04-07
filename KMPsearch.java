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
            System.err.println("Usage: java KMPsearch \"target\" [filename.txt]");
            System.exit(1);
        }

        String pattern = args[0];
        Map<Character, int[]> skipTable = buildSkipTable(pattern);

        if (args.length == 1) {
            printSkipTable(pattern, skipTable);
        } else {
            String filename = args[1];
            searchInFile(pattern, skipTable, filename);
        }
    }

    private static Map<Character, int[]> buildSkipTable(String pattern) {
        Map<Character, int[]> skipTable = new HashMap<>();
        Set<Character> charsInPattern = new HashSet<>();
        
        // Get all unique characters in pattern
        for (char c : pattern.toCharArray()) {
            charsInPattern.add(c);
            skipTable.put(c, new int[pattern.length()]);
        }
        
        // Build the table incrementally
        for (int pos = 0; pos < pattern.length(); pos++) {
            for (char c : charsInPattern) {
                if (c == pattern.charAt(pos)) {
                    skipTable.get(c)[pos] = 0; // Exact match
                } else {
                    // Create test string by substituting character
                    String temp = pattern.substring(0, pos) + c;
                    
                    // Find maximum alignment shift
                    int maxShift = 0;
                    for (int shift = 1; shift <= pos; shift++) {
                        String patternPart = pattern.substring(0, pos + 1 - shift);
                        String tempPart = temp.substring(shift);
                        if (patternPart.equals(tempPart)) {
                            maxShift = shift;
                            break;
                        }
                    }
                    
                    skipTable.get(c)[pos] = maxShift > 0 ? maxShift : pos + 1;
                }
            }
        }
        
        return skipTable;
    }

    private static void printSkipTable(String pattern, Map<Character, int[]> skipTable) {
        // Print header row
        System.out.print("*");
        for (char c : pattern.toCharArray()) {
            System.out.print("," + c);
        }
        System.out.println();

        // Character rows (sorted alphabetically)
        ArrayList<Character> chars = new ArrayList<>(skipTable.keySet());
        Collections.sort(chars);
        
        for (char c : chars) {
            System.out.print(c);
            for (int pos = 0; pos < pattern.length(); pos++) {
                System.out.print("," + skipTable.get(c)[pos]);
            }
            System.out.println();
        }

        // Wildcard row
        System.out.print("*");
        for (int pos = 0; pos < pattern.length(); pos++) {
            System.out.print("," + (pos + 1));
        }
        System.out.println();
    }

    private static void searchInFile(String pattern, Map<Character, int[]> skipTable, String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            int lineNumber = 0;

            while ((line = br.readLine()) != null) {
                lineNumber++;
                ArrayList<Integer> matches = searchLine(line, pattern, skipTable);
                for (int pos : matches) {
                    System.out.println((pos + 1) + " " + line); // 1-based indexing
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
    }

    private static ArrayList<Integer> searchLine(String text, String pattern, Map<Character, int[]> skipTable) {
        ArrayList<Integer> matches = new ArrayList<>();
        int n = text.length();
        int m = pattern.length();
        
        int i = 0; // Current position in text
        while (i <= n - m) {
            int j = 0;
            // Check for match at current position
            while (j < m && text.charAt(i + j) == pattern.charAt(j)) {
                j++;
            }
            
            if (j == m) {
                // Full match found
                matches.add(i);
                i++; // Move to next position
            } else {
                // Mismatch - use skip table
                char mismatchChar = text.charAt(i + j);
                int skip = skipTable.getOrDefault(mismatchChar, new int[m])[j];
                i += Math.max(skip, 1); // Always move forward at least 1
            }
        }
        
        return matches;
    }
}