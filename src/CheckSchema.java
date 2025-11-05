import java.sql.DriverManager;

public class CheckSchema {
    public static void main(String[] args) {
        String dbPath = System.getProperty("user.dir") + System.getProperty("file.separator")
                + "Iteration 3" + System.getProperty("file.separator") + "databaseProject.db";
        String dbUrl = "jdbc:sqlite:" + dbPath;

        try (var conn = DriverManager.getConnection(dbUrl)) {
            // Check Routes table schema
            System.out.println("=== Routes Table Schema ===");
            try (var stmt = conn.createStatement();
                    var rs = stmt.executeQuery("PRAGMA table_info(Routes)")) {
                while (rs.next()) {
                    System.out.println("Column: " + rs.getString("name") + ", Type: " + rs.getString("type"));
                }
            }

            // Check first few rows
            System.out.println("\n=== First Routes Row ===");
            try (var stmt = conn.createStatement();
                    var rs = stmt.executeQuery("SELECT * FROM Routes LIMIT 1")) {
                if (rs.next()) {
                    for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                        System.out.println(rs.getMetaData().getColumnName(i) + ": " + rs.getString(i));
                    }
                }
            }

            System.out.println("\n=== Connection Table Schema ===");
            try (var stmt = conn.createStatement();
                    var rs = stmt.executeQuery("PRAGMA table_info(Connection)")) {
                while (rs.next()) {
                    System.out.println("Column: " + rs.getString("name") + ", Type: " + rs.getString("type"));
                }
            }

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
