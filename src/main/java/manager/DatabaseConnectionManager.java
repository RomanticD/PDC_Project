package manager;

import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.DriverManager;

@Slf4j
public class DatabaseConnectionManager {

    private final String protocol = "jdbc:derby:";
    private Connection conn;

    public DatabaseConnectionManager() {
        if(this.conn == null){
            try {
                String dbName = "pdc_project";
                this.conn = DriverManager.getConnection(protocol + dbName);
                log.info(conn.getClass().getName());
            } catch (Exception ex) {
                log.error("Error when creating connection! Error: " + ex.getMessage());
            }
        } else {
            log.warn("A connection is already existed!");
        }
    }
    public Connection getConnection() {
        return conn;
    }
}
