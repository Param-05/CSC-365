import java.util.*;

class Graph {
    private Node[] nodes;
    private int[][] adjMatrix;

    public Graph(int numNodes) {
        nodes = new Node[numNodes];
        adjMatrix = new int[numNodes][numNodes];

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

    public void addEdge(int i, int j, int weight) {
        adjMatrix[i][j] = weight;
        adjMatrix[j][i] = weight;
    }

    public Node[] getNodes() {
        return nodes;
    }

    public int[][] getAdjMatrix() {
        return adjMatrix;
    }
}

class Node {
    private int id;
    private String bID;

    public Node(int id) {
        this.id = id;
    }

    public void addBID(String bID) {
        this.bID = bID;
    }

    public int getId() {
        return id;
    }

    public String getBID() {
        return bID;
    }

    @Override
    public String toString() {
        return bID;
    }
}

// class DisjointSets {
// private int[] parent;
// private int[] rank;

// public DisjointSets(int size) {
// parent = new int[size];
// rank = new int[size];
// for (int i = 0; i < size; i++) {
// parent[i] = i;
// rank[i] = 0;
// }
// }

// public int find(int x) {
// if (parent[x] != x) {
// parent[x] = find(parent[x]);
// }
// return parent[x];
// }

// public void union(int x, int y) {
// int xRoot = find(x);
// int yRoot = find(y);
// if (xRoot == yRoot) {
// return;
// }
// if (rank[xRoot] < rank[yRoot]) {
// parent[xRoot] = yRoot;
// } else if (rank[xRoot] > rank[yRoot]) {
// parent[yRoot] = xRoot;
// } else {
// parent[yRoot] = xRoot;
// rank[xRoot]++;
// }
// }

// public static int numDisjointSets(int[][] adjMatrix) {
// int n = adjMatrix.length;
// DisjointSets ds = new DisjointSets(n);

// for (int i = 0; i < n; i++) {
// for (int j = 0; j < n; j++) {
// if (adjMatrix[i][j] > 1) {
// ds.union(i, j);
// }
// }
// }

// Set<Integer> roots = new HashSet<>();
// for (int i = 0; i < n; i++) {
// roots.add(ds.find(i));
// }

// return roots.size();
// }
// // public static int numDisjointSets(int[][] adjMatrix) {
// // int[] parent = new int[adjMatrix.length];
// // DisjointSets ds = new DisjointSets(n);
// // int count = adjMatrix.length;
// // for (int i = 0; i < adjMatrix.length; i++) {
// // parent[i] = i;
// // }
// // for (int i = 0; i < adjMatrix.length; i++) {
// // for (int j = i+1; j < adjMatrix.length; j++) {
// // if (adjMatrix[i][j] > 0 && ds.findParent(parent, i) !=
// ds.findParent(parent,
// // j)) {
// // ds.union(parent, i, j);
// // count--;
// // }
// // }
// // }
// // return count;
// // }

// }

public class D2 {
    private Graph graph;
    // private DisjointSets disjointSets;

    public D2(Graph graph) {
        this.graph = graph;
    }

