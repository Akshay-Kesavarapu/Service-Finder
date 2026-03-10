import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class CustomerDashboard extends JFrame {
    private String customerEmail;
    private JTable providerTable;
    private JButton bookButton;

    public CustomerDashboard(String email) {
        this.customerEmail = email;

        setTitle("Customer Dashboard");
        setSize(700, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10,10));

        JLabel welcomeLabel = new JLabel("Welcome, " + email, SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        add(welcomeLabel, BorderLayout.NORTH);

        // Table for providers
        providerTable = new JTable();
        loadProviders();
        add(new JScrollPane(providerTable), BorderLayout.CENTER);

        // Book Button
        bookButton = new JButton("Book Selected Service");
        add(bookButton, BorderLayout.SOUTH);

        // Action
        bookButton.addActionListener(e -> bookService());

        setVisible(true);
    }

    private void loadProviders() {
        try {
            Connection conn = DBConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT id, name, service_type, phone FROM providers");

            // Table model
            DefaultTableModel model = new DefaultTableModel(new String[]{"ID","Name","Service","Phone"},0);
            while(rs.next()) {
                model.addRow(new Object[]{rs.getInt("id"), rs.getString("name"),
                        rs.getString("service_type"), rs.getString("phone")});
            }
            providerTable.setModel(model);

        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    private void bookService() {
        int selectedRow = providerTable.getSelectedRow();
        if(selectedRow == -1) {
            JOptionPane.showMessageDialog(this,"Please select a provider!");
            return;
        }

        // Get data from the selected row (Note: provider email is needed, but not loaded in the current model. We'll use a new query to fetch it.)
        int providerId = (int) providerTable.getValueAt(selectedRow, 0);
        String providerName = (String) providerTable.getValueAt(selectedRow, 1);
        String serviceType = (String) providerTable.getValueAt(selectedRow, 2); // Service Type is already available

        String providerEmail = "";

        // STEP 1: Fetch the provider's email using their ID
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement emailPs = conn.prepareStatement("SELECT email FROM providers WHERE id = ?")) {

            emailPs.setInt(1, providerId);
            ResultSet rs = emailPs.executeQuery();
            if (rs.next()) {
                providerEmail = rs.getString("email");
            } else {
                JOptionPane.showMessageDialog(this, "Error: Could not find provider email.");
                return;
            }

            // STEP 2: Insert the booking into the 'bookings' table
            PreparedStatement insertPs = conn.prepareStatement(
                    "INSERT INTO bookings (customer_email, provider_email, service_type, status) VALUES (?, ?, ?, ?)"
            );
            insertPs.setString(1, this.customerEmail);
            insertPs.setString(2, providerEmail);
            insertPs.setString(3, serviceType);
            insertPs.setString(4, "Pending"); // Initial status

            insertPs.executeUpdate();

            JOptionPane.showMessageDialog(this,
                    "✅ Service booked with " + providerName + " successfully! Status: Pending");

        } catch(SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,"Error saving booking: "+e.getMessage());
        }
    }

    // Test standalone
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CustomerDashboard("test@example.com"));
    }
}