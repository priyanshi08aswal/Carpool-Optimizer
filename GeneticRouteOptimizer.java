package com.edigest.myfirstproject.backend;

import java.util.*;

public class GeneticRouteOptimizer {
    public static List<RideRequest> optimizePickupOrder(String startLocation, List<RideRequest> requests, WeightedGraph graph) {
        // Parameters
        int popSize = 50, generations = 100;
        Random rand = new Random();

        // Initialize rides
        List<List<RideRequest>> rides = new ArrayList<>();
        for (int i = 0; i < popSize; i++) {
            List<RideRequest> perm = new ArrayList<>(requests);
            Collections.shuffle(perm, rand);
            rides.add(perm);
        }

        List<RideRequest> bestOrder = null;
        double bestFitness = Double.MAX_VALUE;

        for (int gen = 0; gen < generations; gen++) {
            // Evaluate fitness
            rides.sort(Comparator.comparingDouble(order -> totalDistance(startLocation, order, graph)));
            if (totalDistance(startLocation, rides.get(0), graph) < bestFitness) {
                bestFitness = totalDistance(startLocation, rides.get(0), graph);
                bestOrder = new ArrayList<>(rides.get(0));
            }
            // Selection: keep top 50%
            List<List<RideRequest>> newPop = new ArrayList<>(rides.subList(0, popSize / 2));
            // Crossover and mutation
            while (newPop.size() < popSize) {
                List<RideRequest> parent1 = new ArrayList<>(newPop.get(rand.nextInt(newPop.size())));
                List<RideRequest> parent2 = new ArrayList<>(newPop.get(rand.nextInt(newPop.size())));
                List<RideRequest> child = crossover(parent1, parent2, rand);
                if (rand.nextDouble() < 0.2) mutate(child, rand);
                newPop.add(child);
            }
            rides = newPop;
        }
        return bestOrder;
    }

    private static double totalDistance(String start, List<RideRequest> order, WeightedGraph graph) {
        double dist = 0.0;
        String prev = start;
        for (RideRequest req : order) {
            dist += DijkstraUtil.dijkstra(graph, prev, req.getSource());
            prev = req.getSource();
        }
        return dist;
    }

    private static List<RideRequest> crossover(List<RideRequest> p1, List<RideRequest> p2, Random rand) {
        Set<RideRequest> childSet = new LinkedHashSet<>();
        int cut = rand.nextInt(p1.size());
        childSet.addAll(p1.subList(0, cut));
        for (RideRequest r : p2) if (!childSet.contains(r)) childSet.add(r);
        return new ArrayList<>(childSet);
    }

    private static void mutate(List<RideRequest> order, Random rand) {
        int i = rand.nextInt(order.size()), j = rand.nextInt(order.size());
        Collections.swap(order, i, j);
    }
}
