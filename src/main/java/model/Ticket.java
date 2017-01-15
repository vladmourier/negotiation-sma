package model;

import java.util.Date;
import java.util.HashMap;

/**
 * Created by Vlad on 03/01/2017.
 */
public class Ticket {

    public static HashMap<Integer, Ticket> tickets = new HashMap<Integer, Ticket>();

    int id;
    Flight flight;
    int price;
    Date date;

    public Ticket(int id, Destination from, Destination to, int price, Date date) {
        this.flight = new Flight(from, to);
        this.id = id;
        this.price = price;
        this.date = date;
        tickets.put(id, this);
    }

    public Ticket(int id, Flight flight, int price, Date date) {
        this.id = id;
        this.flight = flight;
        this.price = price;
        this.date = date;
        tickets.put(id, this);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Flight getFlight() {
        return flight;
    }

    public void setFlight(Flight flight) {
        this.flight = flight;
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
  
    @Override
    public String toString() {
        return "Ticket{" +
                "id=" + id +
                ", flight=" + flight +
                ", price=" + price +
                ", date=" + date +
                '}';
    }
}
