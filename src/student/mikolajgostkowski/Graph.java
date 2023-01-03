package student.mikolajgostkowski;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

public class Graph {
    private Integer[][] costMatrix;
    private Integer[][] costMatrixCp;
    private int dim;
    private int totalCost;
    private List<Point> zeroLocations;
    private Set<Integer> coveredRows;
    private Set<Integer> coveredCols;

    public Integer[][] getCostMatrixCp() {
        return costMatrixCp;
    }

    public void readCostMat(String fileName) throws IOException {
        File matFile = new File(fileName);

        BufferedReader br = new BufferedReader(new FileReader(matFile));

        int[] nNodes = Arrays.stream(br.readLine().split(" ")).mapToInt(Integer::parseInt).toArray();
        if (nNodes[0] != nNodes[1]) {
            throw new IOException("Matrix is not a square matrix");
        }
        this.dim = nNodes[0];
        this.costMatrix = new Integer[nNodes[0]][nNodes[1]];
        this.costMatrixCp = new Integer[nNodes[0]][nNodes[1]];
        String st;
        int n = 0;
        while((st = br.readLine()) != null) {
            this.costMatrix[n] = Arrays.stream(st.split(" ")).map(Integer::parseInt).toArray(Integer[]::new);
            this.costMatrixCp[n] = Arrays.stream(st.split(" ")).map(Integer::parseInt).toArray(Integer[]::new);
            n++;
        }
    }

    public List<Integer> hungarianMethod() {
        //step1: reduce by row and by column (minimum from row)
        this.reduce();
        //step2: check minimum lines again, if n -> finish, else -> additional zeros
        this.printGraph();
        while (this.optimalLineNum() != this.dim) {
            // make additional zeros
            this.createAdditionalZeros();
            this.printGraph();
        }

        // finish
        return this.findOptimalAssignment();
    }

    public int getZerosAmount() {
        List<Point> zeroLocations = new ArrayList<>();
        for (int i = 0; i < this.dim; i++) {
            for (int j = 0; j < this.dim; j++) {
                 if (this.costMatrix[i][j] == 0) {
                    zeroLocations.add(new Point(i, j));
                }
            }
        }

        this.zeroLocations = zeroLocations;
        return zeroLocations.size();
    }

    public int optimalLineNum() {
        Map<Integer, Integer>[] allLinesComb = ZeroCover.coverZeros(this.costMatrix);

        System.out.println("Row lines: " + allLinesComb[0]);
        System.out.println("Col lines: " + allLinesComb[1]);
        System.out.println("------");

        Map<Integer, Integer>[] minLinesComb = this.getMapsForSum(allLinesComb[0], allLinesComb[1], this.getZerosAmount());

        System.out.println("Row lines: " + minLinesComb[0]);
        System.out.println("Col lines: " + minLinesComb[1]);

        this.coveredRows = minLinesComb[0].keySet();
        this.coveredCols = minLinesComb[1].keySet();

        return minLinesComb[0].size() + minLinesComb[1].size();
    }

    public void createAdditionalZeros() {

        List<Integer> notCovered = new ArrayList<>();
        List<int[]> notCoveredLoc = new ArrayList<>();
        List<int[]> coveredTwice = new ArrayList<>();
        for (int i = 0; i < this.dim; i++) {
            if (!this.coveredRows.contains(i)) {
                for (int j = 0; j < this.dim; j++) {
                    if (!this.coveredCols.contains(j)) {
                        notCovered.add(this.costMatrix[i][j]);
                        notCoveredLoc.add(new int[] {i, j});
                    }
                }
            } else {
                for (int j = 0; j < this.dim; j++) {
                    if (this.coveredCols.contains(j)) {
                        coveredTwice.add(new int[] {i, j});
                    }
                }
            }
        }

        int min = Integer.MAX_VALUE;
        for (Integer nCov : notCovered) {
            min = Math.min(min, nCov);
        }

        int finalMin = min;
        notCoveredLoc.forEach(nCovLoc -> this.costMatrix[nCovLoc[0]][nCovLoc[1]] -= finalMin);
        coveredTwice.forEach(covTw -> this.costMatrix[covTw[0]][covTw[1]] += finalMin);
    }

    public void reduce() {
        // Subtract row minima from each row
        for (Integer[] row : this.costMatrix) {
            int rowMin = Integer.MAX_VALUE;
            for (int element : row) {
                rowMin = Math.min(rowMin, element);
            }
            for (int i = 0; i < row.length; i++) {
                row[i] -= rowMin;
            }
        }

        // Subtract column minima from each column
        for (int i = 0; i < this.costMatrix[0].length; i++) {
            int columnMin = Integer.MAX_VALUE;
            for (Integer[] row : this.costMatrix) {
                columnMin = Math.min(columnMin, row[i]);
            }
            for (Integer[] row : this.costMatrix) {
                row[i] -= columnMin;
            }
        }
    }


