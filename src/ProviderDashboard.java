import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class ProviderDashboard extends JFrame {
    private String providerEmail;
    private JTable bookingsTable;
    private JButton markCompletedButton;

    public ProviderDashboard(String email) {
        this.providerEmail = email;

        setTitle("Service Provider Dashboard");
        setSize(700, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JLabel welcomeLabel = new JLabel("Welcome, " + email, SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        add(welcomeLabel, BorderLayout.NORTH);

        // Table for bookings
        bookingsTable = new JTable();
        loadBookings();
        add(new JScrollPane(bookingsTable), BorderLayout.CENTER);

        // Mark Completed Button
        markCompletedButton = new JButton("Mark Selected Booking Completed");
        add(markCompletedButton, BorderLayout.SOUTH);

        markCompletedButton.addActionListener(e -> markCompleted());

        setVisible(true);
    }

    private void loadBookings() {
        try {
            Connection conn = DBConnection.getConnection();

            // CORRECTED: Filter bookings by the logged-in provider's email
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT id, customer_email, provider_email, service_type, status FROM bookings WHERE provider_email = ?"
            );
            ps.setString(1, this.providerEmail); // Filter using the instance variable

            ResultSet rs = ps.executeQuery();

            DefaultTableModel model = new DefaultTableModel(
                    new String[]{"ID","Customer Email","Provider Email","Service","Status"},0);

            while(rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("customer_email"),
                        rs.getString("provider_email"),
                        rs.getString("service_type"),
                        rs.getString("status")
                });
            }
            bookingsTable.setModel(model);

        } catch(Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading bookings: " + ex.getMessage());
        }
    }

    private void markCompleted() {
        int selectedRow = bookingsTable.getSelectedRow();
        if(selectedRow == -1) {
            JOptionPane.showMessageDialog(this,"Please select a booking!");
            return;
        }

        int bookingId = (int) bookingsTable.getValueAt(selectedRow, 0);

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(
                    "UPDATE bookings SET status=? WHERE id=?"
            );
            ps.setString(1, "Completed");
            ps.setInt(2, bookingId);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this,"Booking marked as completed!");
            loadBookings(); // refresh table
        } catch(SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,"Error: "+e.getMessage());
        }
    }

    // Test standalone
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ProviderDashboard("provider@example.com"));
    }
}