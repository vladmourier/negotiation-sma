package model;

import java.util.Date;
import java.util.HashMap;

/**
 * Created by Vlad on 03/01/2017.
 */
public class Ticket {

    public static HashMap<Integer, Ticket> tickets = new HashMap<Integer, Ticket>();


    int id;
    Flight f;
    int price;
    Date date;

    public Ticket(int id, Destination from, Destination to, int price, Date date) {
        this.f = new Flight(from, to);
        this.price = price;
        this.date = date;
    }

    public Ticket(int id, Flight f, int price, Date date) {
        this.f = f;
        this.price = price;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Flight getF() {
        return f;
    }

    public void setF(Flight f) {
        this.f = f;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean compare(Ticket t) {
        return this.getF().equals(t.getF()) && this.getDate().equals(t.getDate());
    }
}
