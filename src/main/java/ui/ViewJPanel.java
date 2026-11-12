package ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import model.User;
import utility.DatabaseConnector;
import utility.UserValidator;

/**
 * View screen: shows every user from the database in a {@link JTable}.
 * <p>
 * Selecting a row shows its details in the (disabled) fields. <b>Edit</b>
 * enables the fields, <b>Submit</b> saves the changes to the database, and
 * <b>Delete</b> removes the selected user.
 *
 * @author Shirabthinath Shivaji
 * @version 1.0
 */
public class ViewJPanel extends JPanel {

    /** The user currently selected in the table. */
    private User userObject;

    /** The users loaded from the database, in table row order. */
    private ArrayList<User> users = new ArrayList<>();

    /** Table model; cells are read-only, editing happens in the fields. */
    private final DefaultTableModel tableModel = new DefaultTableModel(
            new String[]{"ID", "Name", "College", "Age", "Hobbies"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    /** The table of users. */
    private final JTable userJTable = new JTable(tableModel);

    /** Name field. */
    private final JTextField nameTextField = new JTextField(22);

    /** College selection. */
    private final JComboBox<String> collegeComboBox = new JComboBox<>(Colleges.ALL);

    /** Age field. */
    private final JTextField ageTextField = new JTextField(22);

    /** Hobbies field. */
    private final JTextArea hobbyTextArea = new JTextArea(3, 22);

    /** Deletes the selected user. */
    private final JButton deleteButton = new JButton("Delete");

    /** Enables the fields for editing. */
    private final JButton editButton = new JButton("Edit");

    /** Saves the edited values to the database. */
    private final JButton submitEditButton = new JButton("Submit");

    /** Creates new form ViewJPanel and loads the users from the database. */
    public ViewJPanel() {
        initComponents();
        populateTable();
    }

    /** Builds and lays out the components. */
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JLabel titleLabel = new JLabel("View Users");
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 22f));
        add(titleLabel, BorderLayout.NORTH);

        userJTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        userJTable.setRowHeight(24);
        userJTable.getTableHeader().setReorderingAllowed(false);
        userJTable.getColumnModel().getColumn(0).setMaxWidth(50);
        userJTable.getColumnModel().getColumn(1).setPreferredWidth(110);
        userJTable.getColumnModel().getColumn(2).setPreferredWidth(270);
        userJTable.getColumnModel().getColumn(3).setMaxWidth(50);
        userJTable.getColumnModel().getColumn(4).setPreferredWidth(200);
        userJTable.getSelectionModel().addListSelectionListener(evt -> {
            if (!evt.getValueIsAdjusting()) {
                userJTableRowSelected();
            }
        });
        JScrollPane tableScroll = new JScrollPane(userJTable);
        tableScroll.setPreferredSize(new Dimension(700, 200));
        add(tableScroll, BorderLayout.CENTER);

        hobbyTextArea.setLineWrap(true);
        hobbyTextArea.setWrapStyleWord(true);

        JPanel fieldsPanel = new JPanel(new GridBagLayout());
        addRow(fieldsPanel, 0, "Name:", nameTextField);
        addRow(fieldsPanel, 1, "College:", collegeComboBox);
        addRow(fieldsPanel, 2, "Age:", ageTextField);
        addRow(fieldsPanel, 3, "Hobbies:", new JScrollPane(hobbyTextArea));

