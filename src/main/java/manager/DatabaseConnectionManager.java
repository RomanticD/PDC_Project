package manager;

import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConnectionManager {

    private final String protocol = "jdbc:derby:";
    private Connection conn;

    public DatabaseConnectionManager() {
        try {
            String dbName = "pdc_project";
            this.conn = DriverManager.getConnection(protocol + dbName);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    public Connection getConnection() {
        return conn;
    }
}