    public List<Node> shortestPath(int srcIndex, int destIndex) {
        Node src = graph.getNodes()[srcIndex];
        Node dest = graph.getNodes()[destIndex];
        int numNodes = graph.getNodes().length;

        // Initialize distances and visited array
        int[] distances = new int[numNodes];
        Arrays.fill(distances, Integer.MAX_VALUE);
        distances[src.getId()] = 0;
        boolean[] visited = new boolean[numNodes];

        // Initialize priority queue with source node
        PriorityQueue<NodeDistancePair> pq = new PriorityQueue<NodeDistancePair>();
        pq.offer(new NodeDistancePair(src, 0));

        // Traverse graph using Dijkstra's algorithm
        while (!pq.isEmpty()) {
            // Get node with shortest distance from priority queue
            NodeDistancePair currPair = pq.poll();
            Node currNode = currPair.node;

            // Mark node as visited
            visited[currNode.getId()] = true;

            // Update distances for adjacent nodes
            int[] adjRow = graph.getAdjMatrix()[currNode.getId()];
            for (int i = 0; i < numNodes; i++) {
                if (adjRow[i] > 0 && !visited[i]) {
                    int newDistance = distances[currNode.getId()] + adjRow[i];
                    if (newDistance < distances[i]) {
                        distances[i] = newDistance;
                        pq.offer(new NodeDistancePair(graph.getNodes()[i], newDistance));
                    }
                }
            }
        }

        // Construct path from source to destination
        if (distances[dest.getId()] == Integer.MAX_VALUE) {
            // No path from source to destination
            return new ArrayList<Node>();
        } else {
            List<Node> path = new ArrayList<Node>();
            Node curr = dest;
            while (curr != src) {
                path.add(curr);
                int[] adjRow = graph.getAdjMatrix()[curr.getId()];
                for (int i = 0; i < numNodes; i++) {
                    if (adjRow[i] > 0 && distances[i] == distances[curr.getId()] - adjRow[i]) {
                        curr = graph.getNodes()[i];
                        break;
                    }
                }
            }
            path.add(src);
            Collections.reverse(path);

            return path;
        }
    }

    private static class NodeDistancePair implements Comparable<NodeDistancePair> {
        private Node node;
        private int distance;

        public NodeDistancePair(Node node, int distance) {
            this.node = node;
            this.distance = distance;
        }

        public Node getNode() {
            return node;
        }

        public int getDistance() {
            return distance;
        }

        public int compareTo(NodeDistancePair other) {
            return Integer.compare(distance, other.distance);
        }
    }

    class DisjointSet {

        static int[] parent = new int[1000000];

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
        static void connectedComponents(int n, int[][] graph) {
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
        static void printAnswer(int n, int[][] graph) {
            // Setting parent to itself
            for (int i = 0; i < n; i++) {
                parent[i] = i;
            }

            // Traverse all edges
            for (int i = 0; i < n; i++) {
                for (int j = i + 1; j < n; j++) {
                    if (graph[i][j] > 0) {
                        connect(i, j);
                    }
                }
            }

            // Print answer
            connectedComponents(n, graph);
        }

        // Driver Code
    }

    public static void main(String[] args) {
        // Create graph with 5 nodes
        Graph graph = new Graph(5);

        // Add BIDs to nodes
        Map<Integer, String> indexToBid = new HashMap<>();
        indexToBid.put(0, "Target");
        indexToBid.put(1, "Walmart");
        indexToBid.put(2, "Some business");
        indexToBid.put(3, "Aldi");
        indexToBid.put(4, "Idk some other business");
        graph.addBIDsToNode(indexToBid, 5);

        // Add edges with weights
        // graph.addEdge(0, 1, 4);
        // graph.addEdge(0, 2, 1);
        // graph.addEdge(1, 2, 2);
        // graph.addEdge(1, 3, 5);
        // graph.addEdge(2, 3, 1);
        // graph.addEdge(2, 4, 3);
        // graph.addEdge(3, 4, 1);

        graph.addEdge(0, 1, 4);
        graph.addEdge(3, 4, 1);
        graph.addEdge(1, 2, 1);
        // graph.addEdge(1, 4, 3);
        // graph.addEdge(2, 3, 2);
        // graph.addEdge(2, 4, 1);

        DisjointSet.printAnswer(5, graph.getAdjMatrix());

        // Create Dijkstra object and find shortest path between nodes 0 and 4
        D2 dijkstra = new D2(graph);
        List<Node> path = dijkstra.shortestPath(0, 2);

        // Print path
        System.out.print("Shortest path: ");
        for (Node node : path) {
            System.out.print(node.getId() + ":(" + node.getBID() + ") -> ");
        }
    }

}
