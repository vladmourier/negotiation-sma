package model;

/**
 * Created by Vlad on 03/01/2017.
 */
public class Flight {
    Destination from;
    Destination to;

    public Flight(Destination from, Destination to) {
        this.from = from;
        this.to = to;
    }
}
