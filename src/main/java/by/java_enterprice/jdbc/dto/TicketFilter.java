package by.java_enterprice.jdbc.dto;

public record TicketFilter (
    String passengerName,
    String seatNo,
    int limit,
    int offset) {
}
