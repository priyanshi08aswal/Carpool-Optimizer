
package com.edigest.myfirstproject.backend;
import java.util.*;

import java.util.*;

public class WeightedGraph {

    public static class Edge {

        String dest;
        double weight;

        public Edge(String dest, double weight) {
            this.dest = dest;
            this.weight = weight;
        }
    }

    private Map<String, List<Edge>> adj = new HashMap<>();

    public void addVertex(String v) {
        adj.putIfAbsent(v, new ArrayList<>());
    }

    public void addEdge(String src, String dest, double weight) {
        adj.putIfAbsent(src, new ArrayList<>());
        adj.get(src).add(new Edge(dest, weight));
        adj.putIfAbsent(dest, new ArrayList<>());
        adj.get(dest).add(new Edge(src, weight));
    }

    public List<Edge> getEdges(String v) {
        return adj.getOrDefault(v, Collections.emptyList());
    }

    public Set<String> getVertices() {
        return adj.keySet();
    }
}
