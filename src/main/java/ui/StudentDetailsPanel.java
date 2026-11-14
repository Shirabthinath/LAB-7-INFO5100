package ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.io.File;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileNameExtensionFilter;
import model.User;
import utility.UserValidator;

/**
 * All the input fields for one student, used by both the create screen
 * ({@link FormJPanel}) and the view screen ({@link ViewJPanel}), so the two
 * screens always show the same fields and use the same validation.
 *
 * @author Shirabthinath Shivaji
 * @version 2.0
 */
public class StudentDetailsPanel extends JPanel {

    /** Size in pixels of the photo preview. */
    private static final int PREVIEW_SIZE = 120;

    /** Placeholder shown first in the college drop-down. */
    private static final String SELECT_COLLEGE = "-- Select college --";

    /** Placeholder shown first in the continent drop-down. */
    private static final String SELECT_CONTINENT = "-- Select continent --";

    /** First name input. */
    private final JTextField firstNameTextField = new JTextField(20);

    /** Last name input. */
    private final JTextField lastNameTextField = new JTextField(20);

    /** Gender choice: male. */
    private final JRadioButton maleRadioButton = new JRadioButton("Male");

    /** Gender choice: female. */
    private final JRadioButton femaleRadioButton = new JRadioButton("Female");

    /** Gender choice: other. */
    private final JRadioButton otherRadioButton = new JRadioButton("Other");

    /** Makes the gender choices mutually exclusive. */
    private final ButtonGroup genderButtonGroup = new ButtonGroup();

    /** Date of birth picker (month/day/year). */
    private final JSpinner dobSpinner;

    /** Shows the age calculated from the date of birth. */
    private final JLabel ageValueLabel = new JLabel();

    /** Phone input. */
    private final JTextField phoneTextField = new JTextField(20);

    /** Email input. */
    private final JTextField emailTextField = new JTextField(20);

    /** College choice. */
    private final JComboBox<String> collegeComboBox = new JComboBox<>();

    /** Continent choice. */
    private final JComboBox<String> continentComboBox = new JComboBox<>();

    /** Experience input (optional). */
    private final JTextArea experienceTextArea = new JTextArea(3, 20);

    /** Hobbies input (optional). */
    private final JTextArea hobbyTextArea = new JTextArea(3, 20);

    /** Opens a file chooser to pick a photo. */
    private final JButton browseButton = new JButton("Upload Photo...");

    /** Removes the chosen photo. */
    private final JButton removePhotoButton = new JButton("Remove");

    /** Shows the chosen photo. */
    private final JLabel photoPreviewLabel = new JLabel("No photo", SwingConstants.CENTER);

    /** Path of the chosen photo, or {@code null} when there is none. */
    private String photoPath;

    /** Creates the fields. */
    public StudentDetailsPanel() {
        Calendar cal = Calendar.getInstance();
        Date today = cal.getTime();
        cal.add(Calendar.YEAR, -20);
        Date initial = cal.getTime();
        cal.set(1920, Calendar.JANUARY, 1);
        Date earliest = cal.getTime();
        dobSpinner = new JSpinner(new SpinnerDateModel(initial, earliest, today, Calendar.DAY_OF_MONTH));
        dobSpinner.setEditor(new JSpinner.DateEditor(dobSpinner, "MM/dd/yyyy"));
        dobSpinner.addChangeListener(e -> updateAgeLabel());
        initComponents();
        updateAgeLabel();
    }

