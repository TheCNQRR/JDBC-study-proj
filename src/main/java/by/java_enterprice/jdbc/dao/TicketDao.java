package by.java_enterprice.jdbc.dao;

import by.java_enterprice.jdbc.dto.TicketFilter;
import by.java_enterprice.jdbc.entity.Ticket;
import by.java_enterprice.jdbc.exception.DaoException;
import by.java_enterprice.jdbc.utils.ConnectionManager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class TicketDao {
    private final static TicketDao INSTANCE = new TicketDao();

    private final static String SAVE_SQL = """
                                            INSERT INTO ticket
                                            (passport_no, passenger_name, flight_id, seat_no, cost) 
                                            VALUES (?, ?, ?, ?, ?)
                                            """;

    private final static String DELETE_SQL = """
                                            DELETE FROM ticket
                                            WHERE id = ?
                                            """;

    private final static String FIND_ALL_SQL = """
                                            SELECT id, passport_no, passenger_name, flight_id, seat_no, cost
                                            FROM ticket
                                            """;

    private final static String GET_BY_ID_SQL = """
                                                SELECT id, passport_no, passenger_name, flight_id, seat_no, cost
                                                FROM ticket
                                                WHERE id = ?
                                                """;

    private final static String UPDATE_SQL = """
                                                UPDATE ticket
                                                SET passport_no = ?,
                                                    passenger_name = ?,
                                                    flight_id = ?,
                                                    seat_no = ?,
                                                    cost = ?
                                                WHERE id = ?
                                                """;

    public List<Ticket> findAll(TicketFilter filter) {
        List<Object> parameters = new ArrayList<>();
        List<String> whereSql = new ArrayList<>();

        if (filter.passengerName() != null) {
            parameters.add(filter.passengerName());
            whereSql.add("passenger_name = ?");
        }

        if (filter.seatNo() != null) {
            parameters.add(filter.seatNo());
            whereSql.add("seat_no = ?");
        }
        parameters.add(filter.limit());
        parameters.add(filter.offset());
        var where = whereSql.stream().collect(Collectors.joining(
                " AND ",
                parameters.size() > 2 ? " WHERE " : " ",
                " LIMIT ? OFFSET ? "
        ));
        String sql = FIND_ALL_SQL + where;

        try(var connection = ConnectionManager.get();
            var statement = connection.prepareStatement(sql)) {
            List<Ticket> tickets = new ArrayList<>();

            for (int i = 0; i < parameters.size(); i++) {
                statement.setObject(i + 1, parameters.get(i));
            }

            var result = statement.executeQuery();

            while (result.next()) {
                tickets.add(
                        buildTicket(result)
                );
            }

            return tickets;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    public List<Ticket> findAll() {
        try(var connection = ConnectionManager.get();
            var statement = connection.prepareStatement(FIND_ALL_SQL)) {
            List<Ticket> tickets = new ArrayList<>();

            var result = statement.executeQuery();

            while (result.next()) {
                tickets.add(
                        buildTicket(result)
                );
            }

            return tickets;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }



    public Optional<Ticket> getById(Long id) {
        try(var connection = ConnectionManager.get();
            var statement = connection.prepareStatement(GET_BY_ID_SQL)) {
            statement.setLong(1, id);
            var result = statement.executeQuery();

            Ticket ticket = null;
            if (result.next()) {
                ticket = new Ticket(
                        result.getLong("id"),
                        result.getString("passport_no"),
                        result.getString("passenger_name"),
                        result.getString("seat_no"),
                        result.getLong("flight_id"),
                        result.getBigDecimal("cost")
                );
            }
            return Optional.ofNullable(ticket);
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    public Ticket save(Ticket ticket) {
        try(var connection = ConnectionManager.get();
            var statement = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, ticket.getPassportNo());
            statement.setString(2, ticket.getPassengerName());
            statement.setLong(3, ticket.getFlightId());
            statement.setString(4, ticket.getSeatNo());
            statement.setBigDecimal(5, ticket.getCost());

            statement.executeUpdate();
            var keys = statement.getGeneratedKeys();
            if (keys.next()) {
                ticket.setId(keys.getLong("id"));
            }

            return ticket;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    public boolean update(Ticket ticket) {
        var connection = ConnectionManager.get();
        try(var statement = connection.prepareStatement(UPDATE_SQL)) {
            statement.setString(1, ticket.getPassportNo());
            statement.setString(2, ticket.getPassengerName());
            statement.setLong(3, ticket.getFlightId());
            statement.setString(4, ticket.getSeatNo());
            statement.setBigDecimal(5, ticket.getCost());
            statement.setLong(6, ticket.getId());
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    public boolean delete(Long id) {
        try (var connection = ConnectionManager.get();
            var statement = connection.prepareStatement(DELETE_SQL)){
            statement.setLong(1, id);

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    private Ticket buildTicket(ResultSet result) throws SQLException {
        return new Ticket(
                result.getLong("id"),
                result.getString("passport_no"),
                result.getString("passenger_name"),
                result.getString("seat_no"),
                result.getLong("flight_id"),
                result.getBigDecimal("cost")
        );
    }

    private TicketDao() {}

    public static TicketDao getInstance() {
        return INSTANCE;
    }
}
