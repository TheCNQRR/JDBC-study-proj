package by.java_enterprice.jdbc.dao;

import by.java_enterprice.jdbc.entity.Flight;
import by.java_enterprice.jdbc.entity.FlightStatus;
import by.java_enterprice.jdbc.exception.DaoException;
import by.java_enterprice.jdbc.utils.ConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FlightDao implements Dao<Long, Flight> {
    private final static FlightDao INSTANCE = new FlightDao();

    private final static String FIND_ALL_SQL = """
                                    SELECT id, flight_no, departure_date, departure_airport_code, arrival_date,
                                           arrival_airport_code, aircraft_id, status
                                    FROM flight
                                    """;

    private final static String FIND_BY_ID_SQL = FIND_ALL_SQL + """                                   
                                    WHERE id = ?
                                    """;

    private final static String SAVE_SQL = """
                                    INSERT INTO flight
                                    (flight_no, departure_date, departure_airport_code, arrival_date,
                                    arrival_airport_code, aircraft_id, status)
                                    VALUES (?, ?, ?, ?, ?, ?, ?)
                                    
                                    """;

    private final static String UPDATE_SQL = """
                                    UPDATE flight
                                    SET flight_no = ?,
                                        departure_date = ?,
                                        departure_airport_code = ?,
                                        arrival_date = ?,
                                        arrival_airport_code = ?,
                                        aircraft_id = ?,
                                        status = ?
                                    WHERE id = ?
                                    """;

    private final static String DELETE_SQL = """
                                    DELETE FROM flight
                                    WHERE id = ?
                                    """;
    @Override
    public List<Flight> findAll() {
        try(var connection = ConnectionManager.get();
            var statement = connection.prepareStatement(FIND_ALL_SQL)) {
            List<Flight> flights = new ArrayList<>();

            var result = statement.executeQuery();

            while (result.next()) {
                flights.add(
                        buildFlight(result)
                );
            }

            return flights;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public Optional<Flight> findById(Long id) {
        try (var connection = ConnectionManager.get()) {
            return findById(id, connection);
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    public Optional<Flight> findById(Long id, Connection connection) {
        try(
            var statement = connection.prepareStatement(FIND_BY_ID_SQL)) {
            statement.setLong(1, id);
            var result = statement.executeQuery();

            Flight flight = null;
            if (result.next()) {
                flight = buildFlight(result);
            }
            return Optional.ofNullable(flight);
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public Flight save(Flight flight) {
        try(var connection = ConnectionManager.get();
            var statement = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, flight.getFlightNo());
            statement.setTimestamp(2, Timestamp.valueOf(flight.getDepartureDate()));
            statement.setString(3, flight.getDepartureAirportCode());
            statement.setTimestamp(4, Timestamp.valueOf(flight.getArrivalDate()));
            statement.setString(5, flight.getArrivalAirportCode());
            statement.setLong(6, flight.getAircraftId());
            statement.setObject(7,flight.getStatus());

            var result = statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    @Override
    public boolean update(Flight flight) {
        return false;
    }

    @Override
    public boolean delete(Long id) {
        return false;
    }

    private Flight buildFlight(ResultSet result) throws SQLException {
        return new Flight(
                result.getLong("id"),
                result.getString("flight_no"),
                result.getTimestamp("departure_date").toLocalDateTime(),
                result.getString("departure_airport_code"),
                result.getTimestamp("arrival_date").toLocalDateTime(),
                result.getString("arrival_airport_code"),
                result.getLong("aircraft_id"),
                FlightStatus.valueOf(result.getString("status"))
        );
    }

    public static FlightDao getInstance() {
        return INSTANCE;
    }

    private FlightDao() {}
}
