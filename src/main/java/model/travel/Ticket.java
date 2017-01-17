package model.travel;

import java.util.GregorianCalendar;
import java.util.HashMap;

/**
 * Created by Vlad on 03/01/2017.
 */
public class Ticket {

    private static int lastTicketNumber = -1;
    public static HashMap<Integer, Ticket> tickets = new HashMap<Integer, Ticket>();

    private int id;
    private Flight flight;
    private int price;
    private GregorianCalendar date;

    public Ticket(UntrackedFlight flight, int price, GregorianCalendar date) {
        this.flight = new Flight(flight);
        this.id = lastTicketNumber +1;
        this.price = price;
        this.date = date;
        tickets.put(id, this);
        lastTicketNumber++;
    }

    public Ticket(Flight flight, int price, GregorianCalendar date) {
        this.id = lastTicketNumber+1;
        this.flight = flight;
        this.price = price;
        this.date = date;
        tickets.put(id, this);
        lastTicketNumber++;
    }

    public boolean compare(Ticket t) {
        return this.getFlight().equals(t.getFlight()) && this.getDate().equals(t.getDate());
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

    public GregorianCalendar getDate() {
        return date;
    }

    public void setDate(GregorianCalendar date) {
        this.date = date;
    }
}
