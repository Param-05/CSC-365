import java.io.*;
import java.util.*;

public class TestAdjMatrix {
    private static Map<String, int[][]> cache = new HashMap<>();

    public void saveAdjacencyMatrix(int[][] adjMatrix, String fileName) {
        try {
            FileOutputStream fileOut = new FileOutputStream(fileName);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(adjMatrix);
            out.close();
            fileOut.close();
            System.out.println("Adjacency matrix saved to " + fileName);
            cache.put(fileName, adjMatrix); // add to cache
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int[][] loadAdjacencyMatrix(String fileName) {
        if (cache.containsKey(fileName)) {
            System.out.println("Adjacency matrix loaded from cache for " + fileName);
            return cache.get(fileName);
        }

        int[][] adjMatrix = null;
        try {
            FileInputStream fileIn = new FileInputStream(fileName);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            adjMatrix = (int[][]) in.readObject();
            in.close();
            fileIn.close();
            System.out.println("Adjacency matrix loaded from " + fileName);
            cache.put(fileName, adjMatrix); // add to cache
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return adjMatrix;
    }

    public static void main(String[] args) {
        // Create an example adjacency matrix
        int[][] matrix = {
                {0, 1, 0, 1},
                {1, 0, 1, 0},
                {0, 1, 0, 1},
                {1, 0, 1, 0}
        };

        // Save the matrix to a file
        String fileName = "adjacency_matrix.txt";
        new TestAdjMatrix().saveAdjacencyMatrix(matrix, fileName);
        System.out.println("Matrix saved to file: " + fileName);

        // Load the matrix from the file
        int[][] loadedMatrix;
        TestAdjMatrix test = new TestAdjMatrix();
        loadedMatrix = test.loadAdjacencyMatrix(fileName);
        System.out.println("Matrix loaded from file: ");
        for (int[] row : loadedMatrix) {
            System.out.println(Arrays.toString(row));
        }

        // Load the matrix from cache
        loadedMatrix = test.loadAdjacencyMatrix(fileName);
        System.out.println("Matrix loaded from cache: ");
        for (int[] row : loadedMatrix) {
            System.out.println(Arrays.toString(row));
        }
    }

}
