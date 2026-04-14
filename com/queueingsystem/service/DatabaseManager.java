package com.queueingsystem.service;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    private static final String DB_FOLDER = "data";
    private static final String DB_URL = "jdbc:sqlite:data/pilaless.db";

    static {
        File folder = new File(DB_FOLDER);
        if (!folder.exists()) {
            folder.mkdirs();
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    public static void initializeDatabase() {
        createAdminAccountsTable();
        createQueueTicketsTable();
        seedAdminAccounts();
    }

    private static void createAdminAccountsTable() {
        String sql = """
                CREATE TABLE IF NOT EXISTS admin_accounts (
                    username TEXT PRIMARY KEY,
                    password TEXT NOT NULL,
                    office_type TEXT NOT NULL,
                    window_number INTEGER NOT NULL
                )
                """;

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void createQueueTicketsTable() {
        String sql = """
                CREATE TABLE IF NOT EXISTS queue_tickets (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    queue_number TEXT NOT NULL,
                    student_name TEXT NOT NULL,
                    student_id TEXT NOT NULL,
                    office_type TEXT NOT NULL,
                    state TEXT NOT NULL,
                    assigned_window INTEGER,
                    created_at INTEGER NOT NULL,
                    called_at INTEGER DEFAULT 0,
                    transaction_date TEXT NOT NULL
                )
                """;

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);

            stmt.execute("""
                    CREATE INDEX IF NOT EXISTS idx_queue_date_office
                    ON queue_tickets(transaction_date, office_type)
                    """);

            stmt.execute("""
                    CREATE INDEX IF NOT EXISTS idx_queue_window
                    ON queue_tickets(transaction_date, office_type, assigned_window)
                    """);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void seedAdminAccounts() {
        String[] inserts = {
                "INSERT OR IGNORE INTO admin_accounts(username,password,office_type,window_number) VALUES('FinanceONE','FWINDOW1','FINANCE',1)",
                "INSERT OR IGNORE INTO admin_accounts(username,password,office_type,window_number) VALUES('FinanceTWO','FWINDOW2','FINANCE',2)",
                "INSERT OR IGNORE INTO admin_accounts(username,password,office_type,window_number) VALUES('FinanceTHREE','FWINDOW3','FINANCE',3)",

                "INSERT OR IGNORE INTO admin_accounts(username,password,office_type,window_number) VALUES('RegistrarONE','RWINDOW1','REGISTRAR',1)",
                "INSERT OR IGNORE INTO admin_accounts(username,password,office_type,window_number) VALUES('RegistrarTWO','RWINDOW2','REGISTRAR',2)",
                "INSERT OR IGNORE INTO admin_accounts(username,password,office_type,window_number) VALUES('RegistrarTHREE','RWINDOW3','REGISTRAR',3)"
        };

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            for (String sql : inserts) {
                stmt.executeUpdate(sql);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}