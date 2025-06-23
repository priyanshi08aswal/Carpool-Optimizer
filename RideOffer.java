package com.edigest.myfirstproject.backend;

public class RideOffer {
    private String driverId;
    private String source;
    private String destination;
    private int availableSeats;

    public RideOffer(String driverId, String source, String destination, int availableSeats) {
        this.driverId = driverId;
        this.source = source;
        this.destination = destination;
        this.availableSeats = availableSeats;
    }

    public String getDriverId() {
        return driverId;
    }

    public String getSource() {
        return source;
    }

    public String getDestination() {
        return destination;
    }

    public int getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(int seats) {
        this.availableSeats = seats;
    }

    @Override
    public String toString() {
        return "RideOffer{driverId='" + driverId + "', source='" + source + "', destination='" + destination + "', availableSeats=" + availableSeats + "}";
    }
}
