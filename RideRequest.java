package com.edigest.myfirstproject.backend;
public class RideRequest {
    private String userId;
    private String source;
    private String destination;
    private double maxDetourTime; // in units (e.g., minutes or distance)

    public RideRequest(String userId, String source, String destination, double maxDetourTime) {
        this.userId = userId;
        this.source = source;
        this.destination = destination;
        this.maxDetourTime = maxDetourTime;
    }

    public RideRequest(String userId, String source, String destination) {
        this(userId, source, destination, Double.MAX_VALUE);
    }

    // Getters and setters
    public String getUserId() { return userId; }
    public String getSource() { return source; }
    public String getDestination() { return destination; }
    public double getMaxDetourTime() { return maxDetourTime; }
    public void setMaxDetourTime(double maxDetourTime) { this.maxDetourTime = maxDetourTime; }

    @Override
    public String toString() {
        return "RideRequest{userId='" + userId + "', source='" + source + "', destination='" + destination + "'}";
    }

}