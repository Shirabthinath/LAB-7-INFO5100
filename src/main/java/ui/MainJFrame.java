package ui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.sql.SQLException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;
import utility.DatabaseConnector;

/**
 * The main window: a header (top panel) with the navigation buttons and a
 * bottom panel (CardLayout) that shows either the {@link FormJPanel} or the
 * {@link ViewJPanel}.
 *
 * @author Shirabthinath Shivaji
 * @version 2.0
 */
public class MainJFrame extends JFrame {

    /** Header with the title and the navigation buttons. */
    private final JPanel topPanel = new JPanel(new BorderLayout());

    /** Shows the current screen; uses a CardLayout. */
    private final JPanel bottomPanel = new JPanel(new CardLayout());

    /** Creates the main window and shows the create form. */
    public MainJFrame() {
        super("Lab 7 - Student Records (JDBC + MySQL)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel titleLabel = new JLabel("Student Records");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 20f));

        JButton formButton = Theme.button("Add Student");
        JButton viewButton = Theme.button("View Students");
        formButton.addActionListener(evt -> showPanel(new FormJPanel(bottomPanel)));
        viewButton.addActionListener(evt -> showPanel(new ViewJPanel()));
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        navPanel.setOpaque(false);
        navPanel.add(formButton);
        navPanel.add(viewButton);

        topPanel.setBackground(Theme.HEADER);
        topPanel.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        topPanel.add(titleLabel, BorderLayout.WEST);
        topPanel.add(navPanel, BorderLayout.EAST);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(topPanel, BorderLayout.NORTH);
        getContentPane().add(bottomPanel, BorderLayout.CENTER);

        showPanel(new FormJPanel(bottomPanel));
        setMinimumSize(new Dimension(1000, 780));
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
     * Starts the application: connects to MySQL, creates the table if
     * needed, and opens the window.
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
