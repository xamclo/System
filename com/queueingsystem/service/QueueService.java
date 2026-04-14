package com.queueingsystem.service;

import com.queueingsystem.enumtypes.OfficeType;
import com.queueingsystem.enumtypes.QueueState;
import com.queueingsystem.model.QueueTicket;
import com.queueingsystem.model.Student;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class QueueService {

    public QueueService() {
        DatabaseManager.initializeDatabase();
    }

    public synchronized QueueTicket createTicket(String name, String studentId, OfficeType officeType) {
        String today = LocalDate.now().toString();

        // IMPORTANT:
        // If same student already has a ticket TODAY for the same office,
        // return that existing ticket instead of creating a new one.
        QueueTicket existingTicket = findTodayTicketByStudentIdAndOffice(studentId, officeType);
        if (existingTicket != null) {
            return existingTicket;
        }

        int nextNumber = getNextSequenceForToday(officeType, today);
        String queueNumber = String.format("%s-%03d", officeType.getPrefix(), nextNumber);

        Student student = new Student(name, studentId);
        QueueTicket ticket = new QueueTicket(queueNumber, student, officeType);
        ticket.setState(QueueState.WAITING);

        String sql = """
                INSERT INTO queue_tickets(
                    queue_number, student_name, student_id, office_type,
                    state, assigned_window, created_at, called_at, transaction_date
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, ticket.getNumber());
            ps.setString(2, student.getName());
            ps.setString(3, student.getStudentId());
            ps.setString(4, officeType.name());
            ps.setString(5, QueueState.WAITING.name());
            ps.setNull(6, Types.INTEGER);
            ps.setLong(7, ticket.getCreatedAt());
            ps.setLong(8, 0L);
            ps.setString(9, today);

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return findTicket(queueNumber);
    }

    private int getNextSequenceForToday(OfficeType officeType, String date) {
        String sql = """
                SELECT queue_number
                FROM queue_tickets
                WHERE office_type = ?
                AND transaction_date = ?
                ORDER BY id DESC
                LIMIT 1
                """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, officeType.name());
            ps.setString(2, date);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String lastNumber = rs.getString("queue_number");
                    if (lastNumber != null && lastNumber.contains("-")) {
                        String digits = lastNumber.substring(lastNumber.indexOf('-') + 1);
                        return Integer.parseInt(digits) + 1;
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 1;
    }

    public synchronized QueueTicket serveNext(OfficeType officeType, int windowNumber) {
        QueueTicket existing = getCurrentServing(officeType, windowNumber);
        if (existing != null) return existing;

        QueueTicket next = getNextWaitingTicket(officeType);
        if (next != null) {
            updateTicketState(next.getNumber(), officeType, QueueState.SERVING, windowNumber, System.currentTimeMillis());
        }

        return getCurrentServing(officeType, windowNumber);
    }

    public synchronized QueueTicket serveSelected(OfficeType officeType, int windowNumber, String ticketNumber) {
        QueueTicket existing = getCurrentServing(officeType, windowNumber);
        if (existing != null) return existing;

        String sql = """
                UPDATE queue_tickets
                SET state = ?, assigned_window = ?, called_at = ?
                WHERE queue_number = ?
                AND office_type = ?
                AND transaction_date = ?
                AND state = ?
                """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, QueueState.SERVING.name());
            ps.setInt(2, windowNumber);
            ps.setLong(3, System.currentTimeMillis());
            ps.setString(4, ticketNumber);
            ps.setString(5, officeType.name());
            ps.setString(6, LocalDate.now().toString());
            ps.setString(7, QueueState.WAITING.name());

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return getCurrentServing(officeType, windowNumber);
    }

    public synchronized void markDone(OfficeType officeType, int windowNumber) {
        QueueTicket current = getCurrentServing(officeType, windowNumber);
        if (current != null) {
            updateTicketState(current.getNumber(), officeType, QueueState.COMPLETED, windowNumber, current.getCalledAt());
        }
    }

    public synchronized void skipCurrent(OfficeType officeType, int windowNumber) {
        QueueTicket current = getCurrentServing(officeType, windowNumber);
        if (current != null) {
            updateTicketState(current.getNumber(), officeType, QueueState.SKIPPED, windowNumber, current.getCalledAt());
        }
    }

    private void updateTicketState(String queueNumber,
                                   OfficeType officeType,
                                   QueueState state,
                                   Integer assignedWindow,
                                   long calledAt) {

        String sql = """
                UPDATE queue_tickets
                SET state = ?, assigned_window = ?, called_at = ?
                WHERE queue_number = ?
                AND office_type = ?
                AND transaction_date = ?
                """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, state.name());

            if (assignedWindow == null) {
                ps.setNull(2, Types.INTEGER);
            } else {
                ps.setInt(2, assignedWindow);
            }

            ps.setLong(3, calledAt);
            ps.setString(4, queueNumber);
            ps.setString(5, officeType.name());
            ps.setString(6, LocalDate.now().toString());

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized QueueTicket getCurrentServing(OfficeType officeType, int windowNumber) {
        String sql = """
                SELECT *
                FROM queue_tickets
                WHERE office_type = ?
                AND transaction_date = ?
                AND state = ?
                AND assigned_window = ?
                ORDER BY id DESC
                LIMIT 1
                """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, officeType.name());
            ps.setString(2, LocalDate.now().toString());
            ps.setString(3, QueueState.SERVING.name());
            ps.setInt(4, windowNumber);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapTicket(rs);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public synchronized QueueTicket getNextWaitingTicket(OfficeType officeType) {
        String sql = """
                SELECT *
                FROM queue_tickets
                WHERE office_type = ?
                AND transaction_date = ?
                AND state = ?
                ORDER BY id ASC
                LIMIT 1
                """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, officeType.name());
            ps.setString(2, LocalDate.now().toString());
            ps.setString(3, QueueState.WAITING.name());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapTicket(rs);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public synchronized String getNextNumber(OfficeType officeType) {
        QueueTicket next = getNextWaitingTicket(officeType);
        return next == null ? null : next.getNumber();
    }

    public synchronized String getNowServingNumber(OfficeType officeType) {
        List<QueueTicket> serving = getServingTickets(officeType);
        if (serving.isEmpty()) return null;
        return serving.get(0).getNumber();
    }

    public synchronized String getNowServingDisplay(OfficeType officeType) {
        List<QueueTicket> serving = getServingTickets(officeType);
        if (serving.isEmpty()) return null;

        List<String> numbers = new ArrayList<>();
        for (QueueTicket ticket : serving) {
            numbers.add(ticket.getNumber());
        }
        return String.join("  ", numbers);
    }

    public synchronized List<String> getWaitingNumbers(OfficeType officeType) {
        List<String> result = new ArrayList<>();
        for (QueueTicket ticket : getWaitingTickets(officeType)) {
            result.add(ticket.getNumber());
        }
        return result;
    }

    public synchronized List<QueueTicket> getServingTickets(OfficeType officeType) {
        return getTicketsByStateForToday(officeType, QueueState.SERVING);
    }

    public synchronized List<QueueTicket> getWaitingTickets(OfficeType officeType) {
        return getTicketsByStateForToday(officeType, QueueState.WAITING);
    }

    private List<QueueTicket> getTicketsByStateForToday(OfficeType officeType, QueueState state) {
        List<QueueTicket> result = new ArrayList<>();

        String sql = """
                SELECT *
                FROM queue_tickets
                WHERE office_type = ?
                AND transaction_date = ?
                AND state = ?
                ORDER BY
                    CASE WHEN assigned_window IS NULL THEN 999 ELSE assigned_window END ASC,
                    id ASC
                """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, officeType.name());
            ps.setString(2, LocalDate.now().toString());
            ps.setString(3, state.name());

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(mapTicket(rs));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public synchronized QueueTicket findTicket(String number) {
        String sql = """
                SELECT *
                FROM queue_tickets
                WHERE queue_number = ?
                AND transaction_date = ?
                ORDER BY id DESC
                LIMIT 1
                """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, number);
            ps.setString(2, LocalDate.now().toString());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapTicket(rs);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public synchronized QueueTicket findLatestTicketByStudentId(String studentId) {
        String sql = """
                SELECT *
                FROM queue_tickets
                WHERE student_id = ?
                ORDER BY id DESC
                LIMIT 1
                """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, studentId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapTicket(rs);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public synchronized QueueTicket findTodayTicketByStudentIdAndOffice(String studentId, OfficeType officeType) {
        String sql = """
                SELECT *
                FROM queue_tickets
                WHERE student_id = ?
                AND office_type = ?
                AND transaction_date = ?
                ORDER BY id DESC
                LIMIT 1
                """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, studentId);
            ps.setString(2, officeType.name());
            ps.setString(3, LocalDate.now().toString());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapTicket(rs);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public synchronized void removeTicket(QueueTicket ticketToRemove) {
        if (ticketToRemove == null) return;

        String sql = """
                DELETE FROM queue_tickets
                WHERE queue_number = ?
                AND transaction_date = ?
                AND state = ?
                """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, ticketToRemove.getNumber());
            ps.setString(2, LocalDate.now().toString());
            ps.setString(3, QueueState.WAITING.name());

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized List<QueueTicket> getTransactionsByDate(LocalDate date, OfficeType officeType) {
        List<QueueTicket> result = new ArrayList<>();

        String sql = """
                SELECT *
                FROM queue_tickets
                WHERE transaction_date = ?
                AND office_type = ?
                ORDER BY id ASC
                """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, date.toString());
            ps.setString(2, officeType.name());

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(mapTicket(rs));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public synchronized List<QueueTicket> getTransactionsByDateAndWindow(LocalDate date,
                                                                         OfficeType officeType,
                                                                         int windowNumber) {
        List<QueueTicket> result = new ArrayList<>();

        String sql = """
                SELECT *
                FROM queue_tickets
                WHERE transaction_date = ?
                AND office_type = ?
                AND assigned_window = ?
                ORDER BY id ASC
                """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, date.toString());
            ps.setString(2, officeType.name());
            ps.setInt(3, windowNumber);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(mapTicket(rs));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    private QueueTicket mapTicket(ResultSet rs) throws SQLException {
        Student student = new Student(
                rs.getString("student_name"),
                rs.getString("student_id")
        );

        OfficeType officeType = OfficeType.valueOf(rs.getString("office_type"));

        QueueTicket ticket = new QueueTicket(
                rs.getString("queue_number"),
                student,
                officeType
        );

        ticket.setState(QueueState.valueOf(rs.getString("state")));

        int assignedWindow = rs.getInt("assigned_window");
        if (!rs.wasNull()) {
            ticket.setAssignedWindow(assignedWindow);
        }

        ticket.setCalledAt(rs.getLong("called_at"));
        return ticket;
    }
}