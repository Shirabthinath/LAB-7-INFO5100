package ui;

import java.awt.CardLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.SQLException;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import model.User;
import utility.DatabaseConnector;
import utility.UserValidator;

/**
 * Create screen: a form that validates the input, adds the user into the
 * database, and then navigates to the {@link ViewJPanel}.
 *
 * @author Shirabthinath Shivaji
 * @version 1.0
 */
public class FormJPanel extends JPanel {

    /** The main window's bottom panel, used to navigate to the view screen. */
    private final JPanel bottomPanel;

    /** Name input. */
    private final JTextField nameTextField = new JTextField(22);

    /** Age input. */
    private final JTextField ageTextField = new JTextField(22);

    /** One radio button per college; only one can be selected. */
    private final JRadioButton[] collegeRadioButtons = new JRadioButton[Colleges.ALL.length];

    /** Groups the college radio buttons. */
    private final ButtonGroup collegeButtonGroup = new ButtonGroup();

    /** Hobbies input. */
    private final JTextArea hobbyTextArea = new JTextArea(4, 22);

    /**
     * Creates the form.
     *
     * @param bottomPanel the main window's bottom panel (CardLayout)
     */
    public FormJPanel(JPanel bottomPanel) {
        this.bottomPanel = bottomPanel;
        initComponents();
    }

    /** Builds and lays out the form components. */
    private void initComponents() {
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JLabel titleLabel = new JLabel("Create User");
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 22f));

        JPanel collegePanel = new JPanel(new GridBagLayout());
        GridBagConstraints rc = new GridBagConstraints();
        rc.anchor = GridBagConstraints.LINE_START;
        rc.gridx = 0;
        for (int i = 0; i < Colleges.ALL.length; i++) {
            collegeRadioButtons[i] = new JRadioButton(Colleges.ALL[i]);
            collegeRadioButtons[i].setActionCommand(Colleges.ALL[i]);
            collegeButtonGroup.add(collegeRadioButtons[i]);
            rc.gridy = i;
            collegePanel.add(collegeRadioButtons[i], rc);
        }

        hobbyTextArea.setLineWrap(true);
        hobbyTextArea.setWrapStyleWord(true);

        JButton submitButton = new JButton("Submit");
        submitButton.addActionListener(this::submitButtonActionPerformed);

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(8, 8, 8, 8);
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        c.anchor = GridBagConstraints.LINE_START;
        add(titleLabel, c);
        c.gridwidth = 1;

        addRow(1, "Name:", nameTextField);
        addRow(2, "Age:", ageTextField);
        addRow(3, "College:", collegePanel);
        addRow(4, "Hobbies:", new JScrollPane(hobbyTextArea));

        c.gridx = 1;
        c.gridy = 5;
        add(submitButton, c);

        // push everything to the top-left
        c.gridx = 2;
        c.gridy = 6;
        c.weightx = 1;
        c.weighty = 1;
        add(new JLabel(), c);
    }

    /**
     * Adds a label and component on one row.
     *
     * @param row       the grid row
     * @param label     the label text
     * @param component the input component
     */
    private void addRow(int row, String label, JComponent component) {
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(8, 8, 8, 8);
        c.gridy = row;
        c.gridx = 0;
        c.anchor = GridBagConstraints.FIRST_LINE_END;
        add(new JLabel(label), c);
        c.gridx = 1;
        c.anchor = GridBagConstraints.LINE_START;
        c.fill = GridBagConstraints.HORIZONTAL;
        add(component, c);
    }

    /**
     * Validates the form, adds the user into the database, shows a popup and
     * navigates to the view screen.
     *
     * @param evt the button click
     */
    private void submitButtonActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            User userObject = new User();
            userObject.setName(this.nameTextField.getText().trim());
            userObject.setAge(UserValidator.parseAge(this.ageTextField.getText()));
            // getSelection() is null when no radio button is selected
            userObject.setCollege(this.collegeButtonGroup.getSelection().getActionCommand());
            userObject.setHobby(this.hobbyTextArea.getText().trim());

            String error = UserValidator.checkName(userObject.getName());
            if (error == null) {
                error = UserValidator.checkHobby(userObject.getHobby());
            }
            if (error != null) {
                JOptionPane.showMessageDialog(this, error, "Oops!", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // All validations have passed: add the userObject into the database
            DatabaseConnector.addUser(userObject);
            System.out.println("Added user into the database");

            // Popup: "Welcome <name> to <college>"
            JOptionPane.showMessageDialog(this, userObject, "Success!", JOptionPane.INFORMATION_MESSAGE);

            // Navigate to the View J Panel
            ViewJPanel viewJPanelObject = new ViewJPanel();
            this.bottomPanel.add(viewJPanelObject);
            CardLayout layout = (CardLayout) this.bottomPanel.getLayout();
            layout.next(bottomPanel);

        } catch (NullPointerException ne) {
            // when the user does not select a college radio button
            JOptionPane.showMessageDialog(this, "Please select your college.", "Oops!",
                    JOptionPane.WARNING_MESSAGE);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter your age correctly (1 to 120).", "Oops!",
                    JOptionPane.WARNING_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Could not save to the database:\n" + e.getMessage(),
                    "Failed!", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Oops, something went wrong!", "Failed!",
                    JOptionPane.ERROR_MESSAGE);
            System.out.println(e.getMessage());
        }
    }
}
