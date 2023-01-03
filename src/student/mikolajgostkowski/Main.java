package student.mikolajgostkowski;

import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        Graph g = new Graph();

        g.readCostMat("resources/testmatrix_3.txt");
        g.printGraph();
        List<Integer> assignment = g.hungarianMethod();
        System.out.println(assignment);
        HungarianMethodVisualizer.visualize(g.getCostMatrixCp(), assignment);
    }
}
