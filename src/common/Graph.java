package common;

import java.util.Map;

public class Graph {
    private Node[] nodes;
    private double[][] adjMatrix;

    public Graph(int numNodes) {
        nodes = new Node[numNodes];
        adjMatrix = new double[numNodes][numNodes];

        for (int i = 0; i < numNodes; i++) {
            nodes[i] = new Node(i);
        }
    }

    // public static int numDisjointSets(int[][] adjMatrix) {
    // int n = adjMatrix.length;
    // boolean[] visited = new boolean[n];
    // int numSets = 0;

    // for (int i = 0; i < n; i++) {
    // if (!visited[i]) {
    // dfs(adjMatrix, visited, i);
    // numSets++;
    // }
    // }

    // return numSets;
    // }

    // private static void dfs(int[][] adjMatrix, boolean[] visited, int v) {
    // visited[v] = true;

    // for (int i = 0; i < adjMatrix.length; i++) {
    // if (adjMatrix[v][i] == 1 && !visited[i]) {
    // dfs(adjMatrix, visited, i);
    // }
    // }
    // }

    public void addBIDsToNode(Map<Integer, String> IndexToBid, int numNodes) {
        for (int i = 0; i < numNodes; i++) {
            nodes[i].addBID(IndexToBid.get(i));
        }
    }

    public void addEdge(int i, int j, double distance) {
        adjMatrix[i][j] = distance;
        adjMatrix[j][i] = distance;
    }

    public Node[] getNodes() {
        return nodes;
    }

    public double[][] getAdjMatrix() {
        return adjMatrix;
    }
}