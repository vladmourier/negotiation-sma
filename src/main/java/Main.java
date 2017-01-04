import model.*;
import model.communication.message.Action;
import model.communication.message.Message;

import java.util.Date;
import java.util.HashMap;

public class Main {
    public static void main(String[] args) {


        Client c = new Client("John doe", 1000, 0);
        Agent.agents.put(c.getId(), c);

        Supplier s = new Supplier("John doe", 1);
        Agent.agents.put(s.getId(), s);


        Ticket t = new Ticket(0, Destination.LYON, Destination.PARIS, 100, new Date());
        Ticket.tickets.put(t.getId(), t);

        Message m = new Message(0, -1, Action.CALL, c, s, t);

        Message m2 = new Message(m.toJSONString());

    }
}
