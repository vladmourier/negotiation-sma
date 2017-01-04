package model;

/**
 * Created by Vlad on 03/01/2017.
 */
public class Flight {
    private Destination from;
    private Destination to;

    public Flight(Destination from, Destination to) {
        this.from = from;
        this.to = to;
    }

    public boolean equals(Flight f) {
        return f.getFrom().equals(this.getFrom()) && f.getTo().equals(this.getTo());
    }

    public Destination getFrom() {
        return from;
    }

    public void setFrom(Destination from) {
        this.from = from;
    }

    public Destination getTo() {
        return to;
    }

    public void setTo(Destination to) {
        this.to = to;
    }
}