        deleteButton.addActionListener(this::deleteButtonActionPerformed);
        editButton.addActionListener(this::editButtonActionPerformed);
        submitEditButton.addActionListener(this::submitEditButtonActionPerformed);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(editButton);
        buttonPanel.add(submitEditButton);
        buttonPanel.add(deleteButton);

        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(fieldsPanel, BorderLayout.WEST);
        southPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(southPanel, BorderLayout.SOUTH);
    }

    /**
     * Adds a label and component on one row.
     *
     * @param panel     the panel to add to
     * @param row       the grid row
     * @param label     the label text
     * @param component the input component
     */
    private void addRow(JPanel panel, int row, String label, JComponent component) {
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);
        c.gridy = row;
        c.gridx = 0;
        c.anchor = GridBagConstraints.FIRST_LINE_END;
        panel.add(new JLabel(label), c);
        c.gridx = 1;
        c.anchor = GridBagConstraints.LINE_START;
        c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(component, c);
    }

    /** Shows the selected user's values in the fields. */
    public void setAllFields() {
        this.nameTextField.setText(userObject.getName());
        this.collegeComboBox.setSelectedItem(userObject.getCollege());
        this.ageTextField.setText(String.valueOf(userObject.getAge()));
        this.hobbyTextArea.setText(userObject.getHobby());
    }

    /** Empties all fields. */
    public void clearAllFields() {
        this.nameTextField.setText("");
        this.collegeComboBox.setSelectedIndex(-1);
        this.ageTextField.setText("");
        this.hobbyTextArea.setText("");
    }

    /** Gets the users from the database and shows them in the JTable. */
    public void populateTable() {
        // getting the data from the database
        try {
            this.users = DatabaseConnector.getUsers();
        } catch (SQLException e) {
            this.users = new ArrayList<>();
            JOptionPane.showMessageDialog(this, "Could not load users:\n" + e.getMessage(),
                    "Failed!", JOptionPane.ERROR_MESSAGE);
        }

        // display the users in the JTable
        DefaultTableModel model = (DefaultTableModel) this.userJTable.getModel();
        model.setRowCount(0);
        for (User u1 : users) {
            Object[] row = new Object[5];
            row[0] = u1.getUserId();
            row[1] = u1.getName();
            row[2] = u1.getCollege();
            row[3] = u1.getAge();
            row[4] = u1.getHobby();
            model.addRow(row);
        }

        // nothing is selected after a refresh
        this.userObject = null;
        clearAllFields();
        disableAll();
        this.editButton.setEnabled(false);
        this.deleteButton.setEnabled(false);
        this.submitEditButton.setEnabled(false);
    }

    /** Enables all fields for editing. */
    public void enableAll() {
        this.nameTextField.setEnabled(true);
        this.collegeComboBox.setEnabled(true);
        this.ageTextField.setEnabled(true);
        this.hobbyTextArea.setEnabled(true);
    }

    /** Disables all fields (read-only view). */
    public void disableAll() {
        this.nameTextField.setEnabled(false);
        this.collegeComboBox.setEnabled(false);
        this.ageTextField.setEnabled(false);
        this.hobbyTextArea.setEnabled(false);
    }

    /** When a row is selected, shows that user's details (read-only). */
    private void userJTableRowSelected() {
        int selectedRowIndex = this.userJTable.getSelectedRow();
        if (selectedRowIndex < 0) {
            return;
        }
        this.userObject = this.users.get(selectedRowIndex);
        setAllFields();
        disableAll();
        this.editButton.setEnabled(true);
        this.deleteButton.setEnabled(true);
        this.submitEditButton.setEnabled(false);
    }

    /**
     * Deletes the selected user after asking for confirmation.
     *
     * @param evt the button click
     */
    private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // which user to delete? find the row the user has clicked on
        int selectedRowIndex = this.userJTable.getSelectedRow();
        if (selectedRowIndex < 0) {
            JOptionPane.showMessageDialog(this, "Please select a row to delete.", "Oops!",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        this.userObject = this.users.get(selectedRowIndex);

        int choice = JOptionPane.showConfirmDialog(this,
                "Delete " + userObject.getName() + " (ID " + userObject.getUserId() + ")?",
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

    /**
     * Loads the selected user into the fields and enables them for editing.
     *
     * @param evt the button click
     */
    private void editButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // which user to edit? find the row the user has clicked on
        int selectedRowIndex = this.userJTable.getSelectedRow();
        if (selectedRowIndex < 0) {
            JOptionPane.showMessageDialog(this, "Please select a row to edit.", "Oops!",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        this.userObject = this.users.get(selectedRowIndex);

        // set the values in the text boxes, combo box, ...
        setAllFields();
        // make them all enabled
        enableAll();
        this.submitEditButton.setEnabled(true);
        this.editButton.setEnabled(false);
        this.nameTextField.requestFocusInWindow();
    }

    /**
     * Validates the edited values and updates the user in the database.
     *
     * @param evt the button click
     */
    private void submitEditButtonActionPerformed(java.awt.event.ActionEvent evt) {
        if (userObject == null) {
            return;
        }
        // 1. read the values from the text boxes, ...
        User updatedUser = new User();
        updatedUser.setName(this.nameTextField.getText().trim());
        updatedUser.setHobby(this.hobbyTextArea.getText().trim());
        Object college = this.collegeComboBox.getSelectedItem();
        updatedUser.setCollege(college == null ? null : college.toString());

        String error = UserValidator.checkName(updatedUser.getName());
        if (error == null && updatedUser.getCollege() == null) {
            error = "Please select your college.";
        }
        if (error == null) {
            error = UserValidator.checkHobby(updatedUser.getHobby());
        }
        if (error == null) {
            try {
                updatedUser.setAge(UserValidator.parseAge(this.ageTextField.getText()));
            } catch (NumberFormatException e) {
                error = "Please enter your age correctly (1 to 120).";
            }
        }
        if (error != null) {
            JOptionPane.showMessageDialog(this, error, "Oops!", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 2. call the update user method from the database connector
        try {
            DatabaseConnector.updateUser(userObject, updatedUser);
            System.out.println("The record has been updated.");
            JOptionPane.showMessageDialog(this, "Record updated!", "Success!",
                    JOptionPane.INFORMATION_MESSAGE);
            // repopulate the table
            populateTable();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Unable to update:\n" + e.getMessage(), "Failed!",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
