package model;

import java.util.Date;

/**
 * Created by Vlad on 03/01/2017.
 */
public class Ticket {
    Flight f;
    int price;
    Date date;

    public Ticket(Destination from, Destination to, int price, Date date) {
        this.f = new Flight(from, to);
        this.price = price;
        this.date = date;
    }

    public Ticket(Flight f, int price, Date date) {
        this.f = f;
        this.price = price;
        this.date = date;
    }
}
