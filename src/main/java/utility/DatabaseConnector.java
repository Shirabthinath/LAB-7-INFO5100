package utility;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import model.User;

/**
 * Connects the application to the MySQL database with JDBC and performs the
 * CRUD operations on the {@code student} table.
 * <p>
 * Every method uses try-with-resources so the connection and statement are
 * always closed, and {@link PreparedStatement} parameters so user input
 * cannot change the SQL (no SQL injection). Errors are passed back to the
 * caller as {@link SQLException} so the screens can tell the user.
 *
 * @author Shirabthinath Shivaji
 * @version 1.0
 */
public class DatabaseConnector {

    // DB_URL = "jdbc:<database>://ip_address:port/database_name";
    // createDatabaseIfNotExist=true creates customerdb on the first run.
    // allowPublicKeyRetrieval=true is needed to log in to MySQL 8+ without SSL.
    static final String DB_URL = "jdbc:mysql://localhost:3306/customerdb"
            + "?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true";

    /** MySQL user of the Docker container (mysql-db). */
    static final String DB_USERNAME = "root";

    /** MYSQL_ROOT_PASSWORD given when the Docker container was created. */
    static final String DB_PASSWORD = "root123";

    /** Not meant to be instantiated; all methods are static. */
    private DatabaseConnector() {
    }

    /**
     * Opens a connection to the database.
     *
     * @return a new connection; the caller closes it
     * @throws SQLException if MySQL is not running or the login is wrong
     */
    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
    }

    /**
     * Creates the {@code student} table if it does not exist yet (same
     * columns as DatabaseQueries.sql).
     *
     * @throws SQLException if the database cannot be reached
     */
    public static void createTable() throws SQLException {
        String query = "CREATE TABLE IF NOT EXISTS student("
                + "student_id INT PRIMARY KEY AUTO_INCREMENT, "
                + "first_name VARCHAR(20), "
                + "last_name VARCHAR(20), "
                + "college VARCHAR(50), "
                + "age INT, "
                + "hobbies VARCHAR(100))";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(query);
        }
    }

    // CRUD Operations on Database

    /**
     * C - Create: inserts a user into the database.
     *
     * @param u1 the user to insert
     * @throws SQLException if the insert fails
     */
    public static void addUser(User u1) throws SQLException {
        String query = "INSERT INTO student(first_name, age, college, hobbies) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, u1.getName());
            stmt.setInt(2, u1.getAge());
            stmt.setString(3, u1.getCollege());
            stmt.setString(4, u1.getHobby());
            int rows = stmt.executeUpdate();
            System.out.println("Rows inserted: " + rows);
        }
    }

    /**
     * R - Read: gets every user from the database.
     *
     * @return all users, in student_id order
     * @throws SQLException if the query fails
     */
    public static ArrayList<User> getUsers() throws SQLException {
        ArrayList<User> users = new ArrayList<>();
        String query = "SELECT * FROM student ORDER BY student_id";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                User u1 = new User();
                u1.setUserId(rs.getInt("student_id"));
                u1.setName(rs.getString("first_name"));
                u1.setCollege(rs.getString("college"));
                u1.setAge(rs.getInt("age"));
                u1.setHobby(rs.getString("hobbies"));
                users.add(u1);
            }
        }
        return users;
    }

    /**
     * U - Update: changes a user's values in the database.
     *
     * @param oldUser     the user as it is now (gives the student_id)
     * @param updatedUser the new values
     * @throws SQLException if the update fails or the user no longer exists
     */
    public static void updateUser(User oldUser, User updatedUser) throws SQLException {
        String query = "UPDATE student SET first_name = ?, college = ?, age = ?, hobbies = ? "
                + "WHERE student_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, updatedUser.getName());
            stmt.setString(2, updatedUser.getCollege());
            stmt.setInt(3, updatedUser.getAge());
            stmt.setString(4, updatedUser.getHobby());
            stmt.setInt(5, oldUser.getUserId());
            int rows = stmt.executeUpdate();
            System.out.println("Rows updated: " + rows);
            if (rows == 0) {
                throw new SQLException("No student found with id " + oldUser.getUserId());
            }
        }
    }

    /**
     * D - Delete: removes a user from the database.
     *
     * @param u1 the user to delete (matched by student_id)
     * @throws SQLException if the delete fails or the user no longer exists
     */
    public static void deleteUser(User u1) throws SQLException {
        String query = "DELETE FROM student WHERE student_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, u1.getUserId());
            int rows = stmt.executeUpdate();
            System.out.println("Rows deleted: " + rows);
            if (rows == 0) {
                throw new SQLException("No student found with id " + u1.getUserId());
            }
        }
    }
}
