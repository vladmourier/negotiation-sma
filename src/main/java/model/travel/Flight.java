package model.travel;

/**
 * Created by Vlad on 03/01/2017.
 */
public class Flight {
    public static int lastFlightId = -1;

    private int id;
    private UntrackedFlight untrackedFlight;


    public Flight(UntrackedFlight untrackedFlight) {
        this.untrackedFlight = untrackedFlight;
        id = lastFlightId+1;
        lastFlightId++;
    }

    public Destination getFrom() {
        return untrackedFlight.getFrom();
    }

    public Destination getTo() {
        return untrackedFlight.getTo();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Flight flight = (Flight) o;
        if (this.untrackedFlight.getFrom() != flight.getFrom()) return false;
        return this.untrackedFlight.getTo() == flight.getTo();
    }

    @Override
    public String toString() {
        return "Flight{" +
                "id=" + id +
                ", from=" + untrackedFlight.getFrom() +
                ", to=" + untrackedFlight.getTo() +
                '}';
    }

    public boolean equals(Flight f) {
        return f.getFrom().equals(this.getFrom()) && f.getTo().equals(this.getTo()) && id == f.getId();
    }

    public int getId() {
        return id;
    }

    public void setFrom(Destination from) {
        this.untrackedFlight.setFrom(from);
    }

    public void setTo(Destination to) {
        this.untrackedFlight.setTo(to);
    }

    public UntrackedFlight getUntrackedFlight() {
        return untrackedFlight;
    }
}
