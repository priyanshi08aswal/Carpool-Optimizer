package com.edigest.myfirstproject.backend;
import java.util.*;

import java.util.*;

public class RouteOptimizer {

    public static List<RideRequest> optimizePickupOrder(String startLocation,List<RideRequest> requests,WeightedGraph graph) {

        if (requests.isEmpty()) {
            return Collections.emptyList();
        }

        List<RideRequest> bestOrder = null;
        double minDistance = Double.MAX_VALUE;

        List<RideRequest> perm = new ArrayList<>(requests);
        List<List<RideRequest>> allPerms = new ArrayList<>();
        permute(perm, 0, allPerms);

        for (List<RideRequest> order : allPerms) {
            double totalDist = 0.0;
            String prevLoc = startLocation;
            for (RideRequest req : order) {
                totalDist += DijkstraUtil.dijkstra(graph, prevLoc, req.getSource());
                prevLoc = req.getSource();
            }
            if (totalDist < minDistance) {
                minDistance = totalDist;
                bestOrder = new ArrayList<>(order);
            }
        }
        return bestOrder;
    }

    private static void permute(List<RideRequest> arr, int k, List<List<RideRequest>> result) {
        if (k == arr.size()) {
            result.add(new ArrayList<>(arr));
        } else {
            for (int i = k; i < arr.size(); i++) {
                Collections.swap(arr, i, k);
                permute(arr, k + 1, result);
                Collections.swap(arr, k, i);
            }
        }
    }
}
