package by.java_enterprice.jdbc;

import by.java_enterprice.jdbc.dao.TicketDao;
import by.java_enterprice.jdbc.entity.Ticket;

import java.sql.SQLException;

public class JdbcRunner {
    public static void main(String[] args) throws SQLException {
        var ticketDao = TicketDao.getInstance();
        Ticket ticket = ticketDao.getById(5L).get();
        System.out.println(ticket);
        ticket.setSeatNo("55D");
        System.out.println(ticketDao.update(ticket));
        System.out.println(ticket);
    }
}
