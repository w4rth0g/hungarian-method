package student.mikolajgostkowski;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ZeroCover {
    public static Map<Integer, Integer>[] coverZeros(Integer[][] matrix) {
// Initialize a list to store the combinations of lines
        List<Map<Integer, Integer>[]> combinations = new ArrayList<>();
        // Generate all possible combinations of lines
        generateCombinations(matrix, new boolean[matrix.length][matrix[0].length], 0, 0, combinations);

        // Choose the combination with the least number of lines
        Map<Integer, Integer>[] bestCombination = null;
        int minLines = Integer.MAX_VALUE;
        for (Map<Integer, Integer>[] combination : combinations) {
            int lines = combination[0].size() + combination[1].size();
            if (lines < minLines) {
                bestCombination = combination;
                minLines = lines;
            }
        }

        return bestCombination;
    }

    // Recursive method to generate all possible combinations of lines
    private static void generateCombinations(Integer[][] matrix, boolean[][] covered, int row, int col, List<Map<Integer, Integer>[]> combinations) {
        // Check if all of the zeros have been covered
        if (allZerosCovered(matrix, covered)) {
            // If all of the zeros have been covered, add the current combination of lines to the list
            Map<Integer, Integer>[] combination = getCombination(covered);
            combinations.add(combination);
            return;
        }

        // If we've reached the end of the matrix, return
        if (row == matrix.length || col == matrix[0].length) {
            return;
        }

        // Skip over cells that are already covered or do not contain a zero
        if (covered[row][col] || matrix[row][col] != 0) {
            generateCombinations(matrix, covered, row + (col + 1) / matrix[0].length, (col + 1) % matrix[0].length, combinations);
            return;
        }

        // Try covering the current zero with a horizontal line
        covered[row][col] = true;
        generateCombinations(matrix, covered, row + (col + 1) / matrix[0].length, (col + 1) % matrix[0].length, combinations);
        covered[row][col] = false;

        // Try covering the current zero with a vertical line
        for (int i = row + 1; i < matrix.length; i++) {
            if (matrix[i][col] == 0) {
                covered[i][col] = true;
            }
        }
        generateCombinations(matrix, covered, row + (col + 1) / matrix[0].length, (col + 1) % matrix[0].length, combinations);
        for (int i = row + 1; i < matrix.length; i++) {
            if (matrix[i][col] == 0) {
                covered[i][col] = false;
            }
        }
    }

    // Helper method to check if all of all of the zeros in the matrix have been covered
    private static boolean allZerosCovered(Integer[][] matrix, boolean[][] covered) {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                if (matrix[i][j] == 0 && !covered[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }

    // Helper method to get the current combination of lines
    private static Map<Integer, Integer>[] getCombination(boolean[][] covered) {
        // Initialize maps to store the rows and columns where the lines were drawn
        Map<Integer, Integer> rowMap = new HashMap<>();
        Map<Integer, Integer> colMap = new HashMap<>();

        // Iterate through the covered matrix and add the rows and columns where the lines were drawn to the maps
        for (int i = 0; i < covered.length; i++) {
            for (int j = 0; j < covered[0].length; j++) {
                if (covered[i][j]) {
                    if (rowMap.containsKey(i)) {
                        rowMap.put(i, rowMap.get(i) + 1);
                    } else {
                        rowMap.put(i, 1);
                    }
                    if (colMap.containsKey(j)) {
                        colMap.put(j, colMap.get(j) + 1);
                    } else {
                        colMap.put(j, 1);
                    }
                }
            }
        }

        // Return an array containing the row and column maps
        return new Map[]{rowMap, colMap};
    }
}