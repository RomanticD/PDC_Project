package manager;

import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.DriverManager;

@Slf4j
public class DatabaseConnectionManager {
    private Connection conn;

    public DatabaseConnectionManager() {
        try {
            String dbName = "pdc_project";
            String protocol = "jdbc:derby:";
            this.conn = DriverManager.getConnection(protocol + dbName);
            log.info(conn.getClass().getName());
        } catch (Exception ex) {
            log.error("Error when creating connection! Error: " + ex.getMessage());
        }
    }
    public Connection getConnection() {
        if (conn == null) log.warn("Please disconnect the existing DB connection and restart the app!");
        return conn;
    }
}