    /** Builds and lays out the components. */
    private void initComponents() {
        setLayout(new GridBagLayout());
        setOpaque(false);

        genderButtonGroup.add(maleRadioButton);
        genderButtonGroup.add(femaleRadioButton);
        genderButtonGroup.add(otherRadioButton);
        JPanel genderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        genderPanel.setOpaque(false);
        for (JRadioButton r : new JRadioButton[]{maleRadioButton, femaleRadioButton, otherRadioButton}) {
            r.setOpaque(false);
            genderPanel.add(r);
        }

        JPanel dobPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        dobPanel.setOpaque(false);
        dobPanel.add(dobSpinner);
        dobPanel.add(new JLabel("    Age: "));
        dobPanel.add(ageValueLabel);

        collegeComboBox.addItem(SELECT_COLLEGE);
        for (String c : Colleges.ALL) {
            collegeComboBox.addItem(c);
        }

        continentComboBox.addItem(SELECT_CONTINENT);
        for (String c : Continents.ALL) {
            continentComboBox.addItem(c);
        }

        hobbyTextArea.setLineWrap(true);
        hobbyTextArea.setWrapStyleWord(true);
        experienceTextArea.setLineWrap(true);
        experienceTextArea.setWrapStyleWord(true);

        phoneTextField.setToolTipText("10 digits, e.g. 617-555-0142");
        emailTextField.setToolTipText("e.g. name@northeastern.edu");

        int row = 0;
        addRow(row++, "First Name", true, firstNameTextField);
        addRow(row++, "Last Name", true, lastNameTextField);
        addRow(row++, "Gender", true, genderPanel);
        addRow(row++, "Date of Birth", true, dobPanel);
        addRow(row++, "Phone", true, phoneTextField);
        addRow(row++, "Email", true, emailTextField);
        addRow(row++, "College", true, collegeComboBox);
        addRow(row++, "Continent", true, continentComboBox);
        addRow(row++, "Experience", false, new JScrollPane(experienceTextArea));
        addRow(row++, "Hobbies", false, new JScrollPane(hobbyTextArea));

        // Photo column on the right
        photoPreviewLabel.setPreferredSize(new Dimension(PREVIEW_SIZE, PREVIEW_SIZE));
        photoPreviewLabel.setBorder(BorderFactory.createLineBorder(java.awt.Color.GRAY));
        browseButton.addActionListener(e -> choosePhoto());
        removePhotoButton.addActionListener(e -> setPhoto(null));
        JPanel photoButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        photoButtons.setOpaque(false);
        photoButtons.add(browseButton);
        photoButtons.add(removePhotoButton);
        JPanel photoPanel = new JPanel(new BorderLayout(0, 6));
        photoPanel.setOpaque(false);
        photoPanel.add(new JLabel("Photo (optional)", SwingConstants.CENTER), BorderLayout.NORTH);
        photoPanel.add(photoPreviewLabel, BorderLayout.CENTER);
        photoPanel.add(photoButtons, BorderLayout.SOUTH);

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 2;
        c.gridy = 0;
        c.gridheight = 5;
        c.anchor = GridBagConstraints.NORTH;
        c.insets = new Insets(5, 30, 5, 5);
        add(photoPanel, c);
    }

