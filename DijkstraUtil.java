
package com.edigest.myfirstproject.backend;

import java.util.*;

public class DijkstraUtil {

    public static double dijkstra(WeightedGraph graph, String start, String end) {
        Map<String, Double> dist = new HashMap<>();
        PriorityQueue<Map.Entry<String, Double>> pq = new PriorityQueue<>(Map.Entry.comparingByValue());
        for (String v : graph.getVertices()) {
            dist.put(v, Double.POSITIVE_INFINITY);
        }
        dist.put(start, 0.0);
        pq.add(new AbstractMap.SimpleEntry<>(start, 0.0));

        while (!pq.isEmpty()) {
            Map.Entry<String, Double> curr = pq.poll();
            String u = curr.getKey();
            double d = curr.getValue();
            if (u.equals(end)) {
                return d;
            }
            if (d > dist.get(u)) {
                continue;
            }
            for (WeightedGraph.Edge edge : graph.getEdges(u)) {
                double alt = d + edge.weight;
                if (alt < dist.get(edge.dest)) {
                    dist.put(edge.dest, alt);
                    pq.add(new AbstractMap.SimpleEntry<>(edge.dest, alt));
                }
            }
        }
        return Double.POSITIVE_INFINITY;
    }
}