    public List<Integer> findOptimalAssignment() {

        List[] possibleAssignments = new List[this.dim];
        List<Integer> usedY = new ArrayList<>();
        for (int i = 0; i < zeroLocations.size(); i++) {
            List<Integer> indeces = new ArrayList<>();
            indeces.add(zeroLocations.get(i).x);
            for(int j = i + 1; j < zeroLocations.size(); j++) {
                if (zeroLocations.get(i).y == zeroLocations.get(j).y) {
                    indeces.add(zeroLocations.get(j).x);
                }
            }
            if (!usedY.contains(zeroLocations.get(i).y)) {
                possibleAssignments[zeroLocations.get(i).y] = indeces;
                usedY.add(zeroLocations.get(i).y);
            }
        }

        int totalCost = Integer.MAX_VALUE;
        List<Integer> currentAssignment = null;
        List<List<Integer>> assignments = generateAssignments(possibleAssignments);
        for (int i = 0; i < assignments.size(); i++) {
            int cost = 0;
            for (int j = 0; j < assignments.get(i).size(); j++) {
                cost += this.costMatrixCp[assignments.get(i).get(j)][j];
            }

            if (cost < totalCost) {
                totalCost = cost;
                currentAssignment = assignments.get(i);
            }
        }

        this.totalCost = totalCost;
        return currentAssignment;
    }

    public List<List<Integer>> generateAssignments(List[] lists) {
        List<List<Integer>> assignments = new ArrayList<>();
        generateAssignments(lists, new ArrayList<>(), assignments);
        return assignments;
    }

    private void generateAssignments(List[] lists, List<Integer> currentAssignment, List<List<Integer>> assignments) {
        if (currentAssignment.size() == lists.length) {
            assignments.add(new ArrayList<>(currentAssignment));
            return;
        }

        for (int i = 0; i < lists[currentAssignment.size()].size(); i++) {
            int value = (int) lists[currentAssignment.size()].get(i);
            if (!currentAssignment.contains(value)) {
                currentAssignment.add(value);
                generateAssignments(lists, currentAssignment, assignments);
                currentAssignment.remove(currentAssignment.size() - 1);
            }
        }
    }
    public Map<Integer, Integer>[] getMapsForSum(Map<Integer, Integer> map1, Map<Integer, Integer> map2, int targetSum) {
        Map<Integer, Integer>[] result = new Map[2];
        result[0] = new HashMap<>();
        result[1] = new HashMap<>();

        Map<String, Integer> combinedMap = new HashMap<>();
        Map<String, Integer> combinedMapResultFinal = new HashMap<>();

        for (Map.Entry<Integer, Integer> entry : map1.entrySet()) {
            combinedMap.put("map1:" + entry.getKey(), entry.getValue());
        }

        for (Map.Entry<Integer, Integer> entry : map2.entrySet()) {
            combinedMap.put("map2:" + entry.getKey(), entry.getValue());
        }

        List<Map<String, Integer>> combs = CombinationFinder.findCombinations(combinedMap, targetSum);

        combinedMapResultFinal = this.filterCombs(combs).get(0);

        for (Map.Entry<String, Integer> entry : combinedMapResultFinal.entrySet()) {
            if (entry.getKey().contains("map1:")) {
                result[0].put(Integer.parseInt(entry.getKey().split("map1:")[1]), map1.get(Integer.parseInt(entry.getKey().split("map1:")[1])));
            } else if (entry.getKey().contains("map2:")) {
                result[1].put(Integer.parseInt(entry.getKey().split("map2:")[1]), map2.get(Integer.parseInt(entry.getKey().split("map2:")[1])));
            }
        }

        return result;
    }

    public List<Map<String, Integer>> filterCombs(List<Map<String, Integer>> combs) {
        int minSize = Integer.MAX_VALUE;

        for (Map<String, Integer> comb : combs) {
            minSize = Math.min(comb.size(), minSize);
        }

        int finalMinSize = minSize;
        List<Map<String, Integer>> filteredCombs = combs.stream()
                .filter(combination -> combination.size() == finalMinSize)
                .collect(Collectors.toList());

        if (filteredCombs.size() > 1) {
            boolean zeroNotCov = false;
            List<Map<String, Integer>> combMapNotZero = new ArrayList<>();
            for (Map<String, Integer> combMap : filteredCombs) {
                for (Point zero : this.zeroLocations) {
                    if (!combMap.containsKey("map1:" + zero.x) && !combMap.containsKey("map2:" + zero.y)) {
                        zeroNotCov = true;
                        combMapNotZero.add(combMap);
                        break;
                    }
                }
            }

            if (zeroNotCov) {
                for (Map<String, Integer> notZeroMap : combMapNotZero) {
                    filteredCombs.remove(notZeroMap);
                }
            }

            if (filteredCombs.size() == 0) {
                return this.filterCombs(combs.stream().filter(comb -> comb.size() > finalMinSize).collect(Collectors.toList()));
            }
        }

        return filteredCombs;
    }

    public void printGraph() {
        System.out.println("-----");
        for (Integer[] n: this.costMatrix) {
            System.out.println(Arrays.toString(n));
        }
        System.out.println("-----");
    }
}
