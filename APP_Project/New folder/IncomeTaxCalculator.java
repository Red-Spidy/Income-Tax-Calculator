import javax.swing.*;
import java.awt.event.*;
import java.sql.*;

public class IncomeTaxCalculator extends JFrame {
    // JDBC variables
    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost:3306/taxdb";
    static final String USER = "root";
    static final String PASS = "Red_Spidy";

    // SWIg components
    JTextField nameField, incomeField, taxField;
    JButton calculateButton, saveButton;

    public IncomeTaxCalculator() {
        // Frame setup
        setTitle("Income Tax Calculator");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);

        // Input fields and labels
        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setBounds(50, 50, 100, 30);
        add(nameLabel);

        nameField = new JTextField();
        nameField.setBounds(150, 50, 200, 30);
        add(nameField);

        JLabel incomeLabel = new JLabel("Income:");
        incomeLabel.setBounds(50, 100, 100, 30);
        add(incomeLabel);

        incomeField = new JTextField();
        incomeField.setBounds(150, 100, 200, 30);
        add(incomeField);

        JLabel taxLabel = new JLabel("Tax:");
        taxLabel.setBounds(50, 150, 100, 30);
        add(taxLabel);

        taxField = new JTextField();
        taxField.setBounds(150, 150, 200, 30);
        taxField.setEditable(false); // Tax field is read-only
        add(taxField);

        // Calculate button
        calculateButton = new JButton("Calculate Tax");
        calculateButton.setBounds(50, 200, 140, 30);
        add(calculateButton);

        // Save button
        saveButton = new JButton("Save to DB");
        saveButton.setBounds(210, 200, 140, 30);
        saveButton.setEnabled(false); // Initially disabled
        add(saveButton);

        // Action listener for tax calculation
        calculateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                calculateTax();
            }
        });

        // Action listener for saving to database
        saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveToDatabase();
            }
        });
    }

    // Method to calculate tax based on income
    private void calculateTax() {
        try {
            double income = Double.parseDouble(incomeField.getText());

            // Simple tax calculation 
            double tax;
            if (income <= 250000) {
                tax = 0;
            } else if (income <= 500000) {
                tax = 0.05 * (income - 250000);
            } else if (income <= 1000000) {
                tax = 12500 + 0.2 * (income - 500000);
            } else {
                tax = 112500 + 0.3 * (income - 1000000);
            }

            taxField.setText(String.format("%.2f", tax));
            saveButton.setEnabled(true); // Enable save button after calculation

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid income.");
        }
    }

    // Method to save user info to database
    private void saveToDatabase() {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            // Register JDBC driver and establish a connection
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, USER, PASS);

            // Prepare the SQL query
            String sql = "INSERT INTO users (name, income, tax) VALUES (?, ?, ?)";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, nameField.getText());
            stmt.setDouble(2, Double.parseDouble(incomeField.getText()));
            stmt.setDouble(3, Double.parseDouble(taxField.getText()));

            // Execute the query
            stmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Data saved successfully!");

        } catch (SQLException | ClassNotFoundException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saving data to database.");
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new IncomeTaxCalculator().setVisible(true);
        });
    }
}
