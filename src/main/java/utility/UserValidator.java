package utility;

/**
 * Validation rules shared by the create and edit screens.
 *
 * @author Shirabthinath Shivaji
 * @version 1.0
 */
public final class UserValidator {

    /** Not meant to be instantiated. */
    private UserValidator() {
    }

    /**
     * Checks the name: required, letters/spaces/apostrophes/hyphens, and at
     * most 20 characters (the size of the first_name column).
     *
     * @param name the name as typed
     * @return an error message, or {@code null} if valid
     */
    public static String checkName(String name) {
        if (name == null || name.isBlank()) {
            return "Please enter your name.";
        }
        if (!name.trim().matches("[A-Za-z][A-Za-z '\\-]*")) {
            return "Name can contain only letters, spaces, apostrophes and hyphens.";
        }
        if (name.trim().length() > 20) {
            return "Name must be 20 characters or fewer.";
        }
        return null;
    }

    /**
     * Parses and checks the age.
     *
     * @param ageText the age as typed
     * @return the age
     * @throws NumberFormatException if the text is not a whole number
     *                               between 1 and 120
     */
    public static int parseAge(String ageText) {
        int age = Integer.parseInt(ageText.trim());
        if (age < 1 || age > 120) {
            throw new NumberFormatException("Age out of range: " + age);
        }
        return age;
    }

    /**
     * Checks the hobbies: at most 100 characters (the size of the column).
     *
     * @param hobby the hobbies as typed
     * @return an error message, or {@code null} if valid
     */
    public static String checkHobby(String hobby) {
        if (hobby != null && hobby.trim().length() > 100) {
            return "Hobbies must be 100 characters or fewer.";
        }
        return null;
    }
}
