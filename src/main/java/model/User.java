package model;

/**
 * A user (student) record stored in the {@code student} table.
 *
 * @author Shirabthinath Shivaji
 * @version 1.0
 */
public class User {

    /** Primary key from the {@code student_id} column; 0 until saved. */
    private int userId;

    /** First name, stored in {@code first_name}. */
    private String name;

    /** Age in years, stored in {@code age}. */
    private int age;

    /** College name, stored in {@code college}. */
    private String college;

    /** Hobbies, stored in {@code hobbies}. */
    private String hobby;

    /** Creates an empty user. */
    public User() {
    }

    /** @return the user id (student_id) */
    public int getUserId() {
        return userId;
    }

    /** @param userId the user id (student_id) */
    public void setUserId(int userId) {
        this.userId = userId;
    }

    /** @return the name */
    public String getName() {
        return name;
    }

    /** @param name the name */
    public void setName(String name) {
        this.name = name;
    }

    /** @return the age */
    public int getAge() {
        return age;
    }

    /** @param age the age */
    public void setAge(int age) {
        this.age = age;
    }

    /** @return the college */
    public String getCollege() {
        return college;
    }

    /** @param college the college */
    public void setCollege(String college) {
        this.college = college;
    }

    /** @return the hobbies */
    public String getHobby() {
        return hobby;
    }

    /** @param hobby the hobbies */
    public void setHobby(String hobby) {
        this.hobby = hobby;
    }

    /**
     * Returns the welcome message shown after the user is added.
     *
     * @return for example "Welcome Rish to College of Engineering"
     */
    @Override
    public String toString() {
        return "Welcome " + name + " to " + college;
    }
}
