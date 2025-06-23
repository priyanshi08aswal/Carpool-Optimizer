package com.edigest.myfirstproject.backend;

import java.util.*;

public class Main {

    static final List<RideRequest> rideRequests = new ArrayList<>();
    static final Object lock = new Object();

    public static final List<String> LOCATIONS = Arrays.asList(
            "L1", "L2", "L3", "L4", "L5", "L6", "L7", "L8", "L9", "L10", "L11", "L12"
    );

    public static String main(String id, String source, String destination, String detourInput, String seatsInput) {
        boolean isDriver = id.toLowerCase().startsWith("d");
        boolean isClient = id.toLowerCase().startsWith("c");

        if (!LOCATIONS.contains(source) || !LOCATIONS.contains(destination) || source.equals(destination)) {
            return "Invalid source or destination.";
        }

        WeightedGraph graph = createGraph();

        if (isClient) {

            System.out.println("Client ID: " + id);
            System.out.println("source location: " + source);
            System.out.println("destination location: " + destination);
            System.out.println("detour: " + detourInput);

            double detour = detourInput.isEmpty() ? Double.MAX_VALUE : Double.parseDouble(detourInput);
            synchronized (lock) {
                rideRequests.add(new RideRequest(id, source, destination, detour));
            }
            return "Ride request accepted.";
        }

        if (isDriver) {

            System.out.println("Driver ID: " + id);
            System.out.println("source location: " + source);
            System.out.println("destination location: " + destination);
            System.out.println("seats: " + seatsInput);

            int seats;
            try {
                seats = Integer.parseInt(seatsInput);
            } catch (NumberFormatException e) {
                return "Invalid seat number.";
            }

            RideOffer offer = new RideOffer(id, source, destination, seats);

            // Wait 15 seconds for clients
            try {
                Thread.sleep(15000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return "Driver wait interrupted.";
            }

            return assignClientsToDriver(offer, graph);
        }

        return "Invalid ID format.";
    }

    private static String assignClientsToDriver(RideOffer driver, WeightedGraph graph) {
        StringBuilder result = new StringBuilder();

        List<RideRequest> assignedClients;
        synchronized (lock) {
            if (rideRequests.isEmpty()) {
                return "No clients available after 15 seconds.";
            }

            List<RideRequest> assigned = new ArrayList<>();
            for (RideRequest req : rideRequests) {
                if (driver.getAvailableSeats() > 0 && isDetourAcceptable(driver, req, graph)) {
                    assigned.add(req);
                    driver.setAvailableSeats(driver.getAvailableSeats() - 1);
                }
            }

            if (assigned.isEmpty()) {
                return "No clients could be assigned to the driver.";
            }
            assignedClients = assigned;
            rideRequests.clear(); // reset after use
        }

        result.append("Cluster 1 assigned to driver ").append(driver.getDriverId()).append(":\n");
        result.append("  🚘 Assigned Clients:\n");

        for (RideRequest req : assignedClients) {
            result.append("    - ").append(req.getUserId()).append(" (")
                    .append(req.getSource()).append(" → ").append(req.getDestination()).append(")\n");
        }

        List<RideRequest> optimalOrder = (assignedClients.size() <= 5)
                ? RouteOptimizer.optimizePickupOrder(driver.getSource(), assignedClients, graph)
                : GeneticRouteOptimizer.optimizePickupOrder(driver.getSource(), assignedClients, graph);

        result.append("  🛣️ Optimized Pickup Path:\n");

        double totalDistance = 0.0;
        String prev = driver.getSource();
        for (RideRequest req : optimalOrder) {
            double leg = DijkstraUtil.dijkstra(graph, prev, req.getSource());
            result.append("    ").append(prev).append(" -> ").append(req.getSource()).append(" : ")
                    .append(leg).append(" units\n");
            totalDistance += leg;
            prev = req.getSource();
        }

        result.append("  Total pickup path distance: ").append(totalDistance).append(" units\n");

        return result.toString();
    }

    private static boolean isDetourAcceptable(RideOffer offer, RideRequest req, WeightedGraph graph) {
        double direct = DijkstraUtil.dijkstra(graph, offer.getSource(), offer.getDestination());
        double withReq = DijkstraUtil.dijkstra(graph, offer.getSource(), req.getSource()) +
                DijkstraUtil.dijkstra(graph, req.getSource(), req.getDestination()) +
                DijkstraUtil.dijkstra(graph, req.getDestination(), offer.getDestination());
        return withReq - direct <= req.getMaxDetourTime();
    }

    private static WeightedGraph createGraph() {
        WeightedGraph graph = new WeightedGraph();
        for (String loc : LOCATIONS) graph.addVertex(loc);

        graph.addEdge("L1", "L2", 2.0);
        graph.addEdge("L2", "L3", 3.5);
        graph.addEdge("L3", "L4", 2.2);
        graph.addEdge("L4", "L5", 1.8);
        graph.addEdge("L5", "L6", 2.7);
        graph.addEdge("L6", "L7", 2.1);
        graph.addEdge("L7", "L8", 1.5);
        graph.addEdge("L8", "L9", 2.9);
        graph.addEdge("L9", "L10", 3.0);
        graph.addEdge("L10", "L11", 2.2);
        graph.addEdge("L11", "L12", 1.7);
        graph.addEdge("L12", "L1", 4.0);

        return graph;
    }
}
