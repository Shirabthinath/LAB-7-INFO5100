package utility;

import java.time.LocalDate;
import java.time.Period;

/**
 * Validation rules shared by the create and edit screens. Each method returns
 * an error message, or {@code null} when the value is valid.
 *
 * @author Shirabthinath Shivaji
 * @version 2.0
 */
public final class UserValidator {

    /** Youngest allowed age in years. */
    public static final int MIN_AGE = 16;

    /** Oldest allowed age in years. */
    public static final int MAX_AGE = 100;

    /** Not meant to be instantiated. */
    private UserValidator() {
    }

    /**
     * Checks a first or last name: required, letters / spaces / apostrophes /
     * hyphens only, and at most 30 characters (the column size).
     *
     * @param label "First name" or "Last name", used in the message
     * @param name  the name as typed
     * @return an error message, or {@code null} if valid
     */
    public static String checkName(String label, String name) {
        if (name == null || name.isBlank()) {
            return "Please enter your " + label.toLowerCase() + ".";
        }
        if (!name.trim().matches("[A-Za-z][A-Za-z '\\-]*")) {
            return label + " can contain only letters, spaces, apostrophes and hyphens.";
        }
        if (name.trim().length() > 30) {
            return label + " must be 30 characters or fewer.";
        }
        return null;
    }

    /**
     * Checks the date of birth: required, not in the future, and giving an
     * age between {@link #MIN_AGE} and {@link #MAX_AGE}.
     *
     * @param dob the date of birth
     * @return an error message, or {@code null} if valid
     */
    public static String checkDateOfBirth(LocalDate dob) {
        if (dob == null) {
            return "Please select your date of birth.";
        }
        if (dob.isAfter(LocalDate.now())) {
            return "Date of birth cannot be in the future.";
        }
        int age = Period.between(dob, LocalDate.now()).getYears();
        if (age < MIN_AGE || age > MAX_AGE) {
            return "Age must be between " + MIN_AGE + " and " + MAX_AGE + " (yours is " + age + ").";
        }
        return null;
    }

    /**
     * Converts a phone number with 10 digits, typed with or without spaces,
     * dashes, dots or brackets, to the format 123-456-7890.
     *
     * @param phone the phone number as typed
     * @return the formatted number, or {@code null} if it does not have
     *         exactly 10 digits
     */
    public static String formatPhone(String phone) {
        if (phone == null || !phone.trim().matches("[0-9 ()+.\\-]+")) {
            return null;
        }
        String digits = phone.replaceAll("[^0-9]", "");
        if (digits.length() == 11 && digits.startsWith("1")) {
            digits = digits.substring(1);
        }
        if (digits.length() != 10) {
            return null;
        }
        return digits.substring(0, 3) + "-" + digits.substring(3, 6) + "-" + digits.substring(6);
    }

    /**
     * Checks the email address format, for example name@northeastern.edu.
     *
     * @param email the email as typed
     * @return an error message, or {@code null} if valid
     */
    public static String checkEmail(String email) {
        if (email == null || email.isBlank()) {
            return "Please enter your email.";
        }
        if (!email.trim().matches("[\\w.+\\-]+@[\\w\\-]+(\\.[\\w\\-]+)+")) {
            return "Please enter a valid email, e.g. name@northeastern.edu.";
        }
        if (email.trim().length() > 100) {
            return "Email must be 100 characters or fewer.";
        }
        return null;
    }

    /**
     * Checks the experience: optional, at most 500 characters.
     *
     * @param experience the experience as typed
     * @return an error message, or {@code null} if valid
     */
    public static String checkExperience(String experience) {
        if (experience != null && experience.trim().length() > 500) {
            return "Experience must be 500 characters or fewer.";
        }
        return null;
    }

    /**
     * Checks the hobbies: optional, at most 200 characters.
     *
     * @param hobby the hobbies as typed
     * @return an error message, or {@code null} if valid
     */
    public static String checkHobby(String hobby) {
        if (hobby != null && hobby.trim().length() > 200) {
            return "Hobbies must be 200 characters or fewer.";
        }
        return null;
    }
}
