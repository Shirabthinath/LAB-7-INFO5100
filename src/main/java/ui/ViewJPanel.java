package ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import model.User;
import utility.DatabaseConnector;

/**
 * View screen: shows every student from the database in a {@link JTable}.
 * <p>
 * Selecting a row shows the student's details (read-only) below the table.
 * <b>Edit</b> unlocks the fields, <b>Submit</b> saves the changes,
 * <b>Cancel</b> discards them, and <b>Delete</b> removes the student.
 *
 * @author Shirabthinath Shivaji
 * @version 2.0
 */
public class ViewJPanel extends JPanel {

    /** The student currently selected in the table. */
    private User userObject;

    /** The students loaded from the database, in table row order. */
    private ArrayList<User> users = new ArrayList<>();

    /** Table model; cells are read-only, editing happens in the fields. */
    private final DefaultTableModel tableModel = new DefaultTableModel(
            new String[]{"ID", "First Name", "Last Name", "Gender", "Age", "Phone", "Email", "College", "Continent"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    /** The table of students. */
    private final JTable userJTable = new JTable(tableModel);

    /** Detail fields for the selected student. */
    private final StudentDetailsPanel detailsPanel = new StudentDetailsPanel();

    /** Unlocks the fields for editing. */
    private final JButton editButton = Theme.button("Edit");

    /** Saves the edited values to the database. */
    private final JButton submitEditButton = Theme.button("Submit");

    /** Discards the edits. */
    private final JButton cancelButton = Theme.button("Cancel");

    /** Deletes the selected student. */
    private final JButton deleteButton = Theme.button("Delete");

    /** Creates new form ViewJPanel and loads the students from the database. */
    public ViewJPanel() {
        initComponents();
        populateTable();
    }

    /** Builds and lays out the components. */
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Theme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(16, 24, 16, 24));

        add(Theme.title("View Students"), BorderLayout.NORTH);

        userJTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        userJTable.setRowHeight(24);
        userJTable.getTableHeader().setReorderingAllowed(false);
        int[] widths = {40, 110, 100, 65, 45, 120, 220, 230, 110};
        for (int i = 0; i < widths.length; i++) {
            userJTable.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }
        userJTable.getColumnModel().getColumn(0).setMaxWidth(50);
        userJTable.getColumnModel().getColumn(4).setMaxWidth(50);
        userJTable.getSelectionModel().addListSelectionListener(evt -> {
            if (!evt.getValueIsAdjusting()) {
                userJTableRowSelected();
            }
        });
        JScrollPane tableScroll = new JScrollPane(userJTable);
        tableScroll.setPreferredSize(new Dimension(880, 150));
        add(tableScroll, BorderLayout.CENTER);

        editButton.addActionListener(this::editButtonActionPerformed);
        submitEditButton.addActionListener(this::submitEditButtonActionPerformed);
        cancelButton.addActionListener(evt -> cancelEditing());
        deleteButton.addActionListener(this::deleteButtonActionPerformed);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.add(editButton);
        buttonPanel.add(submitEditButton);
        buttonPanel.add(cancelButton);
        buttonPanel.add(deleteButton);

        JPanel southPanel = new JPanel(new BorderLayout(0, 8));
        southPanel.setOpaque(false);
        southPanel.add(detailsPanel, BorderLayout.WEST);
        southPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(southPanel, BorderLayout.SOUTH);
    }

    /** Gets the students from the database and shows them in the JTable. */
    public void populateTable() {
        // getting the data from the database
        try {
            this.users = DatabaseConnector.getUsers();
        } catch (SQLException e) {
            this.users = new ArrayList<>();
            JOptionPane.showMessageDialog(this, "Could not load students:\n" + e.getMessage(),
                    "Failed!", JOptionPane.ERROR_MESSAGE);
        }

        // display the students in the JTable
        DefaultTableModel model = (DefaultTableModel) this.userJTable.getModel();
        model.setRowCount(0);
        for (User u1 : users) {
            model.addRow(new Object[]{
                u1.getUserId(), u1.getFirstName(), u1.getLastName(), u1.getGender(), u1.getAge(),
                u1.getPhone(), u1.getEmail(), u1.getCollege(), u1.getContinent()
            });
        }

        // nothing is selected after a refresh
        this.userObject = null;
        detailsPanel.clearAll();
        setViewMode(false);
    }

    /**
     * Switches the buttons and fields between viewing and editing.
     *
     * @param editing {@code true} while the selected student is being edited
     */
    private void setViewMode(boolean editing) {
        boolean selected = userObject != null;
        detailsPanel.setAllEnabled(editing);
        userJTable.setEnabled(!editing);
        editButton.setEnabled(selected && !editing);
        deleteButton.setEnabled(selected && !editing);
        submitEditButton.setEnabled(editing);
        cancelButton.setEnabled(editing);
    }

    /** When a row is selected, shows that student's details (read-only). */
    private void userJTableRowSelected() {
        int selectedRowIndex = this.userJTable.getSelectedRow();
        if (selectedRowIndex < 0) {
            return;
        }
        this.userObject = this.users.get(selectedRowIndex);
        detailsPanel.showUser(userObject);
        setViewMode(false);
    }

    /**
     * Unlocks the fields so the selected student can be changed.
     *
     * @param evt the button click
     */
    private void editButtonActionPerformed(java.awt.event.ActionEvent evt) {
        if (userObject == null) {
            JOptionPane.showMessageDialog(this, "Please select a row to edit.", "Oops!",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        setViewMode(true);
        detailsPanel.focusFirstField();
    }

    /** Discards the edits and shows the saved values again. */
    private void cancelEditing() {
        if (userObject != null) {
            detailsPanel.showUser(userObject);
        }
        setViewMode(false);
    }

    /**
     * Validates the edited values and updates the student in the database.
     *
     * @param evt the button click
     */
    private void submitEditButtonActionPerformed(java.awt.event.ActionEvent evt) {
        if (userObject == null) {
            return;
        }
        try {
            // 1. read and validate the values from the fields
            User updatedUser = detailsPanel.readUser();

            // 2. call the update user method from the database connector
            DatabaseConnector.updateUser(userObject, updatedUser);
            System.out.println("The record has been updated.");
            JOptionPane.showMessageDialog(this, "Record updated!", "Success!",
                    JOptionPane.INFORMATION_MESSAGE);

            // repopulate the table
            populateTable();
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Oops!", JOptionPane.WARNING_MESSAGE);
        } catch (SQLIntegrityConstraintViolationException e) {
            JOptionPane.showMessageDialog(this, "Another student already uses this email.",
                    "Duplicate Email", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Unable to update:\n" + e.getMessage(), "Failed!",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Deletes the selected student after asking for confirmation.
     *
     * @param evt the button click
     */
    private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {
        if (userObject == null) {
            JOptionPane.showMessageDialog(this, "Please select a row to delete.", "Oops!",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        int choice = JOptionPane.showConfirmDialog(this,
                "Delete " + userObject.getFirstName() + " " + userObject.getLastName()
                + " (ID " + userObject.getUserId() + ")?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (choice != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            // call the delete method from the Database Connector
            DatabaseConnector.deleteUser(userObject);
            System.out.println("User deleted successfully.");
            JOptionPane.showMessageDialog(this, "Record deleted!", "Success!",
                    JOptionPane.INFORMATION_MESSAGE);
            // repopulate the table
            populateTable();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Unable to delete:\n" + e.getMessage(), "Failed!",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
