package utility;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import model.User;

/**
 * Connects the application to MySQL with JDBC and performs the CRUD
 * operations on the {@code student_profile} table.
 * <p>
 * Every method uses try-with-resources so connections and statements are
 * always closed, and {@link PreparedStatement} parameters so user input can
 * never change the SQL (no SQL injection). Errors are passed back as
 * {@link SQLException} so the screens can show a message to the user.
 *
 * @author Shirabthinath Shivaji
 * @version 2.0
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

    /** Column list shared by the INSERT and UPDATE statements. */
    private static final String COLUMNS =
            "first_name, last_name, gender, date_of_birth, phone, email, college, continent, "
            + "experience, hobbies, photo_path";

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
     * Creates the {@code student_profile} table if it does not exist yet
     * (same definition as DatabaseQueries.sql).
     *
     * @throws SQLException if the database cannot be reached
     */
    public static void createTable() throws SQLException {
        String query = "CREATE TABLE IF NOT EXISTS student_profile ("
                + "student_id INT PRIMARY KEY AUTO_INCREMENT, "
                + "first_name VARCHAR(30) NOT NULL, "
                + "last_name VARCHAR(30) NOT NULL, "
                + "gender VARCHAR(10) NOT NULL, "
                + "date_of_birth DATE NOT NULL, "
                + "phone VARCHAR(20) NOT NULL, "
                + "email VARCHAR(100) NOT NULL UNIQUE, "
                + "college VARCHAR(50) NOT NULL, "
                + "continent VARCHAR(30), "
                + "experience VARCHAR(500), "
                + "hobbies VARCHAR(200), "
                + "photo_path VARCHAR(500))";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(query);
            // Tables created by an earlier version of the app do not have these
            // columns yet. CREATE TABLE IF NOT EXISTS does not change an existing
            // table, so add them here (existing rows are kept).
            addColumnIfMissing(conn, "continent", "VARCHAR(30) AFTER college");
            addColumnIfMissing(conn, "experience", "VARCHAR(500) AFTER continent");
        }
    }

    /**
     * Adds a column to the {@code student_profile} table if it is missing.
     *
     * @param conn       an open connection
     * @param column     the column name
     * @param definition the column type and position, e.g. "VARCHAR(30) AFTER college"
     * @throws SQLException if the column cannot be checked or added
     */
    private static void addColumnIfMissing(Connection conn, String column, String definition)
            throws SQLException {
        String check = "SELECT COUNT(*) FROM information_schema.COLUMNS "
                + "WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'student_profile' AND COLUMN_NAME = ?";
        try (PreparedStatement stmt = conn.prepareStatement(check)) {
            stmt.setString(1, column);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    return;
                }
            }
        }
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("ALTER TABLE student_profile ADD COLUMN " + column + " " + definition);
            System.out.println("Added column " + column + " to student_profile");
        }
    }

    /**
     * Copies a user's values into the first eleven parameters of a statement,
     * in the order of {@link #COLUMNS}.
     *
     * @param stmt the statement to fill
     * @param u    the user whose values are used
     * @throws SQLException if a parameter cannot be set
     */
    private static void setUserValues(PreparedStatement stmt, User u) throws SQLException {
        stmt.setString(1, u.getFirstName());
        stmt.setString(2, u.getLastName());
        stmt.setString(3, u.getGender());
        // setObject with a LocalDate sends the date exactly as entered. The older
        // setDate(java.sql.Date) is converted between time zones by the driver,
        // which can save the date one day early.
        stmt.setObject(4, u.getDateOfBirth());
        stmt.setString(5, u.getPhone());
        stmt.setString(6, u.getEmail());
        stmt.setString(7, u.getCollege());
        stmt.setString(8, u.getContinent());
        stmt.setString(9, u.getExperience());
        stmt.setString(10, u.getHobby());
        stmt.setString(11, u.getPhotoPath());
    }

    // CRUD Operations on Database

    /**
     * C - Create: inserts a student into the database.
     *
     * @param u1 the student to insert
     * @throws java.sql.SQLIntegrityConstraintViolationException if the email
     *         is already used by another student
     * @throws SQLException if the insert fails for another reason
     */
    public static void addUser(User u1) throws SQLException {
        String query = "INSERT INTO student_profile(" + COLUMNS + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            setUserValues(stmt, u1);
            int rows = stmt.executeUpdate();
            System.out.println("Rows inserted: " + rows);
        }
    }

    /**
     * R - Read: gets every student from the database.
     *
     * @return all students, in student_id order
     * @throws SQLException if the query fails
     */
    public static ArrayList<User> getUsers() throws SQLException {
        ArrayList<User> users = new ArrayList<>();
        String query = "SELECT * FROM student_profile ORDER BY student_id";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                User u1 = new User();
                u1.setUserId(rs.getInt("student_id"));
                u1.setFirstName(rs.getString("first_name"));
                u1.setLastName(rs.getString("last_name"));
                u1.setGender(rs.getString("gender"));
                u1.setDateOfBirth(rs.getObject("date_of_birth", LocalDate.class));
                u1.setPhone(rs.getString("phone"));
                u1.setEmail(rs.getString("email"));
                u1.setCollege(rs.getString("college"));
                u1.setContinent(rs.getString("continent"));
                u1.setExperience(rs.getString("experience"));
                u1.setHobby(rs.getString("hobbies"));
                u1.setPhotoPath(rs.getString("photo_path"));
                users.add(u1);
            }
        }
        return users;
    }

    /**
     * U - Update: changes a student's values in the database.
     *
     * @param oldUser     the student as it is now (gives the student_id)
     * @param updatedUser the new values
     * @throws java.sql.SQLIntegrityConstraintViolationException if the new
     *         email is already used by another student
     * @throws SQLException if the update fails or the student no longer exists
     */
    public static void updateUser(User oldUser, User updatedUser) throws SQLException {
        String query = "UPDATE student_profile SET first_name = ?, last_name = ?, gender = ?, "
                + "date_of_birth = ?, phone = ?, email = ?, college = ?, continent = ?, "
                + "experience = ?, hobbies = ?, photo_path = ? "
                + "WHERE student_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            setUserValues(stmt, updatedUser);
            stmt.setInt(12, oldUser.getUserId());
            int rows = stmt.executeUpdate();
            System.out.println("Rows updated: " + rows);
            if (rows == 0) {
                throw new SQLException("No student found with id " + oldUser.getUserId());
            }
        }
    }

    /**
     * D - Delete: removes a student from the database.
     *
     * @param u1 the student to delete (matched by student_id)
     * @throws SQLException if the delete fails or the student no longer exists
     */
    public static void deleteUser(User u1) throws SQLException {
        String query = "DELETE FROM student_profile WHERE student_id = ?";
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
