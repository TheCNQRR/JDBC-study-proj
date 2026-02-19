package by.java_enterprice.jdbc;

import by.java_enterprice.jdbc.dao.TicketDao;
import by.java_enterprice.jdbc.dto.TicketFilter;
import by.java_enterprice.jdbc.entity.Ticket;

import java.sql.SQLException;

public class JdbcRunner {
    public static void main(String[] args) {
        var ticketDao = TicketDao.getInstance();
        Ticket ticket = ticketDao.findById(5L).get();
        System.out.println(ticket);
        ticket.setSeatNo("55D");
        System.out.println(ticketDao.update(ticket));
        System.out.println(ticket);

        var filter = new TicketFilter(null, "5A", 5, 0);
        System.out.println(ticketDao.findAll(filter));
    }
}
