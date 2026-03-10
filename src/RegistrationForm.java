import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class RegistrationForm extends JFrame {
    private JTextField nameField, emailField, phoneField;
    private JPasswordField passwordField;
    private JComboBox<String> userTypeBox, serviceTypeBox;
    private JButton registerButton;

    public RegistrationForm() {
        setTitle("📝 Register for Service Finder");
        setSize(450, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Name
        gbc.gridx = 0; gbc.gridy = 0;
        add(new JLabel("Name:"), gbc);
        gbc.gridx = 1;
        nameField = new JTextField(20);
        add(nameField, gbc);

        // Email
        gbc.gridx = 0; gbc.gridy = 1;
        add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        emailField = new JTextField(20);
        add(emailField, gbc);

        // Password
        gbc.gridx = 0; gbc.gridy = 2;
        add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        passwordField = new JPasswordField(20);
        add(passwordField, gbc);

        // Phone
        gbc.gridx = 0; gbc.gridy = 3;
        add(new JLabel("Phone:"), gbc);
        gbc.gridx = 1;
        phoneField = new JTextField(20);
        add(phoneField, gbc);

        // User Type
        gbc.gridx = 0; gbc.gridy = 4;
        add(new JLabel("User Type:"), gbc);
        gbc.gridx = 1;
        userTypeBox = new JComboBox<>(new String[]{"Customer", "Service Provider"});
        add(userTypeBox, gbc);

        // Service Type (only for provider)
        gbc.gridx = 0; gbc.gridy = 5;
        add(new JLabel("Service Type:"), gbc);
        gbc.gridx = 1;
        serviceTypeBox = new JComboBox<>(new String[]{"Plumber","Electrician","Carpenter"});
        add(serviceTypeBox, gbc);

        // Show/hide serviceTypeBox based on user type
        userTypeBox.addActionListener(e -> {
            if(userTypeBox.getSelectedItem().equals("Customer"))
                serviceTypeBox.setEnabled(false);
            else
                serviceTypeBox.setEnabled(true);
        });
        serviceTypeBox.setEnabled(false);

        // Register Button
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        registerButton = new JButton("Register");
        registerButton.setBackground(new Color(76, 175, 80));
        registerButton.setForeground(Color.WHITE);
        add(registerButton, gbc);

        // Button Action
        registerButton.addActionListener(e -> registerUser());

        setVisible(true);
    }

    private void registerUser() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        String phone = phoneField.getText().trim();
        String userType = (String) userTypeBox.getSelectedItem();
        String serviceType = serviceTypeBox.getSelectedItem() != null ? (String) serviceTypeBox.getSelectedItem() : "";

        if(name.isEmpty() || email.isEmpty() || password.isEmpty() || phone.isEmpty() ||
                (userType.equals("Service Provider") && serviceType.isEmpty())) {
            JOptionPane.showMessageDialog(this,"Please fill all fields!");
            return;
        }

        String table = userType.equals("Customer") ? "customers" : "providers";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps;
            if(userType.equals("Customer")) {
                ps = conn.prepareStatement("INSERT INTO customers (name,email,password,phone) VALUES (?,?,?,?)");
                ps.setString(1,name);
                ps.setString(2,email);
                ps.setString(3,password);
                ps.setString(4,phone);
            } else {
                ps = conn.prepareStatement("INSERT INTO providers (name,email,password,service_type,phone) VALUES (?,?,?,?,?)");
                ps.setString(1,name);
                ps.setString(2,email);
                ps.setString(3,password);
                ps.setString(4,serviceType);
                ps.setString(5,phone);
            }

            ps.executeUpdate();
            JOptionPane.showMessageDialog(this,"✅ Registration Successful!");
            dispose();

        } catch(SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,"Error: "+e.getMessage());
        }
    }

    // Test Registration Form
    public static void main(String[] args) {
        SwingUtilities.invokeLater(RegistrationForm::new);
    }
}