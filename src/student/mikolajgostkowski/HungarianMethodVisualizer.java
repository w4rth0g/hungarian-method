package student.mikolajgostkowski;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;
import java.io.IOException;
import java.util.List;

public class HungarianMethodVisualizer extends JPanel {
    // 2D array to store the input graph
    private Integer[][] graph;
    // Array to store the matching
    private List<Integer> matching;
    // The number of vertices on one side of the bipartition
    private int n;
    // The number of vertices on the other side of the bipartition
    private int m;
    // The radius of the vertices in the graph
    private int vertexRadius = 20;
    // The spacing between the vertices in the graph
    private int vertexSpacing = 100;
    // The color to use to highlight the matching edges
    private Color matchColor = Color.RED;
    // The font to use for displaying the weights of the edges
    private Font weightFont = new Font(Font.SANS_SERIF, Font.BOLD, 12);

    public HungarianMethodVisualizer(Integer[][] graph, List<Integer> matching) {
        // Initialize the instance variables
        this.graph = graph;
        this.matching = matching;
        n = graph.length;
        m = graph[0].length;
    }

    @Override
    public Dimension getPreferredSize() {
        // Set the preferred size of the panel to be the size of the graph
        return new Dimension((m + 1) * vertexSpacing, (n + 2) * vertexSpacing);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw the edges in the matching
        for (int i = 0; i < n; i++) {
            int x = vertexRadius + i * vertexSpacing;
            int y = vertexRadius;
            g.fillOval(x - vertexRadius, y - vertexRadius, 2 * vertexRadius, 2 * vertexRadius);
            for (int j = 0; j < m; j++) {
                int x2 = vertexRadius + j * vertexSpacing;
                int y2 = vertexRadius + (n - 1) * vertexSpacing + vertexRadius;
                Graphics2D g2 = (Graphics2D) g;
                if (matching.get(j) == i) {
                    g2.setColor(matchColor);
                    g2.setStroke(new BasicStroke(3));
                } else {
                    g2.setColor(Color.BLACK);
                    g2.setStroke(new BasicStroke(1));
                }
                g2.draw(new Line2D.Float(x, y, x2, y2));
                g.setColor(Color.BLACK);
            }
        }

        // Draw the vertices on one side of the bipartition
        for (int i = 0; i < n; i++) {
            int x = vertexRadius + i * vertexSpacing;
            int y = vertexRadius;
            g.fillOval(x - vertexRadius, y - vertexRadius, 2 * vertexRadius, 2 * vertexRadius);
        }

        // Draw the vertices on the other side of the bipartition
        for (int j = 0; j < m; j++) {
            int x = vertexRadius + j * vertexSpacing;
            int y = vertexRadius + (n - 1) * vertexSpacing + vertexRadius;
            g.fillOval(x - vertexRadius, y - vertexRadius, 2 * vertexRadius, 2 * vertexRadius);
        }

        int totalCost = 0;
        StringBuilder equation = new StringBuilder();
        for (int i = 0; i < m; i++) {
            if (matching.get(i) != -1) {
                double weight = graph[matching.get(i)][i];
                totalCost += weight;
                equation.append(weight);
                if (i < m - 1) {
                    equation.append(" + ");
                }
            }
        }
        equation.append(" = ").append(totalCost);

        g.setFont(new Font("Arial", Font.PLAIN, 16));
        g.drawString(equation.toString(), vertexRadius, vertexRadius + n * vertexSpacing + vertexRadius - 30);
    }

    public static void visualize(Integer[][] costMatrix, List<Integer> assignment) throws IOException {

        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Add the panel to the frame
        HungarianMethodVisualizer panel = new HungarianMethodVisualizer(costMatrix, assignment);
        frame.add(panel);
        frame.pack();
        frame.setVisible(true);
    }
}
