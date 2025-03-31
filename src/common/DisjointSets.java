package common;

import java.util.HashSet;

public class DisjointSets {
    static int[] parent = new int[1_000_000];

    // Function to find the topmost parent of vertex a
    static int root(int a) {
        if (a == parent[a]) {
            return a;
        }
        return parent[a] = root(parent[a]);
    }

    // Function to connect the component
    // having vertex a with the component
    // having vertex b
    static void connect(int a, int b) {
        a = root(a);
        b = root(b);

        if (a != b) {
            parent[b] = a;
        }
    }

    // Function to find unique topmost parents
    static void connectedComponents(int n, double[][] ds) {
        HashSet<Integer> s = new HashSet<Integer>();

        // Traverse all vertices
        for (int i = 0; i < n; i++) {
            // Insert all topmost vertices obtained
            s.add(root(i));
        }

        // Print count of connected components
        System.out.println(s.size());
    }

    // Function to print answer
    public static void printAnswer(int n, double[][] ds) {
        // Setting parent to itself
        for (int i = 0; i < n; i++) {
            parent[i] = i;
        }

        // Traverse all edges
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                if (ds[i][j] > 0) {
                    connect(i, j);
                }
            }
        }

        // Print answer
        connectedComponents(n, ds);
    }
}