package model;

import java.time.LocalDate;
import java.time.Period;

/**
 * A student profile stored in the {@code student_profile} table.
 *
 * @author Shirabthinath Shivaji
 * @version 2.0
 */
public class User {

    /** Primary key (student_id); 0 until the student has been saved. */
    private int userId;

    /** First name. */
    private String firstName;

    /** Last name. */
    private String lastName;

    /** Gender: "Male", "Female" or "Other". */
    private String gender;

    /** Date of birth; the age is calculated from it. */
    private LocalDate dateOfBirth;

    /** Phone number in the format 123-456-7890. */
    private String phone;

    /** Email address; unique in the database. */
    private String email;

    /** College name. */
    private String college;

    /** Continent the student is from. */
    private String continent;

    /** Work or project experience (optional). */
    private String experience;

    /** Hobbies (optional). */
    private String hobby;

    /** Full path of the profile photo on this computer (optional). */
    private String photoPath;

    /** Creates an empty user. */
    public User() {
    }

    /**
     * Calculates the age in whole years from the date of birth.
     *
     * @return the age today, or 0 if the date of birth is not set
     */
    public int getAge() {
        if (dateOfBirth == null) {
            return 0;
        }
        return Period.between(dateOfBirth, LocalDate.now()).getYears();
    }

    /** @return the user id (student_id) */
    public int getUserId() { return userId; }

    /** @param userId the user id (student_id) */
    public void setUserId(int userId) { this.userId = userId; }

    /** @return the first name */
    public String getFirstName() { return firstName; }

    /** @param firstName the first name */
    public void setFirstName(String firstName) { this.firstName = firstName; }

    /** @return the last name */
    public String getLastName() { return lastName; }

    /** @param lastName the last name */
    public void setLastName(String lastName) { this.lastName = lastName; }

    /** @return the gender */
    public String getGender() { return gender; }

    /** @param gender the gender */
    public void setGender(String gender) { this.gender = gender; }

    /** @return the date of birth */
    public LocalDate getDateOfBirth() { return dateOfBirth; }

    /** @param dateOfBirth the date of birth */
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    /** @return the phone number */
    public String getPhone() { return phone; }

    /** @param phone the phone number */
    public void setPhone(String phone) { this.phone = phone; }

    /** @return the email address */
    public String getEmail() { return email; }

    /** @param email the email address */
    public void setEmail(String email) { this.email = email; }

    /** @return the college */
    public String getCollege() { return college; }

    /** @param college the college */
    public void setCollege(String college) { this.college = college; }

    /** @return the continent */
    public String getContinent() { return continent; }

    /** @param continent the continent */
    public void setContinent(String continent) { this.continent = continent; }

    /** @return the experience */
    public String getExperience() { return experience; }

    /** @param experience the experience */
    public void setExperience(String experience) { this.experience = experience; }

    /** @return the hobbies */
    public String getHobby() { return hobby; }

    /** @param hobby the hobbies */
    public void setHobby(String hobby) { this.hobby = hobby; }

    /** @return the photo path, or null if there is no photo */
    public String getPhotoPath() { return photoPath; }

    /** @param photoPath the photo path */
    public void setPhotoPath(String photoPath) { this.photoPath = photoPath; }

    /**
     * Returns the welcome message shown after the student is added.
     *
     * @return for example "Welcome Shirabthinath Shivaji to College of Engineering"
     */
    @Override
    public String toString() {
        return "Welcome " + firstName + " " + lastName + " to " + college;
    }
}
