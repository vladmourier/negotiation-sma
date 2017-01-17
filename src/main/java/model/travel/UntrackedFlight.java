package model.travel;

import java.util.ArrayList;

/**
 * Created by Vlad on 17/01/2017.
 */
public class UntrackedFlight {

    static ArrayList<UntrackedFlight> untrackedFlights = new ArrayList<>();
    private Destination from;
    private Destination to;

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

    public UntrackedFlight(Destination from, Destination to) {

        this.from = from;
        this.to = to;

        untrackedFlights.add(this);
    }
}
