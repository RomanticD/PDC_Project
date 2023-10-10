package db;

import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConnectionManager {

    private String protocol = "jdbc:derby:";
    private Connection conn;

    public DatabaseConnectionManager() {
        String driver = "org.apache.derby.jdbc.EmbeddedDriver";
        try {
            String dbName = "pdc_project";
            Class.forName(driver);
            this.conn = DriverManager.getConnection(protocol + dbName);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    public Connection getConnection() {
        return conn;
    }
}
