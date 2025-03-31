package common;

import java.util.*;

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
        double[] distances = new double[numNodes];
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
            double[] adjRow = graph.getAdjMatrix()[currNode.getId()];
            for (int i = 0; i < numNodes; i++) {
                if (adjRow[i] > 0 && !visited[i]) {
                    double newDistance = distances[currNode.getId()] + adjRow[i];
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
                double[] adjRow = graph.getAdjMatrix()[curr.getId()];
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
        private double distance;

        public NodeDistancePair(Node node, double newDistance) {
            this.node = node;
            this.distance = newDistance;
        }

        public Node getNode() {
            return node;
        }

        public double getDistance() {
            return distance;
        }

        public int compareTo(NodeDistancePair other) {
            return Double.compare(distance, other.distance);
        }
    }
}