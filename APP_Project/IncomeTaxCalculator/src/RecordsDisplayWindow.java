import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.sql.*;

public class RecordsDisplayWindow extends JFrame {
    static final String DB_URL = "jdbc:mysql://localhost:3306/taxdb";
    static final String USER = "root";
    static final String PASS = "Red_Spidy";

    public RecordsDisplayWindow(String regime) {
        setTitle("Records - " + regime + " Regime");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Title label
        JLabel titleLabel = new JLabel("Records - " + regime + " Regime", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        add(titleLabel, BorderLayout.NORTH);

        // Table to display data
        String[] columns = {"Name", "Income", "Tax"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Fetch records from database
        fetchRecords(regime, model);
    }

    private void fetchRecords(String regime, DefaultTableModel model) {
        String tableName = regime.equals("Old") ? "old_regime" : "new_regime";
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT name, income, tax FROM " + tableName)) {

            while (rs.next()) {
                String name = rs.getString("name");
                double income = rs.getDouble("income");
                double tax = rs.getDouble("tax");
                model.addRow(new Object[]{name, income, tax});
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error fetching records: " + ex.getMessage());
        }
    }
}
