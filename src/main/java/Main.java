import model.Agent;
import model.Client;
import model.Supplier;
import model.travel.Destination;
import model.travel.Ticket;
import model.travel.UntrackedFlight;

import java.util.GregorianCalendar;
import java.util.Random;

public class Main {
    public static void main(String[] args) {

        UntrackedFlight f1 = new UntrackedFlight(Destination.LYON, Destination.PARIS);


        Client c = new Client(Agent.ANSI_GREEN + "BUYER" + Agent.ANSI_RESET, 100, 0.9, 1.2, 0);
        c.addWantedTicket(new Ticket(f1, 80, new GregorianCalendar(2017, GregorianCalendar.FEBRUARY, 5)));
        c.setMaxPricePerFlight(f1, 80);
        c.addWantedTicket(new Ticket(f1, 90, new GregorianCalendar(2018, GregorianCalendar.FEBRUARY, 5)));


        Client c2 = new Client(Agent.ANSI_PURPLE + "TRADER" + Agent.ANSI_RESET, 1000, 0.9, 1.2, 0);
        c2.setMaxPricePerFlight(f1, 70);
        c2.addWantedTicket(new Ticket(f1, 200, new GregorianCalendar(2017, GregorianCalendar.FEBRUARY, 5)));
        for (int i = 0; i < 4; i++)
            c2.addWantedTicket(new Ticket(f1, new Random().nextInt(50)+50, new GregorianCalendar(2018, GregorianCalendar.FEBRUARY, 5)));


        Supplier s = new Supplier(Agent.ANSI_CYAN + "SELLER" + Agent.ANSI_RESET, 0.9, 1.5);
        s.addAvailableFlight(f1, 1, 100);

        Supplier s2 = new Supplier(Agent.ANSI_YELLOW + "SMUGGLER" + Agent.ANSI_RESET, 0.9, 1.5);
        s2.addAvailableFlight(f1, 8, 80);

        new Thread(c).start();
        new Thread(c2).start();
    }
}
