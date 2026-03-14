import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/busbooking";
    private static final String USER = "root";
    private static final String PASSWORD = "root";

    public static Connection getConnection() {

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Connected Successfully");
            return con;

        } catch (ClassNotFoundException e) {
            System.err.println("ERROR: MySQL JDBC Driver not found!");
            System.err.println("Please add mysql-connector-java JAR to your classpath");
            e.printStackTrace();
            return null;
        } catch (SQLException e) {
            System.err.println("ERROR: Database Connection Failed!");
            System.err.println("URL: " + URL);
            System.err.println("User: " + USER);
            System.err.println("Message: " + e.getMessage());
            System.err.println("\nChecklist:");
            System.err.println("1. Is MySQL server running on localhost:3306?");
            System.err.println("2. Does database 'busbooking' exist?");
            System.err.println("3. Does table 'register_user' exist?");
            System.err.println("4. Are the credentials correct (root/root)?");
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            System.err.println("ERROR: Unexpected error occurred!");
            e.printStackTrace();
            return null;
        }
    }
}
