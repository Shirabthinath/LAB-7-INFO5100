package ui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.sql.SQLException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;
import utility.DatabaseConnector;

/**
 * The main window: a top panel with navigation buttons and a bottom panel
 * (CardLayout) that shows either the {@link FormJPanel} or the
 * {@link ViewJPanel}.
 *
 * @author Shirabthinath Shivaji
 * @version 1.0
 */
public class MainJFrame extends JFrame {

    /** Holds the navigation buttons. */
    private final JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));

    /** Shows the current screen; uses a CardLayout. */
    private final JPanel bottomPanel = new JPanel(new CardLayout());

    /** Creates the main window and shows the create form. */
    public MainJFrame() {
        super("Lab 7 - JDBC CRUD Application");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JButton formButton = new JButton("Create User");
        JButton viewButton = new JButton("View Users");
        formButton.addActionListener(evt -> showPanel(new FormJPanel(bottomPanel)));
        viewButton.addActionListener(evt -> showPanel(new ViewJPanel()));
        topPanel.add(formButton);
        topPanel.add(viewButton);
        topPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, java.awt.Color.LIGHT_GRAY));

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(topPanel, BorderLayout.NORTH);
        getContentPane().add(bottomPanel, BorderLayout.CENTER);

        showPanel(new FormJPanel(bottomPanel));
        setMinimumSize(new Dimension(850, 620));
        setLocationRelativeTo(null);
    }

    /**
     * Replaces the screen in the bottom panel.
     *
     * @param panel the screen to show
     */
    private void showPanel(JPanel panel) {
        bottomPanel.removeAll();
        bottomPanel.add(panel);
        bottomPanel.revalidate();
        bottomPanel.repaint();
    }

    /**
     * Starts the application: connects to MySQL, creates the student table
     * if needed, and opens the window.
     *
     * @param args the command line arguments (not used)
     */
    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> {
            try {
                for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                    if ("Nimbus".equals(info.getName())) {
                        UIManager.setLookAndFeel(info.getClassName());
                        break;
                    }
                }
            } catch (Exception ex) {
                // keep the default look and feel
            }

            try {
                DatabaseConnector.createTable();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null,
                        "Could not connect to the MySQL database.\n\n"
                        + "Make sure Docker Desktop is open and the mysql-db container is running.\n\n"
                        + e.getMessage(),
                        "Database Connection Failed", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
            new MainJFrame().setVisible(true);
        });
    }
}
