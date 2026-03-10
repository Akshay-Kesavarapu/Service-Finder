import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class LoginForm extends JFrame {
    private JTextField emailField;
    private JPasswordField passwordField;
    private JComboBox<String> userTypeBox;
    private JButton loginButton, registerButton;

    public LoginForm() {
        setTitle("🔑 Service Finder Login");
        setSize(450, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10,10));

        // Header
        JLabel headerLabel = new JLabel("Login to Service Finder", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 22));
        headerLabel.setForeground(new Color(25, 118, 210));
        add(headerLabel, BorderLayout.NORTH);

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10,10,10,10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Email
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        emailField = new JTextField(20);
        formPanel.add(emailField, gbc);

        // Password
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        passwordField = new JPasswordField(20);
        formPanel.add(passwordField, gbc);

        // User Type
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("User Type:"), gbc);
        gbc.gridx = 1;
        userTypeBox = new JComboBox<>(new String[]{"Customer", "Service Provider"});
        formPanel.add(userTypeBox, gbc);

        // Login Button
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        loginButton = new JButton("Login");
        loginButton.setBackground(new Color(76, 175, 80));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFont(new Font("Arial", Font.BOLD, 16));
        formPanel.add(loginButton, gbc);

        // Register Button
        gbc.gridy = 4;
        registerButton = new JButton("Register");
        registerButton.setBackground(new Color(33,150,243));
        registerButton.setForeground(Color.WHITE);
        registerButton.setFont(new Font("Arial", Font.BOLD, 16));
        formPanel.add(registerButton, gbc);

        add(formPanel, BorderLayout.CENTER);

        // Button Actions
        loginButton.addActionListener(e -> loginUser());
        registerButton.addActionListener(e -> new RegistrationForm());

        setVisible(true);
    }

    private void loginUser() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        String userType = (String) userTypeBox.getSelectedItem();
        String table = userType.equals("Customer") ? "customers" : "providers";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT * FROM " + table + " WHERE email=? AND password=?"
            );
            ps.setString(1,email);
            ps.setString(2,password);
            ResultSet rs = ps.executeQuery();

            if(rs.next()) {
                JOptionPane.showMessageDialog(this,"Login Successful!");
                if(userType.equals("Customer")) {
                    new CustomerDashboard(email);
                } else {
                    new ProviderDashboard(email);
                }
                dispose(); // close login window
            } else {
                JOptionPane.showMessageDialog(this,"Invalid Email or Password!");
            }

        } catch(Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,"Error: "+ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoginForm::new);
    }
}