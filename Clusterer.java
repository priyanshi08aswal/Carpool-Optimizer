package com.edigest.myfirstproject.backend;
import java.util.*;

public class Clusterer {

    public static class Cluster {

        public List<RideRequest> members = new ArrayList<>();
        public RideRequest centroidRequest;

        public Cluster(RideRequest centroidRequest) {
            this.centroidRequest = centroidRequest;
        }
    }

    // K-medoids-like clustering using graph-based distances
    public static List<Cluster> clusterUsersByGraph(List<RideRequest> requests,WeightedGraph graph,int k) {
        if (requests.size() < k) {
            System.out.println("Not enough users to form " + k + " clusters.");
            return Collections.emptyList();
        }

        List<Cluster> clusters = new ArrayList<>();
        Random rand = new Random();
        Set<Integer> used = new HashSet<>();
        while (clusters.size() < k) {
            int idx = rand.nextInt(requests.size());
            if (!used.contains(idx)) {
                clusters.add(new Cluster(requests.get(idx)));
                used.add(idx);
            }
        }

        for (int iteration = 0; iteration < 50; iteration++) {
            for (Cluster cluster : clusters) {
                cluster.members.clear();
            }

            for (RideRequest req : requests) {
                int bestCluster = 0;
                double minDist = graphDistance(graph, req, clusters.get(0).centroidRequest);
                for (int i = 1; i < clusters.size(); i++) {
                    double dist = graphDistance(graph, req, clusters.get(i).centroidRequest);
                    if (dist < minDist) {
                        minDist = dist;
                        bestCluster = i;
                    }
                }
                clusters.get(bestCluster).members.add(req);
            }

            boolean changed = false;
            for (Cluster cluster : clusters) {
                RideRequest newCentroid = findMedoid(cluster.members, graph);
                if (newCentroid != null && newCentroid != cluster.centroidRequest) {
                    cluster.centroidRequest = newCentroid;
                    changed = true;
                }
            }
            if (!changed) {
                break;
            }
        }

        // Print clusters for debugging
        for (int i = 0; i < clusters.size(); i++) {
            System.out.print("Cluster " + (i + 1) + ": ");
            for (RideRequest member : clusters.get(i).members) {
                System.out.print(member.getUserId() + " ");
            }
            System.out.println();
        }

        return clusters;
    }

    // Graph-based "distance" between two ride requests: sum of source-to-source and dest-to-dest shortest path
    private static double graphDistance(WeightedGraph graph, RideRequest r1, RideRequest r2) {
        double srcDist = DijkstraUtil.dijkstra(graph, r1.getSource(), r2.getSource());
        double dstDist = DijkstraUtil.dijkstra(graph, r1.getDestination(), r2.getDestination());
        return srcDist + dstDist;
    }

    // Find the medoid (the member with minimal total distance to others in the cluster)
    private static RideRequest findMedoid(List<RideRequest> members, WeightedGraph graph) {
        if (members.isEmpty()) {
            return null;
        }
        RideRequest best = members.get(0);
        double minSum = Double.POSITIVE_INFINITY;
        for (RideRequest candidate : members) {
            double sum = 0.0;
            for (RideRequest other : members) {
                if (candidate != other) {
                    sum += graphDistance(graph, candidate, other);
                }
            }
            if (sum < minSum) {
                minSum = sum;
                best = candidate;
            }
        }
        return best;
    }
}
