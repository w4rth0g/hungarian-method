package student.mikolajgostkowski;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CombinationFinder {

    public static List<Map<String, Integer>> findCombinations(Map<String, Integer> inputMap, int targetSum) {
        List<Map<String, Integer>> combinations = new ArrayList<>();

        for (Map.Entry<String, Integer> entry : inputMap.entrySet()) {
            String key = entry.getKey();
            int value = entry.getValue();

            if (value >= targetSum) {
                // If the value of the current element is greater than or equal to the target sum,
                // then we can add it to the list of combinations on its own.
                Map<String, Integer> combination = new HashMap<>();
                combination.put(key, value);
                combinations.add(combination);
            } else {
                // Otherwise, we need to try combining it with other elements to see if we can reach the target sum.
                Map<String, Integer> remainingElements = new HashMap<>(inputMap);
                remainingElements.remove(key);

                List<Map<String, Integer>> subCombinations = findCombinations(remainingElements, targetSum - value);
                for (Map<String, Integer> subCombination : subCombinations) {
                    subCombination.put(key, value);
                }
                combinations.addAll(subCombinations);
            }
        }

        return combinations;
    }
}