    /**
     * Adds a label and its input on one row.
     *
     * @param row       the grid row
     * @param label     the label text
     * @param required  whether to show a red star
     * @param component the input component
     */
    private void addRow(int row, String label, boolean required, JComponent component) {
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);
        c.gridy = row;
        c.gridx = 0;
        c.anchor = GridBagConstraints.FIRST_LINE_END;
        String text = required
                ? "<html>" + label + " <font color='#C8102E'>*</font></html>"
                : label;
        add(new JLabel(text), c);
        c.gridx = 1;
        c.anchor = GridBagConstraints.LINE_START;
        c.fill = GridBagConstraints.HORIZONTAL;
        add(component, c);
    }

    /** Recalculates the age shown next to the date of birth. */
    private void updateAgeLabel() {
        LocalDate dob = getDateOfBirth();
        int age = java.time.Period.between(dob, LocalDate.now()).getYears();
        ageValueLabel.setText(String.valueOf(age));
    }

    /** @return the date in the date of birth picker */
    private LocalDate getDateOfBirth() {
        Date d = (Date) dobSpinner.getValue();
        return d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    /** Lets the user pick an image file and shows it in the preview. */
    private void choosePhoto() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Choose a profile photo");
        chooser.setFileFilter(new FileNameExtensionFilter("Images (jpg, jpeg, png, gif)",
                "jpg", "jpeg", "png", "gif"));
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            setPhoto(chooser.getSelectedFile().getAbsolutePath());
        }
    }

    /**
     * Sets the photo and updates the preview.
     *
     * @param path the image path, or {@code null} for no photo
     */
    private void setPhoto(String path) {
        ImageIcon icon = loadPhoto(path, PREVIEW_SIZE);
        if (icon == null) {
            photoPath = null;
            photoPreviewLabel.setIcon(null);
            photoPreviewLabel.setText(path == null || path.isBlank() ? "No photo" : "Photo not found");
        } else {
            photoPath = path;
            photoPreviewLabel.setIcon(icon);
            photoPreviewLabel.setText(null);
        }
    }

    /**
     * Loads an image scaled to fit a square.
     *
     * @param path the image path; may be {@code null}
     * @param size the width and height in pixels
     * @return the scaled image, or {@code null} if there is no readable file
     */
    public static ImageIcon loadPhoto(String path, int size) {
        if (path == null || path.isBlank() || !new File(path).isFile()) {
            return null;
        }
        ImageIcon original = new ImageIcon(path);
        if (original.getIconWidth() <= 0) {
            return null;
        }
        double scale = Math.min((double) size / original.getIconWidth(),
                (double) size / original.getIconHeight());
        int w = Math.max(1, (int) (original.getIconWidth() * scale));
        int h = Math.max(1, (int) (original.getIconHeight() * scale));
        return new ImageIcon(original.getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH));
    }

    /**
     * Validates the fields and builds a student from them.
     *
     * @return the student described by the fields (userId 0)
     * @throws IllegalArgumentException with a user-friendly message if a
     *                                  value is invalid
     */
    public User readUser() {
        String first = firstNameTextField.getText().trim();
        String last = lastNameTextField.getText().trim();
        LocalDate dob = getDateOfBirth();
        String phone = UserValidator.formatPhone(phoneTextField.getText());
        String email = emailTextField.getText().trim();
        String hobby = hobbyTextArea.getText().trim();
        String experience = experienceTextArea.getText().trim();

        String error = UserValidator.checkName("First name", first);
        if (error == null) {
            error = UserValidator.checkName("Last name", last);
        }
        if (error == null && genderButtonGroup.getSelection() == null) {
            error = "Please select your gender.";
        }
        if (error == null) {
            error = UserValidator.checkDateOfBirth(dob);
        }
        if (error == null && phone == null) {
            error = "Please enter a 10-digit phone number, e.g. 617-555-0142.";
        }
        if (error == null) {
            error = UserValidator.checkEmail(email);
        }
        if (error == null && collegeComboBox.getSelectedIndex() <= 0) {
            error = "Please select your college.";
        }
        if (error == null && continentComboBox.getSelectedIndex() <= 0) {
            error = "Please select your continent.";
        }
        if (error == null) {
            error = UserValidator.checkExperience(experience);
        }
        if (error == null) {
            error = UserValidator.checkHobby(hobby);
        }
        if (error != null) {
            throw new IllegalArgumentException(error);
        }

        User u = new User();
        u.setFirstName(first);
        u.setLastName(last);
        u.setGender(maleRadioButton.isSelected() ? "Male"
                : femaleRadioButton.isSelected() ? "Female" : "Other");
        u.setDateOfBirth(dob);
        u.setPhone(phone);
        u.setEmail(email.toLowerCase());
        u.setCollege((String) collegeComboBox.getSelectedItem());
        u.setContinent((String) continentComboBox.getSelectedItem());
        u.setExperience(experience);
        u.setHobby(hobby);
        u.setPhotoPath(photoPath);
        return u;
    }

    /**
     * Shows a student's values in the fields.
     *
     * @param u the student to show
     */
    public void showUser(User u) {
        firstNameTextField.setText(u.getFirstName());
        lastNameTextField.setText(u.getLastName());
        genderButtonGroup.clearSelection();
        if ("Male".equals(u.getGender())) {
            maleRadioButton.setSelected(true);
        } else if ("Female".equals(u.getGender())) {
            femaleRadioButton.setSelected(true);
        } else if ("Other".equals(u.getGender())) {
            otherRadioButton.setSelected(true);
        }
        dobSpinner.setValue(Date.from(u.getDateOfBirth().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        phoneTextField.setText(u.getPhone());
        emailTextField.setText(u.getEmail());
        collegeComboBox.setSelectedItem(u.getCollege());
        if (u.getContinent() == null || u.getContinent().isBlank()) {
            continentComboBox.setSelectedIndex(0);
        } else {
            continentComboBox.setSelectedItem(u.getContinent());
        }
        experienceTextArea.setText(u.getExperience());
        hobbyTextArea.setText(u.getHobby());
        setPhoto(u.getPhotoPath());
    }

    /** Empties every field. */
    public void clearAll() {
        firstNameTextField.setText("");
        lastNameTextField.setText("");
        genderButtonGroup.clearSelection();
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, -20);
        dobSpinner.setValue(cal.getTime());
        phoneTextField.setText("");
        emailTextField.setText("");
        collegeComboBox.setSelectedIndex(0);
        continentComboBox.setSelectedIndex(0);
        experienceTextArea.setText("");
        hobbyTextArea.setText("");
        setPhoto(null);
    }

    /**
     * Enables or disables every input.
     *
     * @param enabled {@code true} to allow editing
     */
    public void setAllEnabled(boolean enabled) {
        JComponent[] all = {firstNameTextField, lastNameTextField, maleRadioButton, femaleRadioButton,
            otherRadioButton, dobSpinner, phoneTextField, emailTextField, collegeComboBox,
            continentComboBox, experienceTextArea, hobbyTextArea, browseButton, removePhotoButton};
        for (JComponent comp : all) {
            comp.setEnabled(enabled);
        }
    }

    /** Moves the keyboard focus to the first name. */
    public void focusFirstField() {
        firstNameTextField.requestFocusInWindow();
    }
}
