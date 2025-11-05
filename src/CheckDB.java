import java.sql.DriverManager;
import java.sql.Connection;

public class CheckDB {
    public static void main(String[] args) {
        String dbPath = System.getProperty("user.dir") + System.getProperty("file.separator")
                + "Iteration 3" + System.getProperty("file.separator") + "databaseProject.db";
        String dbUrl = "jdbc:sqlite:" + dbPath;

        System.out.println("Database URL: " + dbUrl);

        try (var conn = DriverManager.getConnection(dbUrl)) {
            System.out.println("Connected to database!");

            // Check Cities
            try (var stmt = conn.createStatement();
                    var rs = stmt.executeQuery("SELECT COUNT(*) as cnt FROM Cities")) {
                if (rs.next()) {
                    System.out.println("Cities count: " + rs.getInt("cnt"));
                }
            }

            // Check Routes
            try (var stmt = conn.createStatement();
                    var rs = stmt.executeQuery("SELECT COUNT(*) as cnt FROM Routes")) {
                if (rs.next()) {
                    System.out.println("Routes count: " + rs.getInt("cnt"));
                }
            }

            // Check Connections
            try (var stmt = conn.createStatement();
                    var rs = stmt.executeQuery("SELECT COUNT(*) as cnt FROM Connection")) {
                if (rs.next()) {
                    System.out.println("Connections count: " + rs.getInt("cnt"));
                }
            }

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
