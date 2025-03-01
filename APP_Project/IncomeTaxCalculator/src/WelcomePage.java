import javax.swing.*;
import java.awt.*;

public class WelcomePage extends JFrame {
    JButton newRegimeButton, oldRegimeButton;

    public WelcomePage() {
        // Frame setup
        setTitle("Welcome to Stocks Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 400);
        setLayout(new BorderLayout());

        // Welcome label
        JLabel welcomeLabel = new JLabel("Welcome to the Stocks Management System", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        add(welcomeLabel, BorderLayout.NORTH);

        // Image panel
        JPanel imagePanel = new JPanel(new GridBagLayout());
        ImageIcon icon = new ImageIcon("I:\\APP_Project\\IncomeTaxCalculator\\lib\\images.png"); // Replace with actual path
        JLabel imageLabel = new JLabel(icon);
        imagePanel.add(imageLabel);
        add(imagePanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // New Regime Button
        newRegimeButton = new JButton("New Regime Calculator");
        newRegimeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        buttonPanel.add(newRegimeButton);

        // Old Regime Button
        oldRegimeButton = new JButton("Old Regime Calculator");
        oldRegimeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        buttonPanel.add(oldRegimeButton);

        add(buttonPanel, BorderLayout.SOUTH);

        // Action listeners for regime selection
        newRegimeButton.addActionListener(e -> openIncomeTaxCalculator("New"));
        oldRegimeButton.addActionListener(e -> openIncomeTaxCalculator("Old"));
    }

    private void openIncomeTaxCalculator(String regime) {
        new IncomeTaxCalculator(regime).setVisible(true);
        dispose();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new WelcomePage().setVisible(true);
        });
    }
}
