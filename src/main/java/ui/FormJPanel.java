package ui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.FlowLayout;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import model.User;
import utility.DatabaseConnector;

/**
 * Create screen: validates the input, adds the student into the database,
 * shows a welcome popup with the photo, and then navigates to the
 * {@link ViewJPanel}.
 *
 * @author Shirabthinath Shivaji
 * @version 2.0
 */
public class FormJPanel extends JPanel {

    /** The main window's bottom panel, used to navigate to the view screen. */
    private final JPanel bottomPanel;

    /** The input fields. */
    private final StudentDetailsPanel detailsPanel = new StudentDetailsPanel();

    /**
     * Creates the form.
     *
     * @param bottomPanel the main window's bottom panel (CardLayout)
     */
    public FormJPanel(JPanel bottomPanel) {
        this.bottomPanel = bottomPanel;
        initComponents();
    }

    /** Builds and lays out the components. */
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Theme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        add(Theme.title("Add Student"), BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout());
        center.setOpaque(false);
        center.add(detailsPanel, BorderLayout.NORTH);
        JScrollPane scroll = new JScrollPane(center);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        center.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setOpaque(false);
        scroll.setOpaque(false);
        add(scroll, BorderLayout.CENTER);

        JButton clearButton = Theme.button("Clear");
        JButton submitButton = Theme.button("Submit");
        clearButton.addActionListener(evt -> detailsPanel.clearAll());
        submitButton.addActionListener(this::submitButtonActionPerformed);

        JLabel hint = new JLabel("<html><font color='#C8102E'>*</font> required field</html>");
        JPanel south = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        south.setOpaque(false);
        south.add(clearButton);
        south.add(submitButton);
        south.add(hint);
        add(south, BorderLayout.SOUTH);
    }

    /**
     * Validates the form, adds the student into the database, shows a popup
     * and navigates to the view screen.
     *
     * @param evt the button click
     */
    private void submitButtonActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            User userObject = detailsPanel.readUser();

            // All validations have passed: add the userObject into the database
            DatabaseConnector.addUser(userObject);
            System.out.println("Added user into the database");

            // Popup: "Welcome <name> to <college>" with the photo, if any
            JOptionPane.showMessageDialog(this, userObject, "Success!",
                    JOptionPane.INFORMATION_MESSAGE,
                    StudentDetailsPanel.loadPhoto(userObject.getPhotoPath(), 90));

            // Navigate to the View J Panel
            ViewJPanel viewJPanelObject = new ViewJPanel();
            this.bottomPanel.add(viewJPanelObject);
            CardLayout layout = (CardLayout) this.bottomPanel.getLayout();
            layout.next(bottomPanel);

        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Oops!", JOptionPane.WARNING_MESSAGE);
        } catch (SQLIntegrityConstraintViolationException e) {
            JOptionPane.showMessageDialog(this,
                    "This email is already registered. Please use a different email.",
                    "Duplicate Email", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Could not save to the database:\n" + e.getMessage(),
                    "Failed!", JOptionPane.ERROR_MESSAGE);
        }
    }
}
