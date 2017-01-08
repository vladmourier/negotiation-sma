package model;

import java.util.HashMap;

/**
 * Created by Vlad on 03/01/2017.
 */
public class Flight {
    /**
     * List of flights
     */
    public static HashMap<Integer, Flight> flights = new HashMap<Integer, Flight>();

    Destination from;
    Destination to;

    public Flight(Destination from, Destination to) {
        this.from = from;
        this.to = to;
        flights.put(hashCode(), this);
    }

    public Destination getFrom() {
        return from;
    }

    public Destination getTo() {
        return to;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Flight flight = (Flight) o;
        if (from != flight.from) return false;
        return to == flight.to;
    }

    @Override
    public int hashCode() {
        int result = from != null ? from.hashCode() : 0;
        result = 31 * result + (to != null ? to.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Flight{" +
                "from=" + from +
                ", to=" + to +
                '}';
    }
}
