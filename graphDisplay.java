// package application;
import javax.swing.*;
import java.awt.*;

public class graphDisplay extends JFrame {
    private Point[] vertices;
    private int[][] adjacencyMatrix;

    public graphDisplay(Point[] vertices, int[][] adjacencyMatrix) {
        this.vertices = vertices;
        this.adjacencyMatrix = adjacencyMatrix;
        setTitle("Graph Display");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 1000);
        setVisible(true);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;

        // Draw edges
        g2d.setColor(Color.BLACK);
        for (int i = 0; i < adjacencyMatrix.length; i++) {
            for (int j = i + 1; j < adjacencyMatrix.length; j++) {
                if (adjacencyMatrix[i][j] != 0) {
                    Point source = vertices[i];
                    Point destination = vertices[j];
                    g2d.drawLine((int) source.x, (int) source.y, (int) destination.x, (int) destination.y);
                }
            }
        }

        // Draw vertices
        g2d.setColor(Color.RED);
        for (Point point : vertices) {
            g2d.fillOval((int) point.x - 5, (int) point.y - 5, 10, 10);
        }
    }

    public static void main(String[] args) {
        // Create a sample graph with points and adjacency matrix
        Point p1 = new Point(50, 50);
        Point p2 = new Point(150, 400);
        Point p3 = new Point(250, 250);
        Point p4 = new Point(350, 150);
        Point[] points = {p1, p2, p3, p4};
        int[][] adjacencyMatrix = {
                {0, 3, 5, 4},
                {3, 0, 3, 0},
                {5, 3, 0, 2},
                {4, 0, 2, 0}
        };

        // Create and show the graph display window
        graphDisplay graphDisplay = new graphDisplay(points, adjacencyMatrix);
    }

    private static class Point {
        double x;
        double y;

        public Point(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }
}
