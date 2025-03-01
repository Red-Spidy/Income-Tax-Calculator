import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.awt.event.*;

public class IncomeTaxCalculator extends JFrame {
    static final String DB_URL = "jdbc:mysql://localhost:3306/taxdb";
    static final String USER = "root";
    static final String PASS = "Red_Spidy";

    JTextField nameField, incomeField, taxField;
    JButton calculateButton, saveButton, backButton, showRecordsButton;
    String regime;

    public IncomeTaxCalculator(String regime) {
        this.regime = regime;
        setTitle("Income Tax Calculator - " + regime + " Regime");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Title label at the top
        JLabel titleLabel = new JLabel("Income Tax Calculator - " + regime + " Regime", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        add(titleLabel, BorderLayout.NORTH);

        // Main form panel with GridBagLayout
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Name label and field
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1;
        nameField = new JTextField(20);
        formPanel.add(nameField, gbc);

        // Income label and field
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Income:"), gbc);
        gbc.gridx = 1;
        incomeField = new JTextField(20);
        formPanel.add(incomeField, gbc);

        // Tax label and field
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Tax:"), gbc);
        gbc.gridx = 1;
        taxField = new JTextField(20);
        taxField.setEditable(false);
        formPanel.add(taxField, gbc);

        add(formPanel, BorderLayout.CENTER);

        // Button panel with BoxLayout for responsiveness
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        calculateButton = new JButton("Calculate Tax");
        saveButton = new JButton("Save to DB");
        saveButton.setEnabled(false);

        backButton = new JButton("Back");
        showRecordsButton = new JButton("Show Records");

        buttonPanel.add(backButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        buttonPanel.add(calculateButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        buttonPanel.add(saveButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        buttonPanel.add(showRecordsButton);

        add(buttonPanel, BorderLayout.SOUTH);

        // Action listeners
        calculateButton.addActionListener(e -> calculateTax());
        saveButton.addActionListener(e -> saveToDatabase());
        backButton.addActionListener(e -> goBack());
        showRecordsButton.addActionListener(e -> showRecords());

        // Component listener for responsiveness
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                adjustComponentSizes();
            }
        });
    }

    private void calculateTax() {
        try {
            double income = Double.parseDouble(incomeField.getText());
            double tax = calculateIncomeTax(income);
            taxField.setText(String.format("%.2f", tax));
            saveButton.setEnabled(true);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid income.");
        }
    }

    private double calculateIncomeTax(double income) {
        double tax;
        if (regime.equals("Old")) {
            // Old regime tax calculation
            if (income <= 250000) tax = 0;
            else if (income <= 500000) tax = 0.05 * (income - 250000);
            else if (income <= 1000000) tax = 12500 + 0.2 * (income - 500000);
            else tax = 112500 + 0.3 * (income - 1000000);
        } else {
            // New regime tax calculation
            if (income <= 250000) tax = 0;
            else if (income <= 500000) tax = 0.05 * income;
            else if (income <= 1000000) tax = 25000 + 0.1 * (income - 500000);
            else tax = 75000 + 0.2 * (income - 1000000);
        }
        return tax;
    }

    private void saveToDatabase() {
        String tableName = regime.equals("Old") ? "old_regime" : "new_regime";
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO " + tableName + " (name, income, tax) VALUES (?, ?, ?)")) {
            stmt.setString(1, nameField.getText());
            stmt.setDouble(2, Double.parseDouble(incomeField.getText()));
            stmt.setDouble(3, Double.parseDouble(taxField.getText()));

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) JOptionPane.showMessageDialog(this, "Data saved successfully!");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error saving data: " + ex.getMessage());
        }
    }

    private void showRecords() {
        new RecordsDisplayWindow(regime).setVisible(true);
    }

    private void goBack() {
        new WelcomePage().setVisible(true);
        dispose();
    }

    private void adjustComponentSizes() {
        // Optional: Here, any resizing behavior for specific components can be added
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new WelcomePage().setVisible(true));
    }
}